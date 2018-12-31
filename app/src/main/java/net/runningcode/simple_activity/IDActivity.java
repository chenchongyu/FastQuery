package net.runningcode.simple_activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
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
import net.runningcode.view.SearchView;

import butterknife.BindDrawable;
import butterknife.BindView;

import static net.runningcode.net.FastJsonRequest.getNewInstance;

/**
 * Created by Administrator on 2016/4/11.
 */
public class IDActivity extends BasicActivity implements HttpListener {
    @BindView(R.id.v_icon)
    public ImageView vIcon;
    @BindView(R.id.search_view)
    public SearchView searchView;

    @BindView(R.id.v_birthday)
    public TextView vBirthday;
    @BindView(R.id.v_constellation)
    public TextView vConstellation; //星座
    @BindView(R.id.v_zodiac)
    public TextView vZodiac; //生肖
    @BindView(R.id.v_address)
    public TextView vAddress;
    @BindView(R.id.v_result_panel)
    public View vPanelView;

    @BindDrawable(R.drawable.gradient_toolbar_blue)
    Drawable toolbarBg;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        initToolbar(R.drawable.icon_id);
        setTitle("身份证");

//        setEditBottomColor(vID, R.color.item_blue);
        searchView.setOnSearchClickListener(new SearchView.OnQueryClickListener() {
            @Override
            public void onClick(String key) {
                queryNo(key);
            }
        });

        searchView.setOnTextChangedListener(new SearchView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String ss) {
                if (TextUtils.isEmpty(ss)) {
                    vPanelView.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.drawable.gradient_toolbar_blue);
        setupExitAnimations();
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_id;
    }

    private void queryNo(String key) {
        if (TextUtils.isEmpty(key)) {
            DialogUtils.showShortToast(this, "你到底想查谁？！");
            return;
        }
        FastJsonRequest request = getNewInstance(URLConstant.API_GET_ID_INFO);
        request.add("cardno", key);

        CallServer.getRequestInstance().add(this, request, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response response) {
        vPanelView.setVisibility(View.VISIBLE);
        JSONObject result = (JSONObject) response.get();
        L.i("onsucceed:" + result);
        if (result.getIntValue("error_code") != 0) {
            vAddress.setText(result.getString("reason"));
            vIcon.setImageResource(R.drawable.icon_error);
            return;
        }
        JSONObject data = result.getJSONObject("result");
        vAddress.setText(data.getString("area"));
        vBirthday.setText(data.getString("birthday"));
//        ((ActivityIdBinding)binding).setConstellation(data.getString("constellation"));

        final String gender = data.getString("sex");
        vIcon.setImageResource(CommonUtil.getDrawbleBySex(gender));
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        vPanelView.setVisibility(View.VISIBLE);
        vAddress.setText("查询失败：" + message);
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.item_blue;
    }
}
