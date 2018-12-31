package net.runningcode.simple_activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.constant.URLConstant;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.utils.L;
import net.runningcode.view.CircleTextView;

import static net.runningcode.net.FastJsonRequest.getNewInstance;

/**
 * Created by Administrator on 2016/5/6.
 */
public class LotteryActivity extends BasicActivity implements HttpListener,
        View.OnClickListener {
    enum LOTTERY {
        DLT, SSQ, FCSD;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private Button vSsq, vDlt, vFc3d;
    private TextView vResultLabel;
    private LinearLayout vResult;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        initToolbar(R.drawable.icon_lottery);
        setTitle("彩票");
        vSsq = $(R.id.v_ssq);
        vDlt = $(R.id.v_dlt);
        vFc3d = $(R.id.v_fc3d);
        vResultLabel = $(R.id.v_result_lable);
        vResult = $(R.id.v_result);

        vSsq.setOnClickListener(this);
        vDlt.setOnClickListener(this);
        vFc3d.setOnClickListener(this);
    }

    protected void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.drawable.gradient_toolbar_orange);
        setupExitAnimations();
    }

    private void query(LOTTERY lottery) {
        L.i("当前彩票类型：" + lottery.toString());

        FastJsonRequest request = getNewInstance(URLConstant.API_GET_LOTTERY_INFO);
        request.add("lottery_id", lottery.toString());
//        request.add("recordcnt", 1);

        CallServer.getRequestInstance().add(this, request, this, true, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_ssq:
                query(LOTTERY.SSQ);
                vResult.setBackgroundColor(getResources().getColor(R.color.btn_success));
                break;
            case R.id.v_dlt:
                query(LOTTERY.DLT);
                vResult.setBackgroundColor(getResources().getColor(R.color.btn_warn));
                break;
            case R.id.v_fc3d:
                query(LOTTERY.FCSD);
                vResult.setBackgroundColor(getResources().getColor(R.color.btn_info));
                break;

        }
    }

    @Override
    public void onSucceed(int what, Response response) {
        JSONObject result = (JSONObject) response.get();
        int errNum = result.getIntValue("error_code");
        if (errNum != 0) {
            vResultLabel.setText(result.getString("reason"));
        } else {
            final JSONObject result1 = result.getJSONObject("result");
            if (result1 == null) {
                vResultLabel.setText("暂无数据");
                return;
            }
            String batch = result1.getString("lottery_no");
            String code = result1.getString("lottery_res");

            parseCode(code);

            String text = "第" + batch + "期开奖结果：";
            vResultLabel.setText(text);

        }
    }

    private void parseCode(String code) {
        vResult.removeAllViews();

        String[] codes = TextUtils.split(code, ",");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
//        layoutParams.setMarginEnd(10);
        layoutParams.setMargins(0, 0, 10, 0);
        for (int i = 0; i < codes.length; i++) {
            String subCode = codes[i];
            String[] orgCodes = TextUtils.split(subCode, ",");
            for (String c : orgCodes) {
                CircleTextView textView = new CircleTextView(this);
                textView.setLayoutParams(layoutParams);
                textView.setText(c);
                textView.setCircleWidth(3);
                textView.setRadius(50);
                textView.setTextSize(40f);
//                textView.setBackgroundColor(Color.YELLOW);
                if (i == 0) {
                    textView.setCircleColor(getResources().getColor(R.color.red));
//                    textView.setBackgroundResource(R.drawable.bg_circle_red);
                    textView.setTextColor(getResources().getColor(R.color.red));
                } else {
                    textView.setCircleColor(getResources().getColor(R.color.blue));
                    textView.setTextColor(getResources().getColor(R.color.blue));
                }

                vResult.addView(textView);
            }
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        vResultLabel.setText("请求失败！原因：" + message.getLocalizedMessage());
    }


    @Override
    public int getContentViewID() {
        return R.layout.activity_lottery;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.item_orange;
    }
}
