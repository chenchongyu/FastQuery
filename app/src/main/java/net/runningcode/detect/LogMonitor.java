package net.runningcode.detect;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import net.runningcode.utils.L;


/**
 * Created by Administrator on 2017/3/6.
 */

public class LogMonitor {
    private static LogMonitor instance = new LogMonitor();

    private HandlerThread mLogThread = new HandlerThread("log");
    private Handler mHandler;
    private static final long TIME_BLOCK = 1000L;

    private LogMonitor(){
        mLogThread.start();
        mHandler = new Handler(mLogThread.getLooper());
    }

    private static Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            final StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement element:stackTrace){
                sb.append(element.toString()).append("\n");
            }
            L.e(sb.toString());
        }
    };

    public static LogMonitor getInstance(){
        return instance;
    }

    public void startMonitor(){
        mHandler.postDelayed(mLogRunnable,TIME_BLOCK);
    }

    public void stopMonitor(){
        mHandler.removeCallbacks(mLogRunnable);
    }
}
