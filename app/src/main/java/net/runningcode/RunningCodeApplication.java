
package net.runningcode;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.xiaomi.mistatistic.sdk.URLStatsRecorder;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import net.runningcode.constant.Constants;
import net.runningcode.utils.L;
import net.runningcode.utils.PathUtil;
import net.runningcode.utils.SPUtils;
import net.runningcode.utils.StreamUtil;
import net.runningcode.utils.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class RunningCodeApplication extends Application {
    private static RunningCodeApplication sInstance;
    static String channel;
    public String orcPath;

    public static RunningCodeApplication getInstance(){
        return sInstance;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        L.i("**************attachBaseContext***************");
        sInstance = this;
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.i("**************onCreate***************");
        sInstance = this;
        PathUtil.initial(this);
        NoHttp.init(this);
        Logger.setTag("NoHttpSample");
        Logger.setDebug(BuildConfig.DEBUG);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志
        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(channel)) {
            channel = Constants.DEFAULT_CHANNEL;
        }
        SPUtils.getInstance(null).edit().putString(Constants.KEY_CHANNEL,channel).commit();
        initDb();
//        initPush();
        if (shouldInit())
            initXiaoMi();
//        AutoLayoutConifg.getInstance().useDeviceSize();
//        initOCR();
//        RcExceptionHandler handler = new RcExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(handler);
//        Thread.currentThread().setUncaughtExceptionHandler(handler);
    }

    private void initOCR() {
        String folder = PathUtil.getInstance().getCacheRootPath("ocr");
        orcPath = folder + "/" + "TianruiWorkroomOCR.dat";
        final File file = new File(orcPath);
        if (!file.exists()){
            ThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream in = getAssets().open("TianruiWorkroomOCR.dat");
                        StreamUtil.InputStream2File(in,file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void initDb() {
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }

    private static void initXiaoMi() {
        MiPushClient.registerPush(sInstance, Constants.MI_APP_ID, Constants.MI_APP_KEY);
        // regular stats. 小米统计
        MiStatInterface.initialize(sInstance.getApplicationContext(), Constants.MI_APP_ID, Constants.MI_APP_KEY,channel);

        MiStatInterface.setUploadPolicy(
                MiStatInterface.UPLOAD_POLICY_WIFI_ONLY, 0);
        MiStatInterface.enableLog();

        // enable exception catcher.
        MiStatInterface.enableExceptionCatcher(true);

        // enable network monitor
        URLStatsRecorder.enableAutoRecord();
        Log.i("mipush", MiStatInterface.getDeviceID(sInstance));

    }
    public static void loadImg(final ImageView view, String url) {
        Glide.with(sInstance).load(url).placeholder(R.drawable.icon_query)
                .into(view);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
