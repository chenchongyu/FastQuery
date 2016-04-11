
package net.runningcode;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;
import android.text.TextUtils;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import net.runningcode.dao.DaoMaster;
import net.runningcode.dao.DaoSession;
import net.runningcode.utils.DateUtil;
import net.runningcode.utils.L;
import net.runningcode.utils.PathUtil;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;


public class RunningCodeApplication extends Application {
    private static String CRASH_LOG = "";
    private static RunningCodeApplication sInstance;
    private boolean mRunInServiceProcess = false;
    private DaoSession daoSession;

    public static RunningCodeApplication getInstance(){
        return sInstance;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        L.i("**************attachBaseContext***************");
        checkRunningProcess();
        sInstance = this;
    }

    private void checkRunningProcess() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> infos = activityManager
                .getRunningAppProcesses();
        int pid = Process.myPid();
        RunningAppProcessInfo myInfo = null;
        for (RunningAppProcessInfo info : infos) {
            if (info.pid == pid) {
                myInfo = info;
                break;
            }
        }
        if (myInfo != null) {
            mRunInServiceProcess = !TextUtils.equals(myInfo.processName,
                    getPackageName());
        }
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
        initDb();
//        AutoLayoutConifg.getInstance().useDeviceSize();
//        RcExceptionHandler handler = new RcExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(handler);
//        Thread.currentThread().setUncaughtExceptionHandler(handler);
    }

    private void initDb() {
        DaoMaster.OpenHelper openHelper = new DaoMaster.OpenHelper(this, "quick-query", null) {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };

        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession(){
        return daoSession;
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

}
