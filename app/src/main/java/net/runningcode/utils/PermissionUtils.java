package net.runningcode.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2017/4/11.
 */

public class PermissionUtils {
    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        for (String permission : permissions) {
            L.i("ContextCompat.checkSelfPermission(context, permission):"+ContextCompat.checkSelfPermission(context, permission));
            boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager
                    .PERMISSION_GRANTED);
            if (!hasPermission) return false;
        }
        return true;
    }
}
