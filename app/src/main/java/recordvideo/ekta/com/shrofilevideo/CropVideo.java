package recordvideo.ekta.com.shrofilevideo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import static java.security.AccessController.getContext;

/**
 * Created by Ekta on 03-06-2017.
 */

public class CropVideo {
    Context context;
    String TAG="CropVideo";
    FFmpeg ffmpeg;
    StringBuilder ffmpegCommand;
   public CropVideo(Context context){
       this.context=context;
       ffmpeg = FFmpeg.getInstance(context);

   }
    private void cropVideo(String originalVideoPath, String croppedVideoPath, int xCoordinate,
                           int yCoordinate, int imageWidth, int imageHeight, int croppedAngle) {
        String command;
        ffmpegCommand = new StringBuilder();
        ffmpegCommand.append("-y");
        ffmpegCommand.append(" ");
        ffmpegCommand.append("-i");
        ffmpegCommand.append(" ");
        ffmpegCommand = getFFmpegCommand(ffmpegCommand, imageWidth, imageHeight,
                xCoordinate, yCoordinate, originalVideoPath, croppedVideoPath, croppedAngle);
        command = ffmpegCommand.toString();

        Log.i(TAG, command);
        String[] cmd = command.split(" ");

        new CropVideoTask().execute(cmd);

    }

    private StringBuilder getFFmpegCommand(StringBuilder ffmpegCommand, int imageWidth,
                                           int imageHeight, int xCoordinate, int yCoordinate,
                                           String originalVideoPath, String croppedVideoPath, int angle) {
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        String rotate = "";
        switch (angle) {
            case 0:
                x = xCoordinate;
                y = yCoordinate;
                w = imageWidth;
                h = imageHeight;
                break;
            case 90:
//                x = yCoordinate;
//                y = mFileWidth - imageWidth - xCoordinate;
//                w = imageHeight;
//                h = imageWidth;
//                rotate = ",transpose=1";
                break;
            case 180:
//                x = mFileHeight - imageWidth - xCoordinate;
//                y = mFileWidth - imageHeight - yCoordinate;
//                w = imageWidth;
//                h = imageHeight;
//                rotate = ",vflip";
                break;
            case -90:
//                x = mFileHeight - imageHeight - yCoordinate;
//                y = xCoordinate;
//                w = imageHeight;
//                h = imageWidth;
//                rotate = ",transpose=2";
                break;
        }

        ffmpegCommand.append(originalVideoPath);
        ffmpegCommand.append(" ");
        ffmpegCommand.append("-vf");
        ffmpegCommand.append(" ");
        ffmpegCommand.append("crop=");
        ffmpegCommand.append(w);
        ffmpegCommand.append(":");
        ffmpegCommand.append(h);
        ffmpegCommand.append(":");
        ffmpegCommand.append(x);
        ffmpegCommand.append(":");
        ffmpegCommand.append(y);
        ffmpegCommand.append(rotate);
        ffmpegCommand.append(" ");
        ffmpegCommand.append(croppedVideoPath);

        return ffmpegCommand;
    }

    private class CropVideoTask extends AsyncTask<String[], Void, Void> {
        protected Void doInBackground(String[]... cmd) {
            try {
                ffmpeg = FFmpeg.getInstance(context);
                ffmpeg.execute(cmd[0], new ExecuteBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onProgress(String message) {
                    }

                    @Override
                    public void onFailure(String message) {

                    }

                    @Override
                    public void onSuccess(String message) {
                        // Do something with the cropped video
                    }

                    @Override
                    public void onFinish() {

                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
//                Log.i(TAG, "doInBackground: Exception + Device is not supported");
            }
            return null;
        }
    }

}
