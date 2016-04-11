package net.runningcode.express;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.RunningCodeApplication;
import net.runningcode.bean.Express;
import net.runningcode.constant.URLConstant;
import net.runningcode.dao.ExpressDao;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.net.WaitDialog;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/15.
 */
public class ExpressActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON>,AdapterView.OnItemClickListener {
    private static final int SCANNIN_GREQUEST_CODE = 1;
    private EditText vExpressNo;
    private TextView vExpressName,vExpressTel;
    private RecyclerView vInfos;
    private ListView vNos;
    private ImageView vLogo,vScan,vQuery,vClear;
    private WaitDialog dialog;
    private JSONArray coms;
    ExpressListAdapter adapter;
    public JSONArray list;
    public String expNo;
    private RelativeLayout rootView,vResult;
    private Interpolator interpolator;
    private ExpressDao dao;
    private List<Express> noList;
    private ExpNoAdapter noAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }
    private void initView() {
        dao = RunningCodeApplication.getInstance().getDaoSession().getExpressDao();

        shareTarget.setBackgroundResource(R.drawable.icon_express);
        shareTarget.setVisibility(View.VISIBLE);

        rootView = $(R.id.reveal_root);
        vResult = $(R.id.v_result);
        vExpressName = $(R.id.v_express_name);
        vExpressTel = $(R.id.v_express_tel);

        list = new JSONArray();
        noList = new ArrayList<>();
        vInfos = $(R.id.v_infos);
        vInfos.setHasFixedSize(true);
        vInfos.setLayoutManager(new LinearLayoutManager(this));
        vInfos.setItemAnimator(new DefaultItemAnimator());

        vNos = $(R.id.v_nos);

        adapter = new ExpressListAdapter(this,list);
        vInfos.setAdapter(adapter);
        noAdapter = new ExpNoAdapter(this,noList);
        vNos.setAdapter(noAdapter);
        vNos.setOnItemClickListener(this);

        vScan = $(R.id.v_scan);
        vLogo = $(R.id.v_logo);
        vExpressNo = $(R.id.v_no);
        vQuery = $(R.id.v_query);

        vExpressNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loadData(s.toString());
            }
        });
        vQuery.setOnClickListener(this);
        vScan.setOnClickListener(this);

        dialog = new WaitDialog(this);

    }

    private void loadData(String s) {
        if (TextUtils.isEmpty(s)){
            vNos.setVisibility(View.GONE);
            return;
        }
        List tmp = dao.queryBuilder().where(ExpressDao.Properties.ExpNo.like(s+"%")).list();
        if (tmp.size() > 0){
            noList.clear();
            noList.addAll(tmp);
            adapter.notifyDataSetChanged();

            vNos.setVisibility(View.VISIBLE);
        }else {
            vNos.setVisibility(View.GONE);
        }

    }


    protected void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.color.red);
        setupExitAnimations();
    }

    private void animateButtonsIn() {
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            child.animate()
                    .setStartDelay(100 + i * 100)
                    .setInterpolator(interpolator)
                    .alpha(1)
                    .scaleX(1)
                    .scaleY(1);
        }
    }

    private void animateButtonsOut() {
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            child.animate()
                    .setStartDelay(i)
                    .setInterpolator(interpolator)
                    .alpha(0)
                    .scaleX(0f)
                    .scaleY(0f);
        }
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
        try {
            dao.insert(new Express(num));
        }catch (Exception e){

        }

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
        return R.layout.activity_express;
    }


    @Override
    public void onSucceed(int what, Response<JSON> response) {
        switch (what){
            case URLConstant.API_GET_COM_BY_EXPRESS_NO_WHAT:
                coms = (JSONArray) response.get();
                getExpInfo(0);
                break;
            default:
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
            vResult.setVisibility(View.VISIBLE);
            final JSONObject data = result.getJSONObject("data");
            final JSONObject company = data.getJSONObject("company");
            vExpressName.setText(company.getString("fullname"));
            vExpressTel.setText(company.getString("tel"));
            list.clear();
            list.addAll(data.getJSONObject("info").getJSONArray("context"));
            adapter.notifyItemRangeChanged(0,list.size());
//            adapter.notifyDataSetChanged();
            L.i("logo地址："+company.getJSONObject("icon").getString("normal"));
            Glide.with(this)
                    .load(company.getJSONObject("icon").getString("normal"))
                    .into(vLogo);
//            adapter.notifyItemRangeInserted(0,list.size());
        }else if ((what - URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT)<coms.size()-1){
            getExpInfo(what-URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT+1);
        }else {
            dialog.dismiss();
            DialogUtils.showShortToast(this,"根据单号未获得快递信息！");
//            vResult.setText(result.getString("msg"));
        }
    }

    private void getExpInfo(int i) {
        L.i("第"+i+"次请求");
        JSONObject com = coms.getJSONObject(i);
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS);
        request.add("com",com.getString("comCode"));
        request.add("nu",expNo);
        request.add("appid",4001);//这个值是从百度页面扒的
        CallServer.getRequestInstance().add(this,URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT+i,
                request, this, true, false);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommonUtil.hideInputMethod(this,vExpressNo);
        final String expNo = noList.get(position).getExpNo();
        vExpressNo.setText(expNo);
        querByNO(expNo);
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        dialog.dismiss();
        DialogUtils.showShortToast(this,"网络连接错误！"+message);
//        vResult.setText("请求失败："+message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallServer.getRequestInstance().cancelAll();
    }

}
