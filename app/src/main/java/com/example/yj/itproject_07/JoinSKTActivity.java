package com.example.yj.itproject_07;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

public class JoinSKTActivity extends AppCompatActivity{
    String plan_String, gender_String, phone_String, extra_service_String;
    private EditText name_editText, age_editText, phone_number_editText,
            remain_editText, remain_month_editText, id_number_editText, address_editText,
            account_editText, bank_editText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.joinskt);

        String[] gender_str = getResources().getStringArray(R.array.gender_info);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(this, R.layout.spinneritem , gender_str);
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

        Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    // 가입 정보 서버로 보낸다.
                    postToServer("http://169.56.93.18:8080/user/join", MakeInfoJson());



                }catch (IOException e){
                    e.printStackTrace();
                }
                //new JSONTask().execute("http://169.56.93.18:8080/user/join");
            }
        });
        name_editText = findViewById(R.id.name_editText);
        age_editText = findViewById(R.id.age_editText);
        phone_number_editText = findViewById(R.id.phone_Number_editText);
        remain_editText = findViewById(R.id.remain_editText);
        remain_month_editText = findViewById(R.id.remiain_month_editText);
        id_number_editText = findViewById(R.id.id_number_editText);
        address_editText = findViewById(R.id.address_editText);
        account_editText = findViewById(R.id.account_editText);
        bank_editText = findViewById(R.id.bank_editText);
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
            jsonObject.put("remain", remain_editText.getText().toString());
            jsonObject.put("remain_month", remain_month_editText.getText().toString());
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
