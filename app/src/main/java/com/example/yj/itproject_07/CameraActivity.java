package com.example.yj.itproject_07;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity {
    private FaceDetector mFaceDetector;
    private CameraSource mCameraSource = null;
    private CameraSurfacePreview mPreview;
    private SurfaceHolder mSurfaceHolder;
    private CameraOverlay cameraOverlay;
    private volatile Face mFace;
    private boolean check = false;
    private CameraSurfacePreview surfaceView;
    private String path;
    public static Context mContext;
    private TimerTask timerTask;
    private TimerTask timerTask2;
    static int width;
    static int height;
    private int count = 3;
    private boolean isAccept = false;
    private boolean ssg = false;

    private TextView countTextView;
    private ImageView imageViewLoading;

    MediaPlayer countSound;

    @Override
    protected void onResume(){
        super.onResume();
        startCameraSource();
    }

    private void requestPermission()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 권한받기
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, 200);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.camera);

        countTextView = findViewById(R.id.countTextView);

        imageViewLoading = (ImageView) findViewById(R.id.imageViewLoading);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(imageViewLoading);
        Glide.with(this).load(R.drawable.loading_red).into(gifImage);
        imageViewLoading.setVisibility(View.INVISIBLE);



       requestPermission();
        mPreview = findViewById(R.id.preview_camera);
        cameraOverlay = findViewById(R.id.faceOverlay_camera);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            createCameraSource();
        }

        countSound = MediaPlayer.create(this, R.raw.timer_count);
        countSound.setLooping(false);

        RelativeLayout buttonAccept=(RelativeLayout)findViewById(R.id.relativeLayoutAccept);
        buttonAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "동의", Toast.LENGTH_LONG).show();
                isAccept = true;

                // 사진 촬영 동의 누를 경우 사라진다.
                RelativeLayout relativeLayoutPermission=(RelativeLayout)findViewById(R.id.relativeLayoutPermission);
                relativeLayoutPermission.setVisibility(View.INVISIBLE);

                // 얼굴인식 시작(3,2,1)
                countSound.start();
                new CountDownTimer(2300, 550) {
                    public void onTick(long millisUntilFinished) {
                        if(count == 0){
                            countTextView.setText("Cheese !!");
                        }
                        else {
                            countTextView.setText(String.valueOf(count));
                        }
                        count--;
                    }

                    public void onFinish() {
                        countTextView.setVisibility(View.INVISIBLE);
                        ssg=true;
                    }

                }.start();


            }
        });

        RelativeLayout buttonDeny=(RelativeLayout)findViewById(R.id.relativeLayoutDeny);
        buttonDeny.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "싫어", Toast.LENGTH_LONG).show();

                Intent i = new Intent(CameraActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });



    }

    private void createCameraSource(){
        Context context = getApplicationContext();
        mFaceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        mFaceDetector.setProcessor(
                new MultiProcessor.Builder<>(new CameraActivity.GraphicFaceTrackerFactory()).build()
        );

        if(!mFaceDetector.isOperational()){
            Log.e("SSG", "Face detector dependencies are not yet available.");
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        mCameraSource = new CameraSource.Builder(context, mFaceDetector)
                .setRequestedPreviewSize(640, 480)
                .setRequestedPreviewSize(width, height)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    private void startCameraSource(){
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, 9001);
            dlg.show();
        }
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, null);
            } catch (IOException e) {
                Log.e("ssg", "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new CameraActivity.GraphicFaceTracker(cameraOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private CameraOverlay mOverlay;

        GraphicFaceTracker(CameraOverlay overlay) {
            mOverlay = overlay;
        }
        @Override
        public void onNewItem(int faceId, Face item) {
            if(!ssg || check || !isAccept) return;
            mOverlay = cameraOverlay;
            takePicture();
            check = true;
        }
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face){
            if(!ssg || check || !isAccept) return;
            takePicture();
            check = true;
        }
    }

    public void takePicture() {
        mCameraSource.takePicture(null, new CameraSource.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] bytes) {
                new SaveImage().execute(bytes);

                // 로딩 돌아가기 시작
                imageViewLoading.setVisibility(View.VISIBLE);

            }
        });
    }

    public void turn(){
        Intent i = new Intent(CameraActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    private class SaveImage extends AsyncTask<byte[], Void, File> {

        @Override
        protected File doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/SK_CNC");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());

                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                path = new String(sdCard.getAbsolutePath() + "/SK_CNC/" + fileName);

                /*---------------------------------------*/
                Bitmap srcBmp = BitmapFactory.decodeFile(path);

                int iWidth   = 1366;   // 축소시킬 너비
                int iHeight  = 768;   // 축소시킬 높이
                float fWidth  = srcBmp.getWidth();
                float fHeight = srcBmp.getHeight();

                // 원하는 널이보다 클 경우의 설정
                if(fWidth > iWidth) {
                    float mWidth = (float) (fWidth / 100);
                    float fScale = (float) (iWidth / mWidth);
                    fWidth *= (fScale / 100);
                    fHeight *= (fScale / 100);

                // 원하는 높이보다 클 경우의 설정
                }else if (fHeight > iHeight) {
                    float mHeight = (float) (fHeight / 100);
                    float fScale = (float) (iHeight / mHeight);
                    fWidth *= (fScale / 100);
                    fHeight *= (fScale / 100);
                }

                FileOutputStream fosObj = null;
                try {
                    // 리사이즈 이미지 동일파일명 덮어 쒸우기 작업
                    Bitmap resizedBmp = Bitmap.createScaledBitmap(srcBmp, (int)fWidth, (int)fHeight, true);
                    fosObj = new FileOutputStream(path);
                    resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, fosObj);

                } catch (Exception e){

                } finally {
                    fosObj.flush();
                    fosObj.close();
                }

                // 저장된 이미지를 스트림으로 불러오기
                //File pathFile = new File(url);
                //FileInputStream fisObj = new FileInputStream(pathFile);
                /*-------------------------------------*/

                VisualRecognition vr = new VisualRecognition(path);
                vr.RunVR();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outFile));
                sendBroadcast(mediaScanIntent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }
    }
}
