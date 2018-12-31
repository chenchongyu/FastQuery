package net.runningcode.simple_activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import net.runningcode.view.SearchView;

import butterknife.BindView;

import static net.runningcode.net.FastJsonRequest.getNewInstance;

/**
 * Created by Administrator on 2016/4/11.
 */
public class PhoneActivity extends BasicActivity implements HttpListener {
    @BindView(R.id.v_icon)
    public ImageView vIcon;
    @BindView(R.id.v_result_panel)
    public View vPanel;
    @BindView(R.id.v_searchview)
    public SearchView searchView;
    @BindView(R.id.v_phone_no)
    public TextView vPhoneNO;
    @BindView(R.id.v_phone_result)
    public TextView vPhoneResult;
    @BindView(R.id.v_phone_zip)
    public TextView vZip;

    private String phone;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        initToolbar(R.drawable.icon_phone);
        setTitle("手机归属地");
//        setEditBottomColor(vPhone, R.color.item_orange);
        searchView.setOnSearchClickListener(new SearchView.OnQueryClickListener() {
            @Override
            public void onClick(String key) {
                phone = key;
                vPanel.setVisibility(View.VISIBLE);
                queryNo(key);
            }
        });

        searchView.setOnTextChangedListener(new SearchView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String ss) {
                if (TextUtils.isEmpty(ss)) {
                    vPanel.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void setupWindowAnimations() {
        setupEnterAnimations(R.drawable.gradient_toolbar_blue);
        setupExitAnimations();
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_phone;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.item_blue;
    }

    private void queryNo(String num) {

        if (!CommonUtil.isPhoneNum(num)) {
            DialogUtils.showShortToast(this, "请输入合法的电话号码！");
            return;
        }

        FastJsonRequest request = getNewInstance(URLConstant.API_GET_PHONE_INFO);
        request.add("phone", num);

        CallServer.getRequestInstance().add(this, request, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response response) {
        JSONObject result = (JSONObject) response.get();
        if (result.getIntValue("error_code") == 0) {
            vPhoneNO.setText(phone);
            JSONObject data = result.getJSONObject("result");
            String province = data.getString("province");
            String city = data.getString("city");
            String zip = data.getString("zip");
            final String supplier = data.getString("company");
            String area = TextUtils.equals(province, city) ? province : province + city;
            String text = area + "-" + supplier;
            vPhoneResult.setText(text);
            vZip.setText(getString(R.string.zip, zip));
//        vPhoneResult.setVisibility(View.VISIBLE);
            vIcon.setImageResource(CommonUtil.getDrawbleByPhone(supplier));
        } else {
            String reason = "查询失败：" + result.getString("reason");
            vPhoneResult.setText(reason);
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        String text = "查询失败：" + message;
        vPhoneResult.setText(text);
    }
}
