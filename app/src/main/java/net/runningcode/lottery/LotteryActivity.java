package net.runningcode.lottery;

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

/**
 * Created by Administrator on 2016/5/6.
 */
public class LotteryActivity extends BasicActivity implements HttpListener,View.OnClickListener {
    enum LOTTERY{
        DLT,SSQ,FC3D;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private Button vSsq,vDlt,vFc3d;
    private TextView vResultLabel;
    private LinearLayout vResult;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        shareTarget.setBackgroundResource(R.drawable.icon_lottery);
        shareTarget.setVisibility(View.VISIBLE);

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
        setupEnterAnimations(R.color.lottery_blue);
        setupExitAnimations();
    }

    private void query(LOTTERY lottery){
        L.i("当前彩票类型："+lottery.toString());

        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_LOTTERY_INFO);
        request.add("lotterycode",lottery.toString());
        request.add("recordcnt",1);

        CallServer.getRequestInstance().add(this,request,this,true,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.v_ssq:
                query(LOTTERY.SSQ);
                vResult.setBackgroundColor(getResources().getColor(R.color.btn_success));
                break;
            case R.id.v_dlt:
                query(LOTTERY.DLT);
                vResult.setBackgroundColor(getResources().getColor(R.color.btn_warn));
                break;
            case R.id.v_fc3d:
                query(LOTTERY.FC3D);
                vResult.setBackgroundColor(getResources().getColor(R.color.btn_info));
                break;

        }
    }

    @Override
    public void onSucceed(int what, Response response) {
        JSONObject result = (JSONObject) response.get();
        int errNum = result.getIntValue("errNum");
        if (errNum != 0){
            vResultLabel.setText(result.getString("retMsg"));
        }else {
            JSONObject data = result.getJSONObject("retData").getJSONArray("data").getJSONObject(0);
            String batch = data.getString("expect");
            String code = data.getString("openCode");

            parseCode(code);

            vResultLabel.setText("第"+batch+"期开奖结果：");

        }
    }

    private void parseCode(String code) {
        vResult.removeAllViews();

        String[] codes = TextUtils.split(code,"\\+");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
//        layoutParams.setMarginEnd(10);
        layoutParams.setMargins(0,0,10,0);
        for (int i=0;i<codes.length;i++){
            String subCode = codes[i];
            String[] orgCodes = TextUtils.split(subCode,",");
            for (String c:orgCodes){
                CircleTextView textView = new CircleTextView(this);
                textView.setLayoutParams(layoutParams);
                textView.setText(c);
                textView.setCircleWidth(3);
                textView.setRadius(36);
//                textView.setBackgroundColor(Color.YELLOW);
                if (i == 0){
                    textView.setCircleColor(getResources().getColor(R.color.red));
//                    textView.setBackgroundResource(R.drawable.bg_circle_red);
                    textView.setTextColor(getResources().getColor(R.color.red));
                }else{
                    textView.setCircleColor(getResources().getColor(R.color.blue));
                    textView.setTextColor(getResources().getColor(R.color.blue));
                }

                vResult.addView(textView);
            }
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {

    }


    @Override
    public int getContentViewID() {
        return R.layout.activity_lottery;
    }
}
