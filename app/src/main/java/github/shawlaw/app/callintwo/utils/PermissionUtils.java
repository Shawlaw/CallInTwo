package github.shawlaw.app.callintwo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * Android动态权限申请的工具类
 * @author Shawlaw
 * @date 2017/12/5
 */

public class PermissionUtils {
    private final static int PERMISSION_REQUEST_CODE = 0X10241024;

    public static boolean checkPermission(Context context, String permission) {
        boolean noNeedRequestPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int grantStatus = ContextCompat.checkSelfPermission(context, permission);
            noNeedRequestPermission = (grantStatus == PackageManager.PERMISSION_GRANTED);
        }
        return noNeedRequestPermission;
    }

    public static void requestPermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    public static boolean onPermissionRequestResultBack(
            String carePermission, int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults, IPermissionCallback callback) {
        boolean result = true;
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE : {
                for (int i = 0; i < permissions.length; i++) {
                    if (carePermission.equals(permissions[i])) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            callback.onGranted(carePermission);
                        } else {
                            callback.onDenied(carePermission);
                        }
                        break;
                    }
                }
                break;
            }
            default: {
                result = false;
                break;
            }
        }
        return result;

    }

    public interface IPermissionCallback {
        void onGranted(String permission);
        void onDenied(String permission);
    }
}
