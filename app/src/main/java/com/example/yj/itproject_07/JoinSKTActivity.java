package com.example.yj.itproject_07;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by YJ on 2018-02-09.
 */

public class JoinSKTActivity extends Activity{
    private EditText name_editText, age_editText, phone_number_editText,
            id_number_editText, address_editText, account_editText, bank_editText;
    public static String phone_String, plan_String, gender_String, extra_service_String, join_type_String;
    public static boolean flag = false;
    public static boolean re_flag = true;
    int signviewHight;
    int signviewWidth;
    LinearLayout linearLayout;
    InputMethodManager imm;
    Bitmap bitmap;
    public ImageView signview;
    public TextView sign;

    private FloatingActionButton fab1;

    private void SetRecommend(Spinner targetSpinner, String[] targetArray , String targetString){
        for(int i=0;i<targetArray.length;i++){
            if(targetArray[i].equals(targetString)){
                targetSpinner.setSelection(i);
                System.out.println(targetString + " ::: " + Integer.toString(i));
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.joinskt);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        String[] gender_str = getResources().getStringArray(R.array.gender_info);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(this, R.layout.spinneritem , gender_str);

        fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if(flag == false)
                    flag = true;
                else
                    flag = false;

                MainActivity.fab.callOnClick();
            }

        });

        final Spinner gender_spinner = findViewById(R.id.spinner_gender);
        gender_spinner.setAdapter(gender_adapter);

        gender_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(gender_spinner.getSelectedItemPosition() >= 0){
                            gender_String = parent.getItemAtPosition(position).toString();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );

        String[] plan_str = getResources().getStringArray(R.array.plan_info);
        ArrayAdapter<String> plan_adapter = new ArrayAdapter<String>(this, R.layout.spinneritem , plan_str);
        final Spinner plan_spinner = findViewById(R.id.spinner_plan);
        plan_spinner.setAdapter(plan_adapter);
        plan_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(plan_spinner.getSelectedItemPosition() >= 0){
                            plan_String = parent.getItemAtPosition(position).toString();
                            System.out.println(plan_String);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );

        String[] phone_str = getResources().getStringArray(R.array.phone_info);
        ArrayAdapter<String> phone_adapter = new ArrayAdapter<String>(this, R.layout.spinneritem , phone_str);
        final Spinner phone_spinner = findViewById(R.id.spinner_phone);
        phone_spinner.setAdapter(phone_adapter);
        phone_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(phone_spinner.getSelectedItemPosition() >= 0){
                            phone_String = parent.getItemAtPosition(position).toString();
                            System.out.println(phone_String);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );

        String[] join_type_str = getResources().getStringArray(R.array.join_type_info);
        ArrayAdapter<String> join_type_adapter = new ArrayAdapter<String>(this, R.layout.spinneritem , join_type_str);
        final Spinner join_type_spinner = findViewById(R.id.spinner_join_type);
        join_type_spinner.setAdapter(join_type_adapter);
        join_type_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(join_type_spinner.getSelectedItemPosition() >= 0){
                            join_type_String = parent.getItemAtPosition(position).toString();
                            System.out.println(join_type_String);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );

        String[] extraService_str = getResources().getStringArray(R.array.extra_service_info);
        ArrayAdapter<String> extraService_adapter = new ArrayAdapter<String>(this, R.layout.spinneritem , extraService_str);
        final Spinner extraService_spinner = findViewById(R.id.spinner_extraService);
        extraService_spinner.setAdapter(extraService_adapter);
        extraService_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(extraService_spinner.getSelectedItemPosition() >= 0){
                            extra_service_String = parent.getItemAtPosition(position).toString();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );

        SetRecommend(gender_spinner, gender_str, gender_String);
        if(re_flag) {
            SetRecommend(plan_spinner, plan_str, plan_String);
            SetRecommend(phone_spinner, phone_str, phone_String);
        }
        else{
            plan_spinner.setSelection(0);
            phone_spinner.setSelection(0);
        }

        ImageButton button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    // 가입 정보 서버로 보낸다.
                    postToServer("http://169.56.93.18:8080/user/join", MakeInfoJson());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        sign = findViewById(R.id.signtext);
        signview = findViewById(R.id.signview);
        signview.buildDrawingCache();
        signview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(JoinSKTActivity.this, main.class);
                startActivityForResult(intent, 1);
            }
        });
        ImageButton button_back = findViewById(R.id.button2);
        button_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        name_editText = findViewById(R.id.name_editText);
        age_editText = findViewById(R.id.age_editText);
        phone_number_editText = findViewById(R.id.phone_Number_editText);
        id_number_editText = findViewById(R.id.id_number_editText);
        address_editText = findViewById(R.id.address_editText);
        account_editText = findViewById(R.id.account_editText);
        bank_editText = findViewById(R.id.bank_editText);
        //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)signview.getLayoutParams();

        linearLayout = findViewById(R.id.linearlayout1);
        linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(name_editText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(age_editText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(phone_number_editText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(id_number_editText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(address_editText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(account_editText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(extraService_spinner.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(gender_spinner.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(plan_spinner.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(join_type_spinner.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(phone_spinner.getWindowToken(), 0);
            }
        });
//        if(getIntent().hasExtra("byteArray")) {
//
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            bitmap = BitmapFactory.decodeByteArray(
                    data.getByteArrayExtra("byteArray"), 0,
                    data.getByteArrayExtra("byteArray").length);
            bitmap.setHasAlpha(true); // alpha값 생성
            bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

            sign.setVisibility(View.INVISIBLE);
            signview.setImageBitmap(bitmap);
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        View signview = this.findViewById(R.id.signview);

        signviewWidth = signview.getWidth();
        signviewHight = signview.getHeight();

        Log.w("Layout Width - ", String.valueOf(signview.getWidth()));
        Log.w("Layout Height - ", String.valueOf(signview.getHeight()));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    private final String MakeInfoJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name_editText.getText().toString());
            jsonObject.put("age", age_editText.getText().toString());
            jsonObject.put("sex", gender_String);
            jsonObject.put("phone_number", phone_number_editText.getText().toString());
            jsonObject.put("plan", plan_String);
            jsonObject.put("phone", phone_String);
            //jsonObject.put("remain", remain_editText.getText().toString());
            //jsonObject.put("remain_month", remain_month_editText.getText().toString());
            jsonObject.put("id_number", id_number_editText.getText().toString());
            jsonObject.put("address", address_editText.getText().toString());
            jsonObject.put("account", account_editText.getText().toString());
            jsonObject.put("bank", bank_editText.getText().toString());
            jsonObject.put("extra_service", extra_service_String);

            final String info = jsonObject.toString();
            return info;

        } catch(JSONException e1){
            e1.printStackTrace();
            return null;
        }
    }

    private void postToServer(String ServerURL, final String info) throws IOException {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        System.out.println(info);

        RequestBody body = RequestBody.create(JSON, info);
        final Request request = new Request.Builder()
                .url(ServerURL)
                .post(body)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            final String responseData = response.body().string();
                            System.out.println(responseData);

                            // 가입 완료 화면 띄워준다.
                            Intent i = new Intent(JoinSKTActivity.this, JoinCompleteActivity.class);
                            startActivity(i);



                        }
                    }
                });
            }

        }).start();
    }
}
