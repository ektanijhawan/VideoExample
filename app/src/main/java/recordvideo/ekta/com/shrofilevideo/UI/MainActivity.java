package recordvideo.ekta.com.shrofilevideo.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

//import com.amazonaws.auth.CognitoCachingCredentialsProvider;
//import com.amazonaws.regions.Regions;

import recordvideo.ekta.com.shrofilevideo.R;

public class MainActivity extends AppCompatActivity {
    Button mMakeVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMakeVideo = (Button) findViewById(R.id.make_video);
        mMakeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordVideoActivity.class);
                startActivity(intent);
            }
        });
    }
}