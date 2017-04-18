package net.runningcode.simple_activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.utils.L;
import net.runningcode.utils.StringUtils;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/1/15.
 */
public class NumberActivity extends BasicActivity implements View.OnClickListener {
    private static final int MSG_TEXT_CHANGE = 1;
    private EditText vText;
    private ImageView vClear;
    private TextView vMainResult;
    private View vResult;
    private MyHandler mMyHandler;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }
    private void initView() {
        mMyHandler = new MyHandler(this);
        vClear = $(R.id.v_clear);

        vText = $(R.id.v_no);
        vResult = $(R.id.v_result);
        vMainResult = $(R.id.v_main_result);

        setEditBottomColor(vText,R.color.item_purple);
        vText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Message msg = mMyHandler.obtainMessage(MSG_TEXT_CHANGE);
                msg.obj = s;
                msg.sendToTarget();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    vClear.setVisibility(View.GONE);
                    vResult.setVisibility(View.GONE);
                }else {
                    vClear.setVisibility(View.VISIBLE);
                    vResult.setVisibility(View.VISIBLE);
                }
            }
        });

        vClear.setOnClickListener(this);
        vMainResult.setOnClickListener(this);
        initToolbar(R.color.item_purple,R.drawable.icon_num);
        setTitle("数字转换");
    }


    protected void setupWindowAnimations() {
//        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.drawable.gradient_toolbar_purple);
        setupExitAnimations();
    }

    @Override
    public void onClick(View view) {
        L.i("v getId:"+view.getId()+"  view:"+R.id.view+"  vText:"+ vText.getId());
        switch (view.getId()){
            case R.id.v_clear:
                vText.setText("");
                break;
        }

    }
    @Override
    public int getContentViewID() {
        return R.layout.activity_num2str;
    }

    private static class MyHandler extends Handler {
        private WeakReference<NumberActivity> activityRefefrence;
        public MyHandler(NumberActivity activity){
            activityRefefrence = new WeakReference<NumberActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            NumberActivity activity = activityRefefrence.get();
            if(activity != null){
                final String num = activity.vText.getText().toString();
                if (!TextUtils.isEmpty(num)){
                    try {
                        activity.vMainResult.setText(StringUtils.toTrandition(Integer.parseInt(num)));
                    } catch (Exception e) {
                        if (e instanceof NumberFormatException){
                            activity.vMainResult.setText("你牛X，输入这么大的数你会读吗？");
                        }else {
                            activity.vMainResult.setText("程序异常:"+e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
        }

    }
}
