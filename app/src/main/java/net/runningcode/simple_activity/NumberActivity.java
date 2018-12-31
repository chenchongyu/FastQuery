package net.runningcode.simple_activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.utils.StringUtils;
import net.runningcode.view.SearchView;

import java.lang.ref.WeakReference;

import butterknife.BindView;

/**
 * Created by Administrator on 2016/1/15.
 */
public class NumberActivity extends BasicActivity {
    private static final int MSG_TEXT_CHANGE = 1;
    @BindView(R.id.v_searchview)
    public SearchView vSearchView;
    @BindView(R.id.v_main_result)
    public TextView vMainResult;
    private MyHandler mMyHandler;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        mMyHandler = new MyHandler(this);
        vSearchView.hideQueryBtn();
        vSearchView.setOnTextChangedListener(new SearchView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String ss) {
                Message msg = mMyHandler.obtainMessage(MSG_TEXT_CHANGE);
                msg.obj = ss;
                msg.sendToTarget();

                if (TextUtils.isEmpty(ss)) {
                    vMainResult.setVisibility(View.GONE);
                } else {
                    vMainResult.setVisibility(View.VISIBLE);
                }
            }
        });

        initToolbar(R.drawable.icon_num);
        setTitle("数字转换");
    }


    protected void setupWindowAnimations() {
//        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.drawable.gradient_toolbar_purple);
        setupExitAnimations();
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_num2str;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.item_purple;
    }

    private static class MyHandler extends Handler {
        private WeakReference<NumberActivity> activityRefefrence;

        public MyHandler(NumberActivity activity) {
            activityRefefrence = new WeakReference<NumberActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NumberActivity activity = activityRefefrence.get();
            if (activity != null) {
                final String num = (String) msg.obj;
                if (!TextUtils.isEmpty(num)) {
                    try {
                        activity.vMainResult.setText(StringUtils.toTrandition(Integer.parseInt(num)));
                    } catch (Exception e) {
                        if (e instanceof NumberFormatException) {
                            activity.vMainResult.setText("你牛X，输入这么大的数你会读吗？");
                        } else {
                            activity.vMainResult.setText("程序异常:" + e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
        }

    }
}
