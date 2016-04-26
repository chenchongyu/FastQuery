package net.runningcode;

import android.content.Intent;
import android.os.Bundle;

import net.runningcode.utils.L;
import net.runningcode.utils.ThreadPool;

/**
 * Created by Administrator on 2016/1/15.
 */
public class LogoActivity extends BasicActivity{

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                L.i("from logo to Index.");
//                startActivity(new Intent(LogoActivity.this,IndexActivity.class));
////                finish();
//            }
//        },3000);
        ThreadPool.submitDelay(new Runnable() {
            @Override
            public void run() {
                L.i("from logo to Index in ThreadPool.");
                startActivity(new Intent(LogoActivity.this,IndexActivity.class));
                finish();
            }
        },3);

//        startActivity(new Intent(this,IndexActivity.class));
//        finish();

    }

    @Override
    protected boolean showActionbar() {
        return false;
    }

    @Override
    public int getContentViewID() {
        return R.layout.appstart;
    }
}
