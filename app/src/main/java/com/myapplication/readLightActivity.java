package com.myapplication;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;


public class readLightActivity extends AppCompatActivity implements SurfaceHolder.Callback {



    //global variables
    SurfaceView  cameraView,transparentView;
    SurfaceHolder holder,holderTransparent;

    Camera camera;

    private float RectLeft, RectTop,RectRight,RectBottom ;
    int  deviceHeight,deviceWidth;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_text);
        cameraView = (SurfaceView)findViewById(R.id.CameraView);

        holder = cameraView.getHolder();
        holder.addCallback((SurfaceHolder.Callback) this);
        //holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.setSecure(true);


        // Create second surface with another holder (holderTransparent)
        transparentView = (SurfaceView)findViewById(R.id.TransparentView);

        holderTransparent = transparentView.getHolder();
        holderTransparent.addCallback((SurfaceHolder.Callback) this);
        holderTransparent.setFormat(PixelFormat.TRANSLUCENT);

        transparentView.setZOrderMediaOverlay(true);

        //getting the device heigth and width
        deviceWidth=getScreenWidth();
        deviceHeight=getScreenHeight();

    }

    public static int getScreenWidth() {

        return Resources.getSystem().getDisplayMetrics().widthPixels;

    }



    public static int getScreenHeight() {

        return Resources.getSystem().getDisplayMetrics().heightPixels;

    }





    private void Draw()

    {
        Canvas canvas = holderTransparent.lockCanvas(null);
        Paint  paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);

        RectLeft = 1;
        RectTop = 200 ;
        RectRight = RectLeft+ deviceWidth-100;
        RectBottom =RectTop+ 200;

        Rect rec=new Rect((int) RectLeft,(int)RectTop,(int)RectRight,(int)RectBottom);
        canvas.drawRect(rec,paint);

        holderTransparent.unlockCanvasAndPost(canvas);

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            synchronized (holder)
            {Draw();}   //call a draw method
            camera = Camera.open(); //open a camera
        }

        catch (Exception e) {
            Log.i("Exception", e.toString());
            return;

        }

        Camera.Parameters param;
        param = camera.getParameters();


        param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
        {
            camera.setDisplayOrientation(90);
        }

        camera.setParameters(param);


        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }

        catch (Exception e) {

            return;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        refreshCamera(); //call method for refress camera

    }

    public void refreshCamera() {
        if (holder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        }



        catch (Exception e) {


        }
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (Exception e) {

        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.release(); //for release a camera
    }



}
