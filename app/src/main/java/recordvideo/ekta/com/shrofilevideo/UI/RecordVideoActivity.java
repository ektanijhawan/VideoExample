package recordvideo.ekta.com.shrofilevideo.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import recordvideo.ekta.com.shrofilevideo.R;
import recordvideo.ekta.com.shrofilevideo.Utils.PermissionUtils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.CAPTURE_VIDEO_OUTPUT;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static recordvideo.ekta.com.shrofilevideo.Utils.Constants.CAMERA_PERMISSION;
import static recordvideo.ekta.com.shrofilevideo.Utils.Constants.READ_EXTERNAL_PERMISSION;
import static recordvideo.ekta.com.shrofilevideo.Utils.Constants.RECORD_AUDIO_PERMISSION;
import static recordvideo.ekta.com.shrofilevideo.Utils.Constants.RECORD_VIDEO_PERMISSION;
import static recordvideo.ekta.com.shrofilevideo.Utils.Constants.WRITE_EXTERNAL_PERMISSION;

public class RecordVideoActivity extends AppCompatActivity {



    private Camera myCamera;
    private RecordVideoActivity.MyCameraSurfaceView myCameraSurfaceView;
    private MediaRecorder mediaRecorder;
    Camera.Parameters params;
    Button myButton;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    int width;
    int height;
    boolean recording;
    static SurfaceHolder holder;
    Camera.Parameters parameters;
    boolean compress = false;
    List<Camera.Size> previewSizes;
    FrameLayout myCameraPreview;
    RelativeLayout mHideView;
    static int height1;
    static int width1;
    int actionBarHeight;
    ProgressDialog progressDialog;
    int a;
    static DisplayMetrics displayMetrics;
    String in;
    String out;
    Button cropButton;
    File cropfile;
    FFmpeg ffmpeg;
    String[] cmd;
    Button compressButton;
    String[] complexCommand;
    String compressed;
    Button mediaPlayer;
    String[] permissions;
//    CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        progressDialog = new ProgressDialog(this);
        getSupportActionBar();
        recording = false;
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height1 = displayMetrics.heightPixels;
        width1 = displayMetrics.widthPixels;
        myCamera = getCameraInstance();
//         credentialsProvider = new CognitoCachingCredentialsProvider(
//                getApplicationContext(),
//                "us-east-2:98172123-1722-4cc0-b626-bfe06d735f50", // Identity Pool ID
//                Regions.US_EAST_2 // Region
//        );
        // Create a record in a dataset and synchronize with the server
//        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
//        dataset.put("myKey", "myValue");
//        dataset.synchronize(new DefaultSyncCallback() {
//            @Override
//            public void onSuccess(Dataset dataset, List newRecords) {
//                //Your handler code here
//            }
//        });
        in = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4";
        out = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideocropped.mp4";
        compressed = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compressed.mp4";
        complexCommand = new String[]{"-y", "-i", out, "-s", width1 + "x" + width1, "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", compressed};

        cmd = new String[]{"-y", "-i", in, "-vf", "crop=" + width1 + ":" + width1 + ":" + width1 + ":" + width1, "-c:a", "copy", out};

        permissions = new String[]{CAMERA, RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, CAPTURE_VIDEO_OUTPUT, READ_EXTERNAL_STORAGE};
        if (!PermissionUtils.checkPermission(RecordVideoActivity.this, CAMERA) && !PermissionUtils.checkPermission(RecordVideoActivity.this, RECORD_AUDIO) && !PermissionUtils.checkPermission(RecordVideoActivity.this, READ_EXTERNAL_STORAGE)) {
            PermissionUtils.requestPermissions(this, CAMERA_PERMISSION, permissions);
        } else {

            //Get Camera for preview
            if (myCamera == null) {
                Toast.makeText(RecordVideoActivity.this,
                        "Fail to get Camera",
                        Toast.LENGTH_LONG).show();
            }
        }
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        myCameraSurfaceView = new RecordVideoActivity.MyCameraSurfaceView(this, myCamera);
        myCameraPreview = (FrameLayout) findViewById(R.id.videoview);
        mHideView = (RelativeLayout) findViewById(R.id.hideView);

        ViewTreeObserver vto = myCameraPreview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    myCameraPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    myCameraPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                width = myCameraPreview.getMeasuredWidth();
                height = myCameraPreview.getMeasuredHeight();

            }
        });
        int resheight = 0;
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            resheight = resources.getDimensionPixelSize(resourceId);
        }

        myCameraPreview.addView(myCameraSurfaceView);

        myButton = (Button) findViewById(R.id.mybutton);
        cropButton = (Button) findViewById(R.id.cropButton);
        compressButton = (Button) findViewById(R.id.compressButton);
        mediaPlayer= (Button) findViewById(R.id.mediaButton);
        a = height1 - width1;
        mHideView.setMinimumHeight(a);

        ffmpegLoad();
        myButton.setOnClickListener(myButtonOnClickListener);

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cropfile = new File(out);
                    execFFmpegCommand(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        compressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropButton.setOnClickListener(null);
                compress = true;
                execFFmpegCommand(complexCommand);
            }
        });
        mediaPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressButton.setOnClickListener(null);
                Intent intent = new Intent(RecordVideoActivity.this,MediaPlayerActivity.class);
                intent.putExtra("width",width1);
                intent.putExtra("mediaUri",compressed);
                startActivity(intent);
            }
        });
    }

    Button.OnClickListener myButtonOnClickListener
            = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {


            if (recording) {
                // stop recording and release camera
                mediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                cropButton.setVisibility(View.VISIBLE);
                Toast.makeText(RecordVideoActivity.this, "Successfully saved.!", Toast.LENGTH_SHORT).show();
                myButton.setOnClickListener(null);

            } else {

                if (!prepareMediaRecorder()) {
                    Toast.makeText(RecordVideoActivity.this,
                            "Fail in prepareMediaRecorder()!\n - Ended -",
                            Toast.LENGTH_LONG).show();
                }

                mediaRecorder.start();
                recording = true;
                myButton.setText("STOP");
            }
        }

    };

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            c.setDisplayOrientation(90);
            params = c.getParameters();

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private boolean prepareMediaRecorder() {

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = width1;
        profile.videoFrameHeight = width1;

        myCamera = getCameraInstance();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setOrientationHint(90);
        myCamera.unlock();

        mediaRecorder.setCamera(myCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder.setOutputFile(in);
        mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.

        mediaRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder().getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            myCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (myCamera != null) {
            myCamera.release();        // release the camera for other applications
            myCamera = null;
        }
    }

public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    public MyCameraSurfaceView(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int weight,
                               int height) {
        if (!PermissionUtils.checkPermission(RecordVideoActivity.this, CAMERA) && !PermissionUtils.checkPermission(RecordVideoActivity.this, RECORD_AUDIO) && !PermissionUtils.checkPermission(RecordVideoActivity.this, READ_EXTERNAL_STORAGE)) {
            PermissionUtils.requestPermissions(this, CAMERA_PERMISSION, permissions);
        }
        else {
            parameters = mCamera.getParameters();
            previewSizes = parameters.getSupportedPreviewSizes();
//            Camera.Size previewSize ;
            parameters.setPreviewSize(width1, width1);

            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // make any resize, rotate or reformatting changes here

            // start preview with new settings
            try {
                mCamera.setParameters(parameters);

                mCamera.setPreviewDisplay(mHolder);

                mCamera.startPreview();


            } catch (Exception e) {
            }
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        RecordVideoActivity.holder = holder;
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            if (!PermissionUtils.checkPermission(RecordVideoActivity.this, CAMERA) && !PermissionUtils.checkPermission(RecordVideoActivity.this, WRITE_EXTERNAL_STORAGE) && !PermissionUtils.checkPermission(RecordVideoActivity.this, RECORD_AUDIO) && !PermissionUtils.checkPermission(RecordVideoActivity.this, CAPTURE_VIDEO_OUTPUT) && !PermissionUtils.checkPermission(RecordVideoActivity.this, READ_EXTERNAL_STORAGE))
                PermissionUtils.requestPermissions(this, CAMERA_PERMISSION, permissions);
            else {
                mCamera.setPreviewDisplay(RecordVideoActivity.holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CAMERA_PERMISSION:
            case RECORD_AUDIO_PERMISSION:
            case RECORD_VIDEO_PERMISSION:
            case WRITE_EXTERNAL_PERMISSION:
            case READ_EXTERNAL_PERMISSION:
                try {

                    mCamera.setPreviewDisplay(RecordVideoActivity.holder);
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }



    }

    void ffmpegLoad() {
        ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.v("ffmpeg", "start");


                }

                @Override
                public void onFailure() {
                    Log.v("ffmpeg", "failure");


                }

                @Override
                public void onSuccess() {


                    Log.v("ffmpeg", "success");


                }

                @Override
                public void onFinish() {

                    Log.v("ffmpeg", "finish");

                }
            });
        } catch (
                FFmpegNotSupportedException e) {
            e.printStackTrace();
            // Handle if FFmpeg is not supported by device
        }
    }

    void execFFmpegCommand(String[] cmd) {

        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.v("ffmpegcrop", "start");

                }

                @Override
                public void onProgress(String message) {
                    if (!compress)
                        progressDialog.setMessage(getString(R.string.cropping_may_take_a_while));
                    else
                        progressDialog.setMessage("Compressing");

                    progressDialog.show();
                    Log.v("ffmpegcrop", "progress");

                }

                @Override
                public void onFailure(String message) {

                    Log.v("ffmpegcrop", "crop");

                }

                @Override
                public void onSuccess(String message) {
                    Log.v("ffmpegcrop", "success");
                }


                @Override
                public void onFinish() {
                    if (!compress) {
                        Toast.makeText(RecordVideoActivity.this, R.string.sucess_crop, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RecordVideoActivity.this, R.string.success_compressed, Toast.LENGTH_SHORT).show();
                        mediaPlayer.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                    Log.v("ffmpegcrop", "finish");
                    compressButton.setVisibility(View.VISIBLE);

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}


