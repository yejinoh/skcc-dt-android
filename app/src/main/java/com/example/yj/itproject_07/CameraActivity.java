package com.example.yj.itproject_07;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

    private boolean isAccept = false;

    private ImageView imageViewLoading;

    @Override
    protected void onResume(){
        super.onResume();
        startCameraSource();
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA},200);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.camera);


        imageViewLoading = (ImageView) findViewById(R.id.imageViewLoading);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(imageViewLoading);
        Glide.with(this).load(R.drawable.loading_red).into(gifImage);
        imageViewLoading.setVisibility(View.INVISIBLE);



        requestPermission();
        mPreview = findViewById(R.id.preview_camera);
        cameraOverlay = findViewById(R.id.faceOverlay_camera);
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(rc == PackageManager.PERMISSION_GRANTED)
        {
            createCameraSource();
        }

        RelativeLayout buttonAccept=(RelativeLayout)findViewById(R.id.relativeLayoutAccept);
        buttonAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "동의", Toast.LENGTH_LONG).show();
                isAccept = true;

                // 사진 촬영 동의 누를 경우 사라진다.
                RelativeLayout relativeLayoutPermission=(RelativeLayout)findViewById(R.id.relativeLayoutPermission);
                relativeLayoutPermission.setVisibility(View.INVISIBLE);

                // 얼굴인식 시작(3,2,1)


            }
        });

        RelativeLayout buttonDeny=(RelativeLayout)findViewById(R.id.relativeLayoutDeny);
        buttonDeny.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "싫어", Toast.LENGTH_LONG).show();

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
        mCameraSource = new CameraSource.Builder(context, mFaceDetector)
                .setRequestedPreviewSize(640, 480)
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
                mPreview.start(mCameraSource, cameraOverlay);
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
            //mOverlay = overlay;
        }
        @Override
        public void onNewItem(int faceId, Face item) {
            if(check || !isAccept) return;
            mOverlay = cameraOverlay;
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
        //Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
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
