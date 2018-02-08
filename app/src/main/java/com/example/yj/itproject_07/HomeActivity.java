package com.example.yj.itproject_07;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.home);


        ImageView homeView = findViewById(R.id.homeview);
       // homeView.setBackgroundResource(R.drawable.home);


        //LinearLayout iv = new LinearLayout(this);
      //  iv.setBackgroundResource(R.drawable.background);
        homeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, CameraActivity.class);
                startActivity(i);
                finish();
            }
        });


       // Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
      //  homeView.startAnimation(animation);

    }


}
