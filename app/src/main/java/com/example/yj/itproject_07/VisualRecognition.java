package com.example.yj.itproject_07;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by YJ on 2018-02-07.
 */

public class VisualRecognition {
    private String filepath = new String();
    private String vr_response = new String();
    private String recommend = new String();

    public VisualRecognition(String filepath){
        this.filepath = filepath;
    }

    public void RunVR(){

        final OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://gateway-a.watsonplatform.net/visual-recognition/api/v3/detect_faces").newBuilder();
        //urlBuilder.addQueryParameter("api_key", "8245a9e42ce50c65d949cfd0fa06f1c1d58804e8"); // 지영
        urlBuilder.addQueryParameter("api_key", "5c8c72a78210e19fe8c8b21b8a8bff6a9e317293"); // 세건
        urlBuilder.addQueryParameter("version","2016-05-20");
        String url = urlBuilder.build().toString();

        File file = new File(this.filepath);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if(file.exists()) {
            final MediaType MEDIA_TYPE = MediaType.parse("image/jpg");
            builder.addFormDataPart("F",file.getName(), RequestBody.create(MEDIA_TYPE,file));
            System.out.println(filepath);
        }
        else {
            System.out.println("file not exist");
        }
        final RequestBody requestBody = builder.build();

        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
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
                            vr_response = response.body().string();
                            //Log.d("", responseData);
                            //System.out.println(vr_response);
                            postToServer("http://169.56.93.18:8080/recommand",vr_response);
                        }
                    }
                });
            }

        }).start();
    }

    private void postToServer(String ServerURL, final String vrResponse) throws IOException {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        System.out.println(vr_response);

        File del = new File(this.filepath);
        if(del.delete())
            System.out.println("파일 삭세 성공");
        else
            System.out.println("파일 삭제 실패");


        RequestBody body = RequestBody.create(JSON, vrResponse);
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
                            recommend = responseData;
                            System.out.println(recommend);
                            MainActivity.recommends = recommend;
                            ((CameraActivity)CameraActivity.mContext).turn();

                        }
                    }
                });
            }

        }).start();
    }
}
