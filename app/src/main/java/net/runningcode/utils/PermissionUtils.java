package net.runningcode.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */

public class PermissionUtils {
    private static final String[] STRICT_PERMISSIONS = new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission" +
            ".ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.CAMERA"};

    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            L.i("ContextCompat.checkSelfPermission(context, permission):" + ContextCompat.checkSelfPermission(context, permission));
            boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager
                    .PERMISSION_GRANTED);
            if (!hasPermission) {
                return false;
            }
        }
        return true;
    }

    public static void checkAndRequestPermission(@NonNull FragmentActivity activity, @NonNull OnPermissionGrantCallback callback,
                                                 @Size(min = 1L) String[] permissions) {
        if (hasPermission(activity, permissions)) {
            callback.onGranted(permissions);
        } else {
            internalImpl(activity.getSupportFragmentManager(), callback, permissions);
        }
    }


    private static void internalImpl(FragmentManager fm, OnPermissionGrantCallback callback, String[] permissions) {
        SupportPermissionHandler handler = new SupportPermissionHandler();
        Bundle args = new Bundle();
        args.putStringArray("permissions", permissions);
        handler.setArguments(args);
        handler.setCallback(callback);
        fm.beginTransaction().add(handler, "PermissionHandler" + handler.hashCode()).commit();
    }


    public static class SupportPermissionHandler extends Fragment {
        private static final String TAG = "PermissionHandler";
        private static final int PERMISSION_REQUEST_CODE = 100;
        private String[] permissions;
        @NonNull
        private OnPermissionGrantCallback callback;

        public SupportPermissionHandler() {
        }

        public void setCallback(@NonNull OnPermissionGrantCallback callback) {
            this.callback = callback;
        }

        public void onAttach(Context context) {
            super.onAttach(context);
            if (this.getArguments() != null) {
                this.permissions = this.getArguments().getStringArray("permissions");
            }

            if (this.permissions != null) {
                this.requestPermissions(this.permissions, PERMISSION_REQUEST_CODE);
            }

        }

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            List<String> granted = new ArrayList();
            List<String> denied = new ArrayList();

            int result;
            for (int i = 0; i < permissions.length; ++i) {
                String permission = permissions[i];
                result = grantResults[i];
                if (result == 0) {
                    granted.add(permission);
                } else {
                    denied.add(permission);
                }
            }

            if (granted.size() > 0) {
                this.callback.onGranted((String[]) granted.toArray(new String[granted.size()]));
            }

            if (denied.size() > 0) {
                this.callback.onDenied((String[]) denied.toArray(new String[denied.size()]));
            }

//            for (result = 0; result < STRICT_PERMISSIONS.length; ++result) {
//                String p = STRICT_PERMISSIONS[result];
//                if (denied.contains(p) && this.getActivity() != null) {
//                    PermissionUtil.handleStrictPermissionDenied(this.getActivity(), p);
//                    break;
//                }
//            }

            this.getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }


        public static void toPermissionSetting(FragmentActivity activity) {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + activity.getPackageName()));
            intent.addCategory("android.intent.category.DEFAULT");
            activity.startActivityForResult(intent, 201);
        }
    }


    public interface OnPermissionGrantCallback {
        void onGranted(String[] permissions);

        void onDenied(String[] strings);
    }
}
