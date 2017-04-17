package net.runningcode.detect;

import android.os.Looper;
import android.util.Printer;

/**
 * Created by Administrator on 2017/3/6.
 */

public class BlockDetector {

    public static void start(){
        Looper.getMainLooper().setMessageLogging(new Printer() {
            private static final String START = ">>>>> Dispatching";
            private static final String END = "<<<<< Finished";
            @Override
            public void println(String x) {
                if (x.startsWith(START)){
                    LogMonitor.getInstance().startMonitor();
                }

                if (x.startsWith(END)){
                    LogMonitor.getInstance().stopMonitor();
                }
            }
        });
    }
}
