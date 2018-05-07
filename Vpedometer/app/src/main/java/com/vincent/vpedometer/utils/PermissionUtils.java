package com.vincent.vpedometer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by Administrator on 2018/3/27 14:10
 */
public class PermissionUtils {


    //**************** Android M Permission (Android 6.0 the permission tool)
    public static int permissionRequestCode = 99;
    private static PermissionCallback permissionRunnable;

    public interface PermissionCallback {
        void hasPermission();

        void noPermission();
    }

    /**
     * Android M
     *
     * @param permissionDes how to describe permission you want to grant
     * @param runnable      the call back function for permission
     * @param permissions   the array of permission，can  read Manifest file， e.g. Manifest.permission.WRITE_CONTACTS
     */
    public static void performCodeWithPermission(Context context, @NonNull String permissionDes, PermissionCallback runnable, @NonNull String... permissions) {
        if (permissions == null || permissions.length == 0 || runnable == null)
            return;
        permissionRunnable = runnable;
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(permissions, context)) {
            if (runnable != null) {
                permissionRunnable.hasPermission();
            }
        } else {
            //permission has not been granted.
            requestPermission(context, permissionDes, permissionRequestCode, permissions);
        }

    }

    /**
     * for loop to check the permission has grant before or not
     *
     * @param permissions the permission the program need
     * @param context     the activity
     * @return has been grant or not
     */
    private static boolean checkPermissionGranted(String[] permissions, Context context) {
        boolean flag = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * @param context       the activity
     * @param permissionDes how to describe permission you want to grant
     * @param requestCode   when permission has a result, the requestcode will sent to call back function
     * @param permissions   permission the program need
     */
    private static void requestPermission(final Context context, String permissionDes, final int requestCode, final String[] permissions) {
        if (shouldShowRequestPermissionRationale(context, permissions)) {
            //never ask again check box is clicked we need to show other UI to let user know the permission is neccessary
            new AlertDialog.Builder(context)
                    .setTitle("TIPS")
                    .setMessage(permissionDes)
                    .setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
                        }
                    }).show();

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
        }
    }

    /**
     * we use this function to determine user has clicked never ask again check box or not
     *
     * @param context     the activity
     * @param permissions permission the program need
     * @return the never ask again check box or not
     */
    private static boolean shouldShowRequestPermissionRationale(Context context, String[] permissions) {
        boolean flag = false;
        for (String p : permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, p)) {
                flag = true;
                break;
            }
        }
        return flag;
    }


    /**
     * to call activity which has implement the PermissionCallback interface
     *
     * @param grantResults the result of permission
     * @return the permission has grant or not
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }
        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionRunnable.noPermission();
                return false;
            }
        }
        permissionRunnable.hasPermission();
        return true;
    }

}
