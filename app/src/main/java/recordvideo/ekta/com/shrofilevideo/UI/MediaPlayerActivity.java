package recordvideo.ekta.com.shrofilevideo.UI;

import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import recordvideo.ekta.com.shrofilevideo.R;

public class MediaPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    Uri targetUri;

    MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean pausing = false;
    int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        Bundle extras = getIntent().getExtras();

        width = extras.getInt("width");
        targetUri=Uri.parse(extras.getString("mediaUri"));

        FloatingActionButton play = (FloatingActionButton)  findViewById(R.id.play);
        FloatingActionButton pause = (FloatingActionButton)  findViewById(R.id.pause);


        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceView.setMinimumHeight(width);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
//        surfaceHolder.setFixedSize(176, 144);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mediaPlayer = new MediaPlayer();

        play.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                pausing = false;

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(surfaceHolder);

                try {
                    mediaPlayer.setDataSource(getApplicationContext(), targetUri);
                    mediaPlayer.prepare();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

            }
        });

        pause.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (pausing) {
                    pausing = false;
                    mediaPlayer.start();
                } else {
                    pausing = true;
                    mediaPlayer.pause();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

}
