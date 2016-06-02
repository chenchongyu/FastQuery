package net.runningcode.express;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import net.runningcode.bean.Express;
import net.runningcode.constant.URLConstant;
import net.runningcode.dao.Dao;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.net.WaitDialog;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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
    private View vLine;
    private WaitDialog dialog;
    private JSONArray coms;
    ExpressListAdapter adapter;
    public JSONArray list;
    public String expNo;
    private RelativeLayout rootView,vResult;
    private List<Express> noList;
    private ExpNoAdapter noAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }
    private void initView() {

        rootView = $(R.id.reveal_root);
        vResult = $(R.id.v_result);
        vExpressName = $(R.id.v_express_name);
        vExpressTel = $(R.id.v_express_tel);
        vExpressTel.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        vClear = $(R.id.v_clear);
        vLine = $(R.id.view);

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

        vExpressNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    querByNO();
                    return true;
                }
                return false;
            }
        });
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
        vExpressTel.setOnClickListener(this);
        vClear.setOnClickListener(this);

        dialog = new WaitDialog(this);

        initToolbar(R.color.red,R.drawable.icon_express);

    }

    private void loadData(String s) {
        if (TextUtils.isEmpty(s)){
            vNos.setVisibility(View.GONE);
            vClear.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
            return;
        }else{
            vClear.setVisibility(View.VISIBLE);
            vLine.setVisibility(View.VISIBLE);
        }
        RealmResults<Express> tmp = Realm.getDefaultInstance().where(Express.class).contains("expNo",s).findAll();
//        List tmp = dao.queryBuilder().where(ExpressDao.Properties.ExpNo.like(s+"%")).list();
        if (tmp.size() > 0){
            noList.clear();
            noList.addAll(tmp);
            noAdapter.notifyDataSetChanged();

            vNos.setVisibility(View.VISIBLE);
        }else {
            vNos.setVisibility(View.GONE);
        }

    }


    protected void setupWindowAnimations() {
//        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
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
        L.i("v getId:"+view.getId()+"  view:"+R.id.view+"  vExpressNo:"+vExpressNo.getId());
        switch (view.getId()){
            case R.id.v_query:
                querByNO();
                break;
            case R.id.v_scan:
                scanQRCode();
                break;
            case R.id.v_express_tel:
                callExp();
                break;
            case R.id.v_clear:
                vExpressNo.setText("");
                break;
        }

    }

    private void callExp() {
        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_CALL);//直接拨打电话
        intent.setAction(Intent.ACTION_DIAL);//跳转到拨打电话界面
        intent.setData(Uri.parse("tel:"+vExpressTel.getText().toString()));
        startActivity(intent);
    }

    private void scanQRCode() {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }

    private void querByNO() {
        String num = vExpressNo.getText().toString();
        if (TextUtils.isEmpty(num)){
            DialogUtils.showShortToast(this,"单号没填你查个毛线？！");
            return;
        }
        try {
            Express express = new Express(num);
            Dao.saveAsync(express);
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
                    vExpressNo.setText(bundle.getString("result"));
                    querByNO();
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
        L.i(what+" 返回："+response.get());
        switch (what){
            case URLConstant.API_GET_COM_BY_EXPRESS_NO_WHAT:
                JSON data = response.get();
                if(data instanceof JSONObject){
                    onFailed(what,URLConstant.API_GET_COM_BY_EXPRESS_NO,null,new Exception("根据单号未获得快递信息！"),0,0);
                    return;
                }else
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
        querByNO();
        vNos.setVisibility(View.GONE);
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
