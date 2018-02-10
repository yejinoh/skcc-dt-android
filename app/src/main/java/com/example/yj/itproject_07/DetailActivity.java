package com.example.yj.itproject_07;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2018-02-09.
 */

public class DetailActivity extends AppCompatActivity{
    public static String name,company,storage,color,price;

//    textViewPhoneNameReal
  //          textViewPhoneCompanyReal
    //textViewPhoneStorageReal
      //      textViewPhoneColorReal
    //textViewPhonePriceReal

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail);

        TextView nameview = findViewById(R.id.textViewPhoneNameReal);
        TextView storageview = findViewById(R.id.textViewPhoneStorageReal);
        TextView companyview = findViewById(R.id.textViewPhoneComapanyReal);
        TextView colorview = findViewById(R.id.textViewPhoneColorReal);
        TextView priceview = findViewById(R.id.textViewPhonePriceReal);

        nameview.setText(name);
        storageview.setText(storage);
        companyview.setText(company);
        colorview.setText(color);
        priceview.setText(price);

        ImageView buttonExit = (ImageView)findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "뒤로가기", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
