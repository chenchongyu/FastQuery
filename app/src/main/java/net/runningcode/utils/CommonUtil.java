package net.runningcode.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import net.runningcode.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Enumeration;

public class CommonUtil {
    private final static String[] colors = {"#D7D2B5", "#F5B1A6", "#23C9B5", "#A8EFE9", "#C3EAEF", "#B8EACD"
            , "#ECCCB3", "#F2AEA5", "#FB6370",};

    private static long lastClickTime;

    public static String getVersion(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    public static int getVersionCode(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        int version = 0;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }


    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /*
     * MD5 加密
     */
    public static String md5(String password) {
        StringBuffer sb = new StringBuffer(32);

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(password.getBytes("utf-8"));

            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1, 3));
            }
        } catch (Exception e) {
            return null;
        }
        return sb.toString();

    }

//    public static String getDeviceID(Context context) {
//        TelephonyManager tm = (TelephonyManager) context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        return tm.getDeviceId();
//    }

    @SuppressLint("NewApi")
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }


    public static boolean hideInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return false;
    }

    public static boolean showInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.showSoftInput(view, 0);
        }

        return false;
    }

    /**
     * 获取sd卡剩余空间
     *
     * @return
     */
    public static long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    public static String convertStorage(long size) {
        long kb = 1024L;
        long mb = kb * 1024L;
        long gb = mb * 1024L;
        if (size >= gb) {
            return String.format("%.2fG", new Object[]{Float.valueOf((float) size / (float) gb)});
        } else {
            float f;
            if (size >= mb) {
                f = (float) size / (float) mb;
                return String.format(f > 100.0F ? "%.2fM" : "%.2fM", new Object[]{Float.valueOf(f)});
            } else if (size >= kb) {
                f = (float) size / (float) kb;
                return String.format(f > 100.0F ? "%.2fK" : "%.2fK", new Object[]{Float.valueOf(f)});
            } else {
                return String.format("%dB", new Object[]{Long.valueOf(size)});
            }
        }
    }

    public static boolean containCN(String str) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes.length == str.length()) {
                return false;
            } else {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static void openMarket(Context context) {
        Intent startintent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        startintent.setData(uri);
        startintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startintent);

//        Intent startintent = new Intent("android.intent.action.MAIN");
//        startintent.addCategory("android.intent.category.APP_MARKET");
//        startActivity(startintent);
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    //这里需要注意：这里增加了一个限定条件( inetAddress instanceof Inet4Address ),
                    // 主要是在Android4.0高版本中可能优先得到的是IPv6的地址。参考：http://blog.csdn.net/stormwy/article/details/8832164
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("here", ex.toString());
        }
        return null;
    }

    public static boolean isPhoneNum(String num) {
        return num.matches("^(13|15|18)\\d{9}$");
    }

    public static int getDrawbleByWeather(String weather) {
        if (TextUtils.equals(weather, "阴") || TextUtils.equals(weather, "多云")) {
            return R.drawable.icon_overcast;
        } else if (TextUtils.equals(weather, "小雨")) {
            return R.drawable.icon_rain_light;
        }
        if (TextUtils.equals(weather, "中雨")) {
            return R.drawable.icon_rain_moderate;
        }
        if (TextUtils.equals(weather, "大雨")) {
            return R.drawable.icon_rain_heavy;
        }
        if (weather.contains("雪")) {
            return R.drawable.icon_snow;
        } else {
            return R.drawable.icon_sunny;
        }
    }

    public static int getDrawbleByPhone(String supplier) {
        if (TextUtils.isEmpty(supplier)) {
            return R.drawable.icon_mobile;
        }
        if (supplier.startsWith("联通")) {
            return R.drawable.icon_unicom;
        } else if (supplier.startsWith("电信")) {
            return R.drawable.icon_telecom;
        } else {
            return R.drawable.icon_mobile;
        }
    }

    public static int getDrawbleBySex(String gender) {
        return TextUtils.equals(gender, "男") ? R.drawable.icon_boy : R.drawable.icon_gril;
    }

    public static int getBgDrawbleByWeather(String weather) {
        if (TextUtils.equals(weather, "阴") || TextUtils.equals(weather, "多云")) {
            return R.drawable.icon_overcast_bg;
        } else if (TextUtils.equals(weather, "中雨")) {
            return R.drawable.icon_rain_moderate_bg;
        } else if (TextUtils.equals(weather, "大雨")) {
            return R.drawable.icon_rain_heavy_bg;
        } else if (weather.contains("雨")) {
            return R.drawable.icon_rain_light_bg;
        } else if (weather.contains("雪")) {
            return R.drawable.icon_snow_bg;
        } else {
            return R.drawable.icon_sunny_bg;
        }
    }

    public static String genPicPath() {
        L.i("图片路径：" + PathUtil.getInstance().getCacheRootPath("pic") + "/translate_" + System.currentTimeMillis() + ".jpg");
        return PathUtil.getInstance().getCacheRootPath("pic") + "/translate_" + System.currentTimeMillis() + ".jpg";
    }

    public static String genMP3Path(String ext) {
        L.i("语音路径：" + PathUtil.getInstance().getCacheRootPath("sound") + "/translate_" + ext.replaceAll(" ", "—") + ".mp3");
        return PathUtil.getInstance().getCacheRootPath("sound") + "/translate_" + ext.replaceAll(" ", "—") + ".mp3";
    }

    public static void copy(Context context, String str) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setPrimaryClip(ClipData.newPlainText(null, str));
    }
}


