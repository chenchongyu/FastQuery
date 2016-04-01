package net.runningcode;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import net.runningcode.constant.Constants;
import net.runningcode.utils.SPUtils;

/**
 * Created by Administrator on 2016/1/15.
 */
public class LogoActivity extends BasicActivity{

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        String isLogin = SPUtils.getString(this, Constants.KEY_USER_NAME,"");
        if (TextUtils.isEmpty(isLogin)){
            startActivity(new Intent(this,IndexActivity.class));
        }
        finish();
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
