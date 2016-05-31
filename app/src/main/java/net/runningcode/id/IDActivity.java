package net.runningcode.id;

import android.os.Bundle;
import android.text.TextUtils;
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
import net.runningcode.databinding.ActivityIdBinding;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;

/**
 * Created by Administrator on 2016/4/11.
 */
public class IDActivity extends BasicActivity implements View.OnClickListener,HttpListener {
    private EditText vID;
    private ImageView vQuery,vIcon;
//    private View vPanel;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        initToolbar(R.color.green_light,R.drawable.icon_id);

        vID = $(R.id.v_id_no);
        vIcon = $(R.id.v_icon);
        vQuery = $(R.id.v_query);

        vQuery.setOnClickListener(this);
        vID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    queryNo();
                    return true;
                }
                return false;
            }
        });
    }

    protected void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.color.green_light);
        setupExitAnimations();
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_id;
    }

    @Override
    public void onClick(View v) {
        queryNo();
    }

    private void queryNo() {
        String num = vID.getText().toString();
        if (TextUtils.isEmpty(num)){
            DialogUtils.showShortToast(this,"你到底想查谁？！");
            return;
        }
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_ID_INFO);
        request.add("idcard",num);

        CallServer.getRequestInstance().add(this,request,this,true,true);
    }

    @Override
    public void onSucceed(int what, Response response) {
        JSONObject result = (JSONObject) response.get();
        L.i("onsucceed:"+result);
        if (result.getIntValue("error") == -1){
            ((ActivityIdBinding)binding).setConstellation(result.getString("msg"));
            ((ActivityIdBinding)binding).setAddress("");
            vIcon.setImageResource(R.drawable.icon_error);
            return;
        }
        JSONObject data = result.getJSONObject("data");
        ((ActivityIdBinding)binding).setAddress(data.getString("address"));
        ((ActivityIdBinding)binding).setBirthday(data.getString("birthday"));
        ((ActivityIdBinding)binding).setZodiac("生肖："+data.getString("zodiac"));
        ((ActivityIdBinding)binding).setConstellation(data.getString("constellation"));

        final String gender = data.getString("gender");
        vIcon.setImageResource(CommonUtil.getDrawbleBySex(gender));
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        ((ActivityIdBinding)binding).setAddress("查询失败："+message);
    }
}
