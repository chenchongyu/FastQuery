package net.runningcode.simple_activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.constant.URLConstant;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/6.
 */

public class ShortUrlActivity extends BasicActivity implements HttpListener{
    @BindView(R.id.v_url)
    EditText mVUrl;
    @BindView(R.id.v_gen)
    TextView mVGen;
    @BindView(R.id.v_result)
    TextView mVResult;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        ButterKnife.bind(this);
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_short_url;
    }

    @OnClick(R.id.v_gen)
    public void onViewClicked() {
        String url = mVUrl.getText().toString();
        if (TextUtils.isEmpty(url)){
            DialogUtils.showShortToast(this,"不填网址你生成个毛线？");
            return;
        }
        if (!url.startsWith("http://")){
            url = "http://"+url;
        }
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_SHORT_URL);
        request.add("url",url);
        request.add("type","1");

        CallServer.getRequestInstance().add(this,request,this,true,true);
    }

    @Override
    public void onSucceed(int what, Response response) {
        JSONObject result = (JSONObject) response.get();
        JSONObject data = result.getJSONObject("result");

        String s = data.getString("short_url");

        mVResult.setText(s);

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {

    }
}
