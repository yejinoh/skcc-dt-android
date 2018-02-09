package com.example.yj.itproject_07;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public String phonenames[];

    List<Integer> listImages = new ArrayList<>();
    List<String> listPhoneNames = new ArrayList<>();
    List<String> listPlanNames = new ArrayList<>();
    List<String> listPlanCosts = new ArrayList<>();
    List<String> listPlanDatas = new ArrayList<>();
    List<String> listPlanCalls = new ArrayList<>();
    List<String> listPlanSmss = new ArrayList<>();

    private TextView comFee;
    private TextView insFee;
    private TextView totalFee;
    private TextView textViewChatContent;

    public static int phonePosition = 0;
    public static int planPosition = 0;

    MediaPlayer STTstart;
    MediaPlayer STTend;

    // IntentFilter 객체를 생성한다.
    IntentFilter filter = new IntentFilter();
    // TTS의 음성출력의 완료되는 동작에 대한 알림를 수신하기위해
    // IntentFilter 에 해당 동작을 추가한다.

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            textViewChatContent.setText(chatbotMessage);
        }
    };


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
                //Toast.makeText( getApplicationContext() , "TTS 음성 출력 완료", Toast.LENGTH_SHORT ).show();
                chatView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_main);

        phonenames = new String[4];

        STTstart = MediaPlayer.create(this, R.raw.start);
        STTend =MediaPlayer.create(this, R.raw.end);
        STTstart.setLooping(false);
        STTend.setLooping(false);

        textViewChatContent = findViewById(R.id.textViewChatContent);

        // textViewExpectCommunicationFee 통신 서비스
        // textViewExpectInstallment 월 할부금
        // textViewExpectTotalFee 토탈
        comFee = findViewById(R.id.textViewExpectCommunicationFee);
        insFee = findViewById(R.id.textViewExpectInstallment);
        totalFee = findViewById(R.id.textViewExpectTotalFee);

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
                    //sttText = "말씀해 주세요.";
                    //speakOut();
                    textViewChatContent.setText("");
                    STTstart.start();

                    //Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_LONG).show();
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
                    chatView.setVisibility(View.GONE);
                    check = false;
                }
            }
        });

        initData();

        HorizontalInfiniteCycleViewPager pagerPhone = (HorizontalInfiniteCycleViewPager) findViewById(R.id.horizontal_cycle_phone);
        ImageAdapter adapter = new ImageAdapter(listImages, getBaseContext());
        pagerPhone.setAdapter(adapter);




        //horizontal_cycle_plan
       // HorizontalInfiniteCycleViewPager horizontalCyclePlan = (View)findViewById(R.id.horizontal_cycle_phone);
        pagerPhone.setOnClickListener(new HorizontalInfiniteCycleViewPager.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Log.d()
            }
        });

        ImageView imageViewFake = (ImageView)findViewById(R.id.imageViewFake);
        imageViewFake.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DT","이미지뷰뷰뷰 : ");
                //Toast.makeText(getApplicationContext(), "폰 눌렀다", Toast.LENGTH_LONG).show();

                Intent i = new Intent(MainActivity.this, DetailActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                //finish();


            }
        });



        pagerPhone.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImageView imageViewBackground = (ImageView) findViewById(R.id.imageViewBackground);
                switch (position % 3) {
                    case 0:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_galaxy_a8);
                        break;
                    case 1:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_q6);
                        break;
                    case 2:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_x4);
                        break;
                }

                //Log.d("DT","onPageScrolled : " + Integer.toString(position % 3));
                phonePosition = (position % 3);
                SetTotal();

            }

            @Override
            public void onPageSelected(int position) {
                //Log.d("DT","onPageSelected : "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Toast.makeText(getApplicationContext(), state+"onPageScrollStateChanged", Toast.LENGTH_LONG).show();
                //Log.d("DT","onPageScrollStateChanged : "+state);
            }
        });

        // 버튼 클릭 리스너
        ShineButton shineButton = findViewById(R.id.po_image2);
        shineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mGotoJoinSKT(view);
            }
        });

        HorizontalInfiniteCycleViewPager pagerPlan = (HorizontalInfiniteCycleViewPager) findViewById(R.id.horizontal_cycle_plan);
        PlanAdapter adapterPlan = new PlanAdapter(listPlanNames, listPlanCosts, getBaseContext());
        pagerPlan.setAdapter(adapterPlan);
        pagerPlan.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //Log.d("Now Communication fee", Integer.toString(position % 3));
                planPosition = (position % 3);
                SetTotal();

            }

            @Override
            public void onPageSelected(int position) {
                //Log.d("Plan Select :: ","onPageSelected : " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Toast.makeText(getApplicationContext(), state+"onPageScrollStateChanged", Toast.LENGTH_LONG).show();
                //Log.d("DT","onPageScrollStateChanged : " + state);
            }
        });

    }

    private void SetTotal(){
        String pricePhone = Integer.toString((Integer.parseInt(GetRecommendData("phone",phonePosition,"price").split(",")[0]) / 24));
        String pricePlan = GetRecommendData("plan",planPosition,"month_pay");

        String priceTotal = Integer.toString((Integer.parseInt(pricePhone) + Integer.parseInt(pricePlan)));

        comFee.setText(AddComma(pricePlan));
        insFee.setText(AddComma(pricePhone));
        totalFee.setText(AddComma(priceTotal));

    }

    private String AddComma(String input){
        String output = new String();

        int end = input.length();
        int start = end - 3;

        while(true){
            if((end - 3) < 0){
                output = input.substring(0,end) + output;
                break;
            }
            if(start == 0) {
                output = input.substring(start, end) + output;
            }
            else{
                output = "," + input.substring(start, end) + output;
            }
            //System.out.println(input + " :: " + output);
            end -= 3;
            start -= 3;
        }
        //Log.d("Comma_output",output);
        return output;
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

        for(int i=0;i<5;i++) {
            if(i<3) {
                listPhoneNames.add(GetRecommendData("phone", i, "name"));
                String phone = listPhoneNames.get(i);
                Log.d(listPhoneNames.get(i), "phone name" + i );
                listPlanNames.add(GetRecommendData("plan", i, "name"));
                String temp = GetRecommendData("plan", i, "month_pay");
                System.out.println(temp);
                String month = "월 " + temp.substring(0,2) + "," + temp.substring(2,5) + "원";
                listPlanCosts.add(month);
                listPlanDatas.add(GetRecommendData("plan",i,"data"));
                listPlanCalls.add(GetRecommendData("plan", i, "call"));
                listPlanSmss.add(GetRecommendData("plan", i, "sms"));


                if(listPhoneNames.get(i).equals("Galaxy A8"))
                    listImages.add(R.drawable.galaxy_a8);
                else if(listPhoneNames.get(i).equals("Galaxy A7") )
                    listImages.add(R.drawable.galaxy_a7);
                else if(listPhoneNames.get(i).equals("Galaxy S8"))
                    listImages.add(R.drawable.galaxy_s8);
                else if(listPhoneNames.get(i).equals("iPhone 8"))
                    listImages.add(R.drawable.iphone_8);
                else if(listPhoneNames.get(i).equals("X4+"))
                    listImages.add(R.drawable.x4);
                else if(listPhoneNames.get(i).equals("iPhone X"))
                    listImages.add(R.drawable.iphone_10);
                else if(listPhoneNames.get(i).equals("V30"))
                    listImages.add(R.drawable.lg_v30);
                else if(listPhoneNames.get(i).equals("Q6"))
                    listImages.add(R.drawable.q6);
                else if(listPhoneNames.get(i).equals("XPERIA XZ1"))
                    listImages.add(R.drawable.xperia_xz1);





            }

        }







    }

    private void ConversationRequest(){
        // Chatbot Part
        final Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
        service.setEndPoint("https://gateway.aibril-watson.kr/conversation/api");
        //service.setUsernameAndPassword("117c0fbf-c09c-4c86-8003-23c10b87b407","AUvnd2olbbAB" ); //지영
        service.setUsernameAndPassword("ec82b488-382f-4e57-9029-4cf518331c58", "RpG2PAivzMmh");

        //String workspaceId = "e880c113-1ccc-4c1e-aee7-423ed65923ff"; // 지영
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
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                            //System.out.println(response);
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
            //System.out.println(speechResults);
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                //sttText = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();

                sttText = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                //System.out.println(sttText);

                if(speechResults.getResults().get(0).isFinal()){
                    STTend.start();
                    if(sttText.contains("재고") || sttText.contains("제 고") || sttText.contains("제거")){
                        sttText = GetRecommendData("phone",phonePosition,"name") + " " + sttText;
                    }
                    else if(sttText.contains("부가")){
                        sttText = GetRecommendData("plan",planPosition,"name") + " " + sttText;
                    }
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
            //Toast.makeText(getApplicationContext(), "STT Disconnect!", Toast.LENGTH_LONG).show();
            //enableMicButton();
        }
    }


    public void mGotoJoinSKT(View view)
    {
        Intent intent = new Intent(view.getContext(), JoinSKTActivity.class);
        Toast.makeText(getApplicationContext(), "GaJuha!", Toast.LENGTH_LONG).show();
        view.getContext().startActivity(intent);
    }
}
