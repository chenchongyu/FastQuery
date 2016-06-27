package net.runningcode.simple_activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.constant.URLConstant;
import net.runningcode.databinding.ActivityPhoneBinding;
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
    private ImageView vQuery,vIcon,vClear;
    private View vLine;
//    private View vPanel;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        initToolbar(R.color.phone_green,R.drawable.icon_phone);

        vPhone = $(R.id.v_phone);
        vIcon = $(R.id.v_icon);
        vQuery = $(R.id.v_query);
        vClear = $(R.id.v_clear);
        vLine = $(R.id.view);

        vClear.setOnClickListener(this);
        vQuery.setOnClickListener(this);

        vPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    queryNo();
                    return true;
                }
                return false;
            }
        });

        vPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    vClear.setVisibility(View.VISIBLE);
                    vLine.setVisibility(View.VISIBLE);
                }else {
                    vClear.setVisibility(View.GONE);
                    vLine.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.color.phone_green);
        setupExitAnimations();
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_phone;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.v_query:
                queryNo();
                break;
            case R.id.v_clear:
                vPhone.setText("");
                break;
        }
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
        JSONObject result = (JSONObject) response.get();
        JSONObject data = result.getJSONObject("retData");
        ((ActivityPhoneBinding)binding).setPhoneNum(data.getString("phone"));
        String s = TextUtils.equals(data.getString("province"),data.getString("city"))?data.getString("city")
                :data.getString("province")+data.getString("city");
        final String supplier = data.getString("supplier");
        ((ActivityPhoneBinding)binding).setSupplier(s+ supplier);
        vIcon.setImageResource(CommonUtil.getDrawbleByPhone(supplier));
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        ((ActivityPhoneBinding)binding).setSupplier("查询失败："+message);
    }
}
