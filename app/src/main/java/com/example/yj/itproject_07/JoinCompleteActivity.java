package com.example.yj.itproject_07;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2018-02-09.
 */
public class JoinCompleteActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_join);

        finishTimer();
    }

    private void finishTimer()
    {
        Handler handler =    new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                finish();    // 액티비티 종료

                Intent i = new Intent(JoinCompleteActivity.this, HomeActivity.class);
                startActivity(i);
            }
        };

        handler.sendEmptyMessageDelayed(0, 3000);    // ms, 3초후 종료시킴
    }
}