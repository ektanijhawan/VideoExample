package recordvideo.ekta.com.shrofilevideo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ekta on 01-06-2017.
 */

public class PermissionUtils {
    public static void requestPermissions(Object o, int permissionId, String... permissions) {
//        if (o instanceof Fragment) {
//            FragmentCompat.requestPermissions((Fragment) o, permissions, permissionId);
//        } else
        if (o instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) o, permissions, permissionId);
            // ActivityCompat.requestPermissions((AppCompatActivity) o, permissions, contact_permission);

        }
    }
    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
