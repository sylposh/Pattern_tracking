package com.poshnyamane.pattern_tracking;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;


public class MainActivity extends Activity {
    private static final String TAG = "CameraDemo";
    Button match;
    Preview preview;
    private ImageView frameDisplay;
    TemplateMatching matchIMGS;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                    //mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = new Preview(this);
        frameDisplay = (ImageView) findViewById(R.id.frame_display);
        matchIMGS = new TemplateMatching();
        ((FrameLayout) findViewById(R.id.previewSurface)).addView(preview);
        match = (Button) findViewById(R.id.Match);
        match.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                matchIMGS.match();
            }
        });

        Log.d(TAG, "onCreate'd");


    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }






    //Previw CLASS
    class Preview extends SurfaceView implements SurfaceHolder.Callback {
        private static final String TAG = "Preview";

        //ImageView frameDisplay = (ImageView)findViewById(R.id.frame_display);
        SurfaceHolder mHolder;
        public Camera camera;
        public String PictureFileName = "/sdcard/pic.png";
        YuvImage img;

        Preview(Context context) {
            super(context);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(holder);

                camera.setPreviewCallback(new Camera.PreviewCallback() {

                    public void onPreviewFrame(byte[] data, Camera camera) {
//
                        //set camera and image parameters and store the frame
                        Camera.Parameters parameters = camera.getParameters();
                        int PreviewSizeWidth = parameters.getPreviewSize().width;
                        int PreviewSizeHeight = parameters.getPreviewSize().height;
                        int imageFormat = parameters.getPreviewFormat();


                        if (imageFormat == ImageFormat.NV21) {
                            //Rect rect = new Rect(0, 0, PreviewSizeWidth, PreviewSizeHeight);
                            img = new YuvImage(data, ImageFormat.NV21, PreviewSizeWidth, PreviewSizeHeight, null);



                            ////
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            img.compressToJpeg(new Rect(0, 0, PreviewSizeWidth, PreviewSizeHeight), 50, out);
                            byte[] imageBytes = out.toByteArray();
                            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            frameDisplay.setImageBitmap(image);
                            ///


                            /*OutputStream outStream = null;
                            File file = new File(PictureFileName);

                            try {
                            *//*outStream = new FileOutputStream(String.format(
                                    "/sdcard/Test/%d.jpg", System.currentTimeMillis()));
*//*
                                outStream = new FileOutputStream(file);
                                img.compressToJpeg(rect, 100, outStream);
                                outStream.flush();
                                outStream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                        //load_frame();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            // Because the CameraDevice object is not a shared resource, it's very
            // important to release it when the activity is paused.
            camera.stopPreview();
            camera = null;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            camera.startPreview();
        }


        //The function loads the processed frame to display with the
        //object being tracked enclosed by a rectangle
        public void load_frame() {
            File imgFile = new File("/sdcard/pic.png");

            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    frameDisplay.setImageBitmap(myBitmap);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }
        }
    }
}


