package com.example.yj.itproject_07;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**쓰기모드시에 그림을 그려주는 스크립트 입니다.*/

 public class PaintBoard extends View {


    private Map<Path, Integer> colorsMap = new HashMap<Path, Integer>();    //컬러맵
    private Map<Path, Integer> sizesMap = new HashMap<Path, Integer>();     //펜사이즈맵
    private Map<Path, Xfermode> penModeMap = new HashMap<Path, Xfermode>(); //펜모드(지우개)맵
    private ArrayList<Path> paths = new ArrayList<Path>();                  //언도용 패스가 저장될 곳
    private ArrayList<Path> undonePaths = new ArrayList<Path>();            //레도용

    Canvas mCanvas;     //컨버스
    Canvas mcanvas;
    Bitmap mBitmap;     //더블버퍼링을 위한 비트맵
    Path mPath;  //길
    private Bitmap lastBitmap;

    Xfermode xFer;

    Paint mBitmapPaint;

    //페인트 정보
    public Paint mPaint;
    private static final boolean RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;
    private int mCertainColor = main.mColor;     //색

    private int mStrokeWidth = main.mSize;

    private static int width ;
    private static int height;

    public static boolean mEraserMode = false;


    public PaintBoard(Context context) {
        super(context);
        Display display =((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        // 새로운 페인트오브젝트 생성
        mPaint = new Paint();
        mPaint.setAntiAlias(RENDERING_ANTIALIAS);
        mPaint.setDither(DITHER_FLAG);
        mPaint.setColor(mCertainColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mCanvas = new Canvas();
        width = point.x;
        height = point.y;
        mPath = new Path();
        xFer = null;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);



        Log.i("PaintBoard", "initialized.");
    }

    /*
    * (non-Javadoc)
     * @see korsoft.net.Test008.ColorPickerDialog.OnColorChangedListener#colorChanged(int)
    * 색상선택 다이얼로그에서 색상을 선택했을때 이 메소드가 호출되고,
    * 선택한 색상값이 color 변수에 넘어옵니다. 이 값을 그리기 객체에
    * 색상값을 설정해줍니다. 다이얼로그에서 색상값을 변경하게 되면,
    * 터치로 그리기를 수행할때, 바뀐 색상값으로 적용되게 됩니다.
    */

    //페인트 배경
    public void drawBackground(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT);    //배경색 투명하게
        }
    }

    public void drawBackgroundColor(int color) {
        mCanvas.drawColor(color);
        invalidate();
    }

    public void drawBackgroundPhoto(Bitmap bitmap) {
        if (mCanvas != null) {
            mCanvas.drawBitmap(bitmap, 0, 0, null);
        }
        invalidate();
    }

    //페인트 속성
    public void updatePaintProperty(int color, int size) {
        mPaint.setColor(color);         //색
        mPaint.setStrokeWidth(size);    //사이즈
        mCertainColor = color;
        mStrokeWidth = size;
    }

    public void newImage(int width, int height) {
        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);

        mBitmap = img;
        mCanvas = canvas;

        drawBackground(mCanvas);

        invalidate();
    }

    public void setImage(Bitmap newImage) {
        setImageSize(newImage.getWidth(), newImage.getHeight(), newImage);
        invalidate();
    }

    /**
     * Set image size
     *
     * @param width
     * @param height
     * @param newImage
     */
    public void setImageSize(int width, int height, Bitmap newImage) {
        if (mBitmap != null) {
            if (width < mBitmap.getWidth()) width = mBitmap.getWidth();
            if (height < mBitmap.getHeight()) height = mBitmap.getHeight();
        }

        if (width < 1 || height < 1) return;

        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        drawBackground(mCanvas);

        if (newImage != null) {
            canvas.setBitmap(newImage);
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            mCanvas.restore();
        }

        mBitmap = img;
        mCanvas = canvas;

    }

    //사이즈 변경
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            newImage(w, h);
        }
    }


    /**
     * 지우개 모드일시 패스성질을 CLEAR로 바꿔준다
     * 한번 더 누르면 다시 없던걸로
     */
    public void eMode(boolean mEraser) {
        if (!mEraser) {
            xFer = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
            mEraserMode = true;
        } else if (mEraser) {
            xFer = null;
            mEraserMode = false;
        }
    }


    /**
     * (non-Javadoc)
     *
     * @see View#onDraw(Canvas)
     * 액티비티에 그리기를 수행할때 호출되는 메소드입니다.
     * 먼저 이전에 그렸던 내용이 비트맵에 저장되어있으므로,
     * 먼저 비트맵을 출력하고, 현재 Path 를 출력하여,
     * 현재 터치 드래그 해서, 선을 그리게 됩니다.
     */
    @Override
    protected void onDraw(Canvas canvas) {

        mcanvas = new Canvas(mBitmap);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);




        /** mEraserMode 는 boolean 타입의 변수로서, 현재 지우개 사용모드인지를 판단.
         * 지우개 모드일때 화면에 패스를 그리지 않는 이유는 앞서 말했듯
         * PorterDuff.Mode.CLEAR 모드인 paint 는 검정색 색상의 속성을 지니기 때문에,
         * 이를 화면에 표시 하지 않기 위해서 이다.
         * */
        for (Path p : paths) {
            mPaint.setColor(colorsMap.get(p));
            mPaint.setStrokeWidth(sizesMap.get(p));
            mPaint.setXfermode(penModeMap.get(p));
            mcanvas.drawPath(p, mPaint);

        }

    }

    public void onClickUndo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {
        }
    }

    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        } else {
        }
    }

    public void onClickAllErase() {
        colorsMap = new HashMap<Path, Integer>();
        sizesMap = new HashMap<Path, Integer>();
        penModeMap = new HashMap<Path, Xfermode>();
        paths = new ArrayList<Path>();
        undonePaths = new ArrayList<Path>();
        mBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    public void onClickhistory(Map<Path, Integer> color, Map<Path, Integer> size, Map<Path, Xfermode> pM, ArrayList<Path> pt, ArrayList<Path> upt) {
        colorsMap = color;
        sizesMap = size;
        penModeMap = pM;
        paths = pt;
        undonePaths = upt;
        invalidate();
    }

    public Map getColorMap() {
        return colorsMap;
    }

    public Map getSizeMap() {
        return sizesMap;
    }

    public Map getPenModeMap() {
        return penModeMap;
    }

    public ArrayList getPaths() {
        return paths;
    }

    public ArrayList getUPaths() {
        return undonePaths;
    }


    /**
     * 액티비티 화면에 터치를 하면 선을 초기화 합니다.
     * 현재 경로값을 업데이트 합니다.
     * 실제 터치 이벤트는 밑에 onTouchEvent 메소드에서
     * 발생하고, 그 메소드에서 이 메소드를 호출합니다.
     */
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        if (!main.isOpen) {
            main.isOpen = true;
        }
        paths.add(mPath);
        colorsMap.put(mPath, mCertainColor);
        sizesMap.put(mPath, mStrokeWidth);
        penModeMap.put(mPath, xFer);
        undonePaths.clear();
        //isChanged = false;
    }

    /**
     * 터치 후 드래그를 하면 계속해서 이 메소드가 실시간으로
     * 호출되고, 현재 패스값을 바꾸면서 저장을 합니다.
     * 현재 경로값을 실시간으로 업데이트 합니다.
     * 실제 터치 이벤트는 밑에 onTouchEvent 메소드에서
     * 발생하고, 그 메소드에서 이 메소드를 호출합니다.
     */
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        /** 지우개를 실시간으로 반영하기 위해서는 캐시 캔버스인 mCanvas에 바로바로 그려줘야한다. */
        if (mEraserMode) {
            mCanvas.drawPath(mPath, mPaint);
        }
    }

    /**
     * 드래그하다가 손을 화면에서 떼는 순간 호출되어,
     * 선을 그리기를 마무리 합니다.
     * 실제 터치 이벤트는 밑에 onTouchEvent 메소드에서
     * 발생하고, 그 메소드에서 이 메소드를 호출합니다.
     */
    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath = new Path();
        mPath.reset();
        //paths.add(mPath);
    }


    /**
     * (non-Javadoc)
     *
     * @see View#onTouchEvent(MotionEvent)
     * 위의 세가지 터치 이벤트에서 설정한 경로값을 실시간으로,
     * 이 이벤트에서 각 터치 이벤트 종류에 따라서 경로값을 재설정하고,
     * invalidate 메소드를 호출하여, 액티비티 화면에 다시 그리기를 수행합니다.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }



}

