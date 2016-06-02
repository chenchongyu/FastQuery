
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
import net.runningcode.utils.DateUtil;
import net.runningcode.utils.L;
import net.runningcode.utils.PathUtil;
import net.runningcode.utils.SPUtils;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class RunningCodeApplication extends Application {
    private static String CRASH_LOG = "";
    private static RunningCodeApplication sInstance;
    Realm realm = null;
    static String channel;
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
        Logger.setDebug(true);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志
        CRASH_LOG = PathUtil.getInstance().getCacheRootPath("log") + "/rc_crash.log";
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
        RcExceptionHandler handler = new RcExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        Thread.currentThread().setUncaughtExceptionHandler(handler);
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
    /*
    private void initPush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(false);
        mPushAgent.enable();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        //开启推送并设置注册的回调处理
        L.i("是否开启了推送："+mPushAgent.isEnabled()+"   "+mPushAgent.getRegistrationId());
    }
*/
    public static void loadImg(final ImageView view, String url) {
        Glide.with(sInstance).load(url).placeholder(R.drawable.icon_query)
                .into(view);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private class RcExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            PrintStream printer = null;
            try {
                FileOutputStream out = new FileOutputStream(CRASH_LOG, true);
                printer = new PrintStream(out, false);
                printer.print("#====== ");
                printer.print(DateUtil.getCurDateStr());
                printer.println(" ======");

                printer.print("UncaughtException:");
                if (thread != null) {
                    printer.print(thread.getName());
                    printer.print("(" + thread.getId() + ")");
                }
                printer.println();

                if (ex != null) {
                    ex.printStackTrace();
                    ex.printStackTrace(printer);

                    Throwable t = ex.getCause();
                    if (t != null) {
                        printer.println("Case trace:");
                        t.printStackTrace(printer);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (printer != null) {
                    try {
                        printer.close();
                    } catch (Throwable t) {
                        // ignore
                    }
                }
            }

        }
    }

    /**
     * 该Handler是在BroadcastReceiver中被调用，故
     * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
     * */
   /* UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            L.i("接收到自定义消息："+msg.extra);
            String type = msg.extra.get("type").toString();
            if (TextUtils.equals(type, Constants.TYPE_OPEN)){
                CommonUtil.openMarket(sInstance);
            }
        }
    };*/
}
