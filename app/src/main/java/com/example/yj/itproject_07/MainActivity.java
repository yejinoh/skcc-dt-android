package com.example.yj.itproject_07;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.conversation.v1.*;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    boolean check = false;
    private FloatingActionButton fab;
    public RelativeLayout chatView;
    public RelativeLayout chat;

    public static String recommends;

    public JSONArray phone;
    public JSONArray plan;

    private SpeechToText speechService;
    private MicrophoneHelper microphoneHelper;
    private MicrophoneInputStream capture;

    private String sttText = new String();

    public boolean flag = false;
    public String chatbotMessage = new String();
    public TextToSpeech tts;

    List<Integer> listImages = new ArrayList<>();
    List<String> listPlanNames = new ArrayList<>();
    List<String> listPlanCosts = new ArrayList<>();
    List<String> listPlanDatas = new ArrayList<>();
    List<String> listPlanCalls = new ArrayList<>();
    List<String> listPlanSmss = new ArrayList<>();

    // IntentFilter 객체를 생성한다.
    IntentFilter filter = new IntentFilter();
    // TTS의 음성출력의 완료되는 동작에 대한 알림를 수신하기위해
    // IntentFilter 에 해당 동작을 추가한다.

    BroadcastReceiver m_br = new BroadcastReceiver() {

        // 브로드캐스트 알림이 수신되면 호출되는 onReceive 메소드를 정의한다.
        public void onReceive(Context context, Intent intent)
        {
            // Intent 로부터 어떤 동작으로 인해 Broadcast Receiver 에게 알림이
            // 수신되었는지에 대한 정보를 가져온다.
            String act = intent.getAction();

            // TTS의 음성출력이 완료되어 알림이 수신된 경우
            if(act.equals(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)) {

                // 토스트 메시지를 통해 TTS 의 음성출력이 완료되었음을 사용자에게 알린다.
                Toast.makeText( getApplicationContext() , "TTS 음성 출력 완료", Toast.LENGTH_SHORT ).show();
                chatView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);
        microphoneHelper = new MicrophoneHelper(this);
        speechService = initSpeechToTextService();

        filter.addAction(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);

        chatView = findViewById(R.id.relativeLayoutChat);
        chatView.setVisibility(View.INVISIBLE);

        //Toast.makeText(getApplicationContext(), recommends, Toast.LENGTH_LONG).show();
        this.parseRecommends();

        //System.out.println("teste!!!!! : "+this.GetDetailPlan(0,"name"));

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check)
                {
                    Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_LONG).show();
                    chatView.setVisibility(View.VISIBLE);
                    check = true;

                    capture = microphoneHelper.getInputStream(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                speechService.recognizeUsingWebSocket(capture, getRecognizeOptions(),
                                        new MicrophoneRecognizeDelegate());
                            } catch (Exception e) {
                                showError(e);
                            }
                        }
                    }).start();


                }
                else if(check){

                    microphoneHelper.closeInputStream();
                    /*
                    ConversationRequest();

                    while(true){
                        if(flag == true) {
                            flag = false;
                            break;
                        }
                    }
                    System.out.println("chatbot OK");
                    speakOut();
                    */
                    //Toast.makeText(getApplicationContext(), "unclick", Toast.LENGTH_LONG).show();
                    chatView.setVisibility(View.GONE);
                    check = false;
                }
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();

            }
        });

        initData();

        HorizontalInfiniteCycleViewPager pagerPhone = (HorizontalInfiniteCycleViewPager) findViewById(R.id.horizontal_cycle_phone);
        ImageAdapter adapter = new ImageAdapter(listImages, getBaseContext());
        pagerPhone.setAdapter(adapter);

        HorizontalInfiniteCycleViewPager pagerPlan = (HorizontalInfiniteCycleViewPager) findViewById(R.id.horizontal_cycle_plan);
        PlanAdapter adapterPlan = new PlanAdapter(listPlanNames, listPlanCosts, getBaseContext());
        pagerPlan.setAdapter(adapterPlan);

        pagerPhone.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImageView imageViewBackground = (ImageView) findViewById(R.id.imageViewBackground);
                switch (position%3) {
                    case 0:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_iphone_10);
                        break;
                    case 1:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_galaxy_s8);
                        break;
                    case 2:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_lg_v30);
                        break;
                }

                Log.d("DT","onPageScrolled : "+position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("DT","onPageSelected : "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Toast.makeText(getApplicationContext(), state+"onPageScrollStateChanged", Toast.LENGTH_LONG).show();
                Log.d("DT","onPageScrollStateChanged : "+state);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseRecommends(){
        try
        {
            JSONObject obj = new JSONObject(recommends);
            JSONObject data = obj.getJSONObject("data");
            phone = data.getJSONArray("phone");
            plan = data.getJSONArray("plan");

            //System.out.println(plan.getJSONObject(0).getString("name"));
            //System.out.println(phone);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private String GetRecommendData(String dataType, int index, String name){
        try
        {
            if(dataType.equals("plan")){
                return plan.getJSONObject(index).getString(name);
            }
            else if(dataType.equals("phone")){
                return phone.getJSONObject(index).getString(name);
            }

            else{
                return null;
            }
            //System.out.println(plan.getJSONObject(0).getString("name"));
            //System.out.println(phone);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void initData(){
        // 핸드폰 사진 추가하기 위해서 Drawable의 사진 파일 불러옴.
        listImages.add(R.drawable.iphone_10);
        listImages.add(R.drawable.galaxy_s8);
        listImages.add(R.drawable.lg_v30);

        for(int i=0;i<5;i++) {
            if(i<3) {
                listPlanNames.add(GetRecommendData("plan", i, "name"));
                listPlanCosts.add(GetRecommendData("plan", i, "month_pay"));
                listPlanDatas.add(GetRecommendData("plan",i,"data"));
                listPlanCalls.add(GetRecommendData("plan", i, "call"));
                listPlanSmss.add(GetRecommendData("plan", i, "sms"));
            }

        }

    }

    private void ConversationRequest(){
        // Chatbot Part
        final Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
        service.setEndPoint("https://gateway.aibril-watson.kr/conversation/api");
        service.setUsernameAndPassword("ec82b488-382f-4e57-9029-4cf518331c58", "RpG2PAivzMmh");

        String workspaceId = "122eb3c2-66eb-4307-9a28-b096a18f46ae";

        InputData input = new InputData.Builder(sttText).build();

        final MessageOptions options = new MessageOptions.Builder(workspaceId)
                .input(input)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                service.message(options).enqueue(new ServiceCallback<MessageResponse>() {
                    @Override
                    public void onResponse(MessageResponse response) {
                        try{
                            JSONObject ConversationResult = new JSONObject(response);
                            JSONObject output = ConversationResult.getJSONObject("output");
                            String outString = output.getString("text");
                            chatbotMessage = outString.substring(2, outString.length() - 2);
                            System.out.println(chatbotMessage);
                            System.out.println(response);
                            speakOut();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        }).start();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.KOREA);

            // tts.setPitch(5); // set pitch level

            // tts.setSpeechRate(2); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {

            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }

    }

    private void speakOut() {
        registerReceiver(m_br, filter);
        tts.speak(chatbotMessage, TextToSpeech.QUEUE_FLUSH, null, null);
        System.out.println("Response of : " + sttText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
    }

    private SpeechToText initSpeechToTextService() {
        SpeechToText service = new SpeechToText();
        String username = "10d4c053-7d76-4a32-bc5b-de1ca03b3d93";
        String password = "mjOZInyCI6cd";
        service.setUsernameAndPassword(username, password);
        service.setEndPoint("https://stream.aibril-watson.kr/speech-to-text/api");
        return service;
    }

    private RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder().contentType(ContentType.OPUS.toString())
                .model("ko-KR_NarrowbandModel").interimResults(true).inactivityTimeout(2000).build();
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //e.printStackTrace();
            }
        });
    }
    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {

        @Override
        public void onTranscription(SpeechResults speechResults) {
            System.out.println(speechResults);
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                //sttText = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();

                sttText = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                //System.out.println(sttText);

                if(speechResults.getResults().get(0).isFinal()){
                    System.out.println("Final Text :: " + sttText);
                    microphoneHelper.closeInputStream();
                    check = false;
                    ConversationRequest();
                }
            }
        }

        @Override
        public void onError(Exception e) {
            showError(e);
            //enableMicButton();
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(getApplicationContext(), "STT Disconnect!", Toast.LENGTH_LONG).show();
            //enableMicButton();
        }
    }
}
