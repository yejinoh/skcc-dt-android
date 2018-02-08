package com.example.yj.itproject_07;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by YJ on 2018-02-08.
 */

public class GetDetails {

    public void GetPhoneDetail(String name){
        final OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://169.56.93.18:8080/get/detail/phone").newBuilder();
        urlBuilder.addQueryParameter("name",name);

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    //Log.d("", response.body().string());

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
                                //MainActivity.phone_details = responseData;
                                System.out.println(responseData);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void GetPlanDetail(String name){
        final OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://169.56.93.18:8080/get/detail/plan").newBuilder();
        urlBuilder.addQueryParameter("name",name);

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    //Log.d("", response.body().string());

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

                                try{
                                    JSONObject obj = new JSONObject(responseData);
                                    JSONObject data = obj.getJSONObject("data");
                                    //System.out.println("obj test :: " + data.getString("name"));
                                    //MainActivity.plan_details.add(data);


                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                //System.out.println(responseData);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }
}
