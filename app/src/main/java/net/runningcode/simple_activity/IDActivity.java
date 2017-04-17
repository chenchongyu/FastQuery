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
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;

import static net.runningcode.net.FastJsonRequest.getNewInstance;

/**
 * Created by Administrator on 2016/4/11.
 */
public class IDActivity extends BasicActivity implements View.OnClickListener,HttpListener {
    private EditText vID;
    private ImageView vQuery,vIcon,vClear;
    private View vLine;
    private View vPanel;
    TextView vBirthday,vConstellation,vZodiac,vAddress;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        initToolbar(R.color.id_red,R.drawable.icon_id);
        setTitle("身份证");
        vID = $(R.id.v_id_no);
        vIcon = $(R.id.v_icon);
        vQuery = $(R.id.v_query);
        vClear = $(R.id.v_clear);
        vLine = $(R.id.view);
        vPanel = $(R.id.v_result_panel);
        vBirthday = $(R.id.v_birthday);
        vConstellation = $(R.id.v_constellation);
        vZodiac = $(R.id.v_zodiac);
        vAddress = $(R.id.v_address);

        vQuery.setOnClickListener(this);
        vClear.setOnClickListener(this);

        setEditBottomColor(vID,R.color.id_red);
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
        vID.addTextChangedListener(new TextWatcher() {
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
        setupEnterAnimations(R.color.id_red);
        setupExitAnimations();
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.v_query:
                queryNo();
                break;
            case R.id.v_clear:
                vID.setText("");
                break;
        }
    }

    private void queryNo() {
        String num = vID.getText().toString();
        if (TextUtils.isEmpty(num)){
            DialogUtils.showShortToast(this,"你到底想查谁？！");
            return;
        }
        FastJsonRequest request = getNewInstance(URLConstant.API_GET_ID_INFO);
        request.add("id",num);

        CallServer.getRequestInstance().add(this,request,this,true,true);
    }

    @Override
    public void onSucceed(int what, Response response) {
        JSONObject result = (JSONObject) response.get();
        L.i("onsucceed:"+result);
        if (result.getIntValue("error_code") != 0){
            vAddress.setText(result.getString("reason"));
            vIcon.setImageResource(R.drawable.icon_error);
            return;
        }
        JSONObject data = result.getJSONObject("result");
        vAddress.setText(data.getString("address"));
        vBirthday.setText(data.getString("birthday"));
//        ((ActivityIdBinding)binding).setConstellation(data.getString("constellation"));

        final String gender = data.getString("sex");
        vIcon.setImageResource(CommonUtil.getDrawbleBySex(gender));
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        vAddress.setText("查询失败："+message);
    }
}
