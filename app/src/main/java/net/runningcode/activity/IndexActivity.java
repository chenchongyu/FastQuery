package net.runningcode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.adapter.ExpressListAdapter;
import net.runningcode.constant.URLConstant;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.net.WaitDialog;
import net.runningcode.utils.L;

/**
 * Created by Administrator on 2016/1/15.
 */
public class IndexActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON> {
    private static final int SCANNIN_GREQUEST_CODE = 1;
    private EditText vExpressNo;
    private TextView vResult;
    private RecyclerView vInfos;
    private ImageView vLogo,vScan;
    private Button vQuery;
    private WaitDialog dialog;
    private JSONArray coms;
    ExpressListAdapter adapter;
    public JSONArray list;
    public String expNo;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    @Override
    protected boolean showActionbar() {
        return true;
    }

    private void initView() {

        list = new JSONArray();
        vInfos = $(R.id.v_infos);
        vInfos.setHasFixedSize(true);
        vInfos.setLayoutManager(new LinearLayoutManager(this));
        vInfos.setItemAnimator(new DefaultItemAnimator());
        adapter = new ExpressListAdapter(this,list);
        vInfos.setAdapter(adapter);

        vScan = $(R.id.v_scan);
        vLogo = $(R.id.v_logo);
        vExpressNo = $(R.id.v_no);
        vResult = $(R.id.v_result);
        vQuery = $(R.id.v_query);

        vQuery.setOnClickListener(this);
        vScan.setOnClickListener(this);

        dialog = new WaitDialog(this);

        setActionBarColor(R.color.colorPrimary);
        setStatusBarColor(R.color.colorPrimaryDark);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.v_query:
                querByNO(vExpressNo.getText().toString());
                break;
            case R.id.v_scan:
                scanQRCode();
                break;
        }

    }

    private void scanQRCode() {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }

    private void querByNO(String num) {
        expNo = num;
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_COM_BY_EXPRESS_NO);
        request.add("num", expNo);
        dialog.show();
        CallServer.getRequestInstance().add(this, request, this, true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    L.i(bundle.getString("result"));
                    querByNO(bundle.getString("result"));
                    //显示扫描到的内容
//                    mTextView.setText(bundle.getString("result"));
                    //显示
//                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_index;
    }


    @Override
    public void onSucceed(int what, Response<JSON> response) {
        switch (what){
            case URLConstant.API_GET_COM_BY_EXPRESS_NO_WHAT:
                coms = (JSONArray) response.get();
                getExpInfo(0);
                break;
            case URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT:
            case URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT1:
            case URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT2:
                setResult(what, response);
                break;
        }

    }

    private void setResult(int what,Response<JSON> response) {
        JSONObject result = (JSONObject) response.get();
        String status = result.getString("status");
        L.i("请求："+what+" 返回结果："+status);
        if (TextUtils.equals(status, "0")){
            dialog.dismiss();
            final JSONObject data = result.getJSONObject("data");
            final JSONObject company = data.getJSONObject("company");
            vResult.setText(company.getString("shortname")+(data.getString("notice") == null?"":data.getString("notice")));

            list.clear();
            list.addAll(data.getJSONObject("info").getJSONArray("context"));
            adapter.notifyDataSetChanged();
            L.i("logo地址："+company.getJSONObject("icon").getString("normal"));
            Glide.with(this)
                    .load(company.getJSONObject("icon").getString("normal"))
                    .into(vLogo);
//            adapter.notifyItemRangeInserted(0,list.size());
        }else if (what < URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT2){
            getExpInfo(what+1-URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT);
        }else {
            dialog.dismiss();
            vResult.setText(result.getString("msg"));
        }
    }

    private void getExpInfo(int i) {
        JSONObject com = coms.getJSONObject(i);
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS);
        request.add("com",com.getString("comCode"));
        request.add("nu",expNo);
        request.add("appid",4001);//这个值是从百度页面扒的
        CallServer.getRequestInstance().add(this,URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT+i,
                request, this, true, false);

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        dialog.dismiss();
        vResult.setText("请求失败："+message);
    }
}
