package net.runningcode.simple_activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.constant.Constants;
import net.runningcode.constant.URLConstant;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;
import net.runningcode.utils.SPUtils;

/**
 * Created by Administrator on 2016/1/15.
 */
public class CarActivity extends BasicActivity implements View.OnClickListener,HttpListener {
    private static final int MSG_TEXT_CHANGE = 1;
    private EditText vText;
    private ImageView vClear,vQuery;
    private TextView vMainResult;
    private View vResult;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }
    private void initView() {
        vClear = $(R.id.v_clear);
        vQuery = $(R.id.v_query);

        vText = $(R.id.v_no);
        vResult = $(R.id.v_result);
        vMainResult = $(R.id.v_main_result);

        String no = SPUtils.getInstance(null).getString(Constants.KEY_CAR_NO,"");
        vText.setText(no);

        vText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    vClear.setVisibility(View.GONE);
                    vResult.setVisibility(View.GONE);
                }else {
                    vClear.setVisibility(View.VISIBLE);

                }
            }
        });

        vClear.setOnClickListener(this);
        vQuery.setOnClickListener(this);
        vMainResult.setOnClickListener(this);
        initToolbar(R.color.car_blue,R.drawable.icon_car);

    }


    protected void setupWindowAnimations() {
//        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.color.car_blue);
        setupExitAnimations();
    }

    @Override
    public void onClick(View view) {
        L.i("v getId:"+view.getId()+"  view:"+R.id.view+"  vText:"+ vText.getId());
        switch (view.getId()){
            case R.id.v_clear:
                vText.setText("");
                break;
            case R.id.v_query:
                queryNo();
                break;
        }

    }

    private void queryNo() {
        String num = vText.getText().toString();
        if (TextUtils.isEmpty(num)){
            DialogUtils.showShortToast(this,"你到底想不想查？！");
            return;
        }
        SPUtils.getInstance(null).edit().putString(Constants.KEY_CAR_NO,num);
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_YAOHAO);
//        ?name=6704105512283&city=%E5%8C%97%E4%BA%AC&format=json&resource_id=4003
        request.add("name",num);
        request.add("city","北京");

        CallServer.getRequestInstance().add(this,request,this,true,true);
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_car;
    }

    @Override
    public void onSucceed(int what, Response response) {
        //{"msg":"","status":"0","data":{"tele_number":"","0":{"status":"1","dispNum":"0","disp_data":[]}}}
        //{"msg":"","status":"0","data":[{"status":"1","dispNum":"0","disp_data":[]}]}
        vResult.setVisibility(View.VISIBLE);
        JSONObject result = (JSONObject)response.get();
        JSONArray data = result.getJSONArray("data");
        JSONArray dispData = data.getJSONObject(0).getJSONArray("disp_data");
        if (dispData ==null || dispData.size() == 0){
            vMainResult.setText("很抱歉！该编号本次未中签！");
        }else {
            vMainResult.setText("恭喜！该编号已中签！您可以登陆摇号官网打印《小客车配置指标确认通知书》办理购车、上牌等手续");
        }

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        vMainResult.setText("查询失败:"+message.getLocalizedMessage());
    }

}
