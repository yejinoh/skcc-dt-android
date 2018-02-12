package com.example.yj.itproject_07;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;

public class main extends Activity {

    private ImageView mPenBtn;
    private ImageView mEraserBtn;
    private ImageView editBtn;
    private Uri uri;


    public static final String SETTING_PREFERENCE = "my_setting";
    public static final String KEY_MY_PENCOLOR = "my_color";
    public static final String KEY_MY_PENSIZE = "my_size";

    public static boolean boardOpen = false;

    public static PaintBoard     board;   //보드
    public static FrameLayout frameLayout;

    public static RelativeLayout container;  //스크린샷을 위한 레이아웃
    public static int mColor = Color.BLACK;    //현재 펜 칼라
    public static int mSize = 10;             //현재 펜 사이즈
    public static int eraseSize = 50;        //지우개 사이즈

    public int tempColor = Color.RED;

    boolean penSelected = true;     //펜 선택시
    boolean eraserSelected = false; //지우개 선택시


    public static boolean isOpen = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActivityManager.getInstance().addActivity(this);

        SharedPreferences prefs = getSharedPreferences(SETTING_PREFERENCE, MODE_PRIVATE);
        mColor = prefs.getInt(KEY_MY_PENCOLOR, mColor);
        mSize = prefs.getInt(KEY_MY_PENSIZE, mSize);

        final RelativeLayout boardLayout = (RelativeLayout) findViewById(R.id.spenViewLayout);   //보드 레이아웃

        frameLayout = (FrameLayout) findViewById(R.id.spenViewContainer);
        container = (RelativeLayout) findViewById(R.id.container);

        mEraserBtn = findViewById(R.id.eraserBtn);
        mEraserBtn.setOnClickListener(mEraserBtnClickListener);

        editBtn = (ImageView)findViewById(R.id.edit_exit);
        editBtn.setOnClickListener(editClickListener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        board = new PaintBoard(this);   //보드판생성
        board.setBackgroundColor(Color.WHITE);
        board.setLayoutParams(params);  //레이아웃파라미터
        board.setPadding(2, 2, 2, 2);   //여백생성

        boardLayout.addView(board);     //보드레이아웃에 보드뷰추가

        //앱을 켰을 때 펜모드로 초기화
        board.eMode(true);
        penSelected = true;
        eraserSelected = false;
        boardOpen = true;
        //initResource();

    }



        private final View.OnClickListener mEraserBtnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.board.onClickAllErase();
            }
        };

    private final View.OnClickListener editClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(main.this, JoinSKTActivity.class);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            board.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bs);
            intent.putExtra("byteArray", bs.toByteArray());
            setResult(RESULT_OK, intent);
//            startActivity(intent);
            finish();
        }

    };




//    private void initResource(){
//   if(penSelected){
//       mPenBtn.setImageResource(R.drawable.changecolor2);
//       mEraserBtn.setImageResource(R.drawable.eraser4);
//
//   }
//    else if(eraserSelected){
//        mPenBtn.setImageResource(R.drawable.changecolor);
//        mEraserBtn.setImageResource(R.drawable.eraser2);
//
//    }
//
//    else
//   {
//       Log.d("init", "else!");
//   }
//
//}


}

