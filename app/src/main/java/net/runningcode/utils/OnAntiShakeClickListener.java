package net.runningcode.utils;

import android.view.View;

/**
 * 防抖点击的监听
 *
 */
public abstract class OnAntiShakeClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFastDoubleClick()) {
            return;
        }

        onAntiShakeClick(v);
    }

    public abstract void onAntiShakeClick(View v);
}
