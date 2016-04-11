package net.runningcode.phone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.constant.URLConstant;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DialogUtils;

/**
 * Created by Administrator on 2016/4/11.
 */
public class PhoneActivity extends BasicActivity implements View.OnClickListener,HttpListener {
    private EditText vPhone;
    private ImageView vQuery,vIcon;
    private TextView vPhoneNo,vResult;
    private View vPanel;
    private Interpolator interpolator;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        shareTarget.setBackgroundResource(R.drawable.icon_phone);

        vPanel = $(R.id.v_result_panel);
        vPhone = $(R.id.v_phone);
        vIcon = $(R.id.v_icon);
        vPhoneNo = $(R.id.v_phone_no);
        vResult = $(R.id.v_phone_result);
        vQuery = $(R.id.v_query);

        vQuery.setOnClickListener(this);
    }

    protected void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.color.colorPrimaryGreen);
        setupExitAnimations();
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_phone;
    }

    @Override
    public void onClick(View v) {
        queryNo();
    }

    private void queryNo() {
        String num = vPhone.getText().toString();

        if (!CommonUtil.isPhoneNum(num)){
            DialogUtils.showShortToast(this,"请输入合法的电话号码！");
            return;
        }

        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_PHONE_INFO);
        request.add("phone",num);

        CallServer.getRequestInstance().add(this,request,this,true,true);
    }

    @Override
    public void onSucceed(int what, Response response) {
        vPanel.setVisibility(View.VISIBLE);
        JSONObject result = (JSONObject) response.get();
        JSONObject data = result.getJSONObject("retData");
        vPhoneNo.setText(data.getString("phone"));
        String s = TextUtils.equals(data.getString("province"),data.getString("city"))?data.getString("city")
                :data.getString("province")+data.getString("city");
        vResult.setText(s+data.getString("supplier"));
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        vPanel.setVisibility(View.VISIBLE);
        vResult.setText("查询失败："+message);
    }
}
