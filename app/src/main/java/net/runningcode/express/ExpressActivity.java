package net.runningcode.express;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
import net.runningcode.utils.PermissionUtils;
import net.runningcode.view.SearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;

import static net.runningcode.net.FastJsonRequest.getNewInstance;

/**
 * Created by Administrator on 2016/1/15.
 */
public class ExpressActivity extends BasicActivity implements View.OnClickListener,
        HttpListener<JSON>, AdapterView.OnItemClickListener {
    private static final int SCANNIN_GREQUEST_CODE = 1;
    @BindView(R.id.v_searchview)
    public SearchView vSearchView;
    @BindView(R.id.v_express_name)
    public TextView vExpressName;
    @BindView(R.id.v_express_tel)
    public TextView vExpressTel;
    @BindView(R.id.v_infos)
    public RecyclerView vInfos;
    @BindView(R.id.v_nos)
    public ListView vNos;
    @BindView(R.id.v_logo)
    public ImageView vLogo;
    @BindView(R.id.v_scan)
    public ImageView vScan;
    @BindView(R.id.v_result)
    public RelativeLayout vResult;

    private WaitDialog dialog;
    private JSONArray coms;
    ExpressListAdapter adapter;
    public JSONArray list;
    public String expNo;
    private List<Express> noList;
    private ExpNoAdapter noAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {


        list = new JSONArray();
        noList = new ArrayList<>();
        vInfos.setHasFixedSize(true);
        vInfos.setLayoutManager(new LinearLayoutManager(this));
        vInfos.setItemAnimator(new DefaultItemAnimator());

        adapter = new ExpressListAdapter(this, list);
        vInfos.setAdapter(adapter);
        noAdapter = new ExpNoAdapter(this, noList);
        vNos.setAdapter(noAdapter);
        vNos.setOnItemClickListener(this);

        vSearchView.setOnSearchClickListener(new SearchView.OnQueryClickListener() {
            @Override
            public void onClick(String key) {
                querByNO(key);
            }
        });

        vSearchView.setOnTextChangedListener(new SearchView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String ss) {
                loadData(ss);
            }
        });

        vScan.setOnClickListener(this);
        vExpressTel.setOnClickListener(this);

        dialog = new WaitDialog(this);

        initToolbar(R.drawable.icon_express);
        setTitle("快递");
    }

    private void loadData(String s) {
        if (TextUtils.isEmpty(s)) {
            vNos.setVisibility(View.GONE);
            return;
        }
        RealmResults<Express> tmp = Realm.getDefaultInstance().where(Express.class).contains("expNo", s).findAll();
//        List tmp = dao.queryBuilder().where(ExpressDao.Properties.ExpNo.like(s+"%")).list();
        if (tmp.size() > 0) {
            noList.clear();
            noList.addAll(tmp);
            noAdapter.notifyDataSetChanged();

            vNos.setVisibility(View.VISIBLE);
        } else {
            vNos.setVisibility(View.GONE);
        }

    }


    protected void setupWindowAnimations() {
//        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.drawable.gradient_toolbar_red);
        setupExitAnimations();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_scan:
                scanQRCode();
                break;
            case R.id.v_express_tel:
                callExp();
                break;
        }

    }

    private void callExp() {

        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_CALL);//直接拨打电话
        intent.setAction(Intent.ACTION_DIAL);//跳转到拨打电话界面
        intent.setData(Uri.parse("tel:" + vExpressTel.getText().toString()));
        startActivity(intent);

    }

    private void scanQRCode() {
        PermissionUtils.checkAndRequestPermission(this, new PermissionUtils.OnPermissionGrantCallback() {
            @Override
            public void onGranted(String[] permissions) {
                Intent intent = new Intent();
                intent.setClass(ExpressActivity.this, CaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }

            @Override
            public void onDenied(String[] strings) {
                DialogUtils.showShortToast(ExpressActivity.this, "未授予拍照权限，无法使用相关功能");
            }

        }, new String[]{Manifest.permission.CAMERA});
    }

    private void querByNO(String num) {
        if (TextUtils.isEmpty(num)) {
            DialogUtils.showShortToast(this, "单号没填你查个毛线？！");
            return;
        }
        try {
            Express express = new Express(num);
            Dao.saveAsync(express);
        } catch (Exception e) {

        }

        expNo = num;
        FastJsonRequest request = getNewInstance(URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS);
        request.add("nu", expNo);
        request.add("appid", 4001); //this is from baidu
        dialog.show();
        CallServer.getRequestInstance().add(this, request, this, true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    L.i(result);
                    vSearchView.setText(result);
                    querByNO(result);
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
    protected int getStatusBarColor() {
        return R.color.item_red;
    }


    @Override
    public void onSucceed(int what, Response<JSON> response) {
        L.i(what + " 返回：" + response.get());
        try {
            setResult(what, response);
        } catch (Exception e) {
            e.printStackTrace();
            onFailed(what, URLConstant.API_GET_COM_BY_EXPRESS_NO, null, new Exception("解析数据失败！"), 0, 0);
        }

    }

    private void setResult(int what, Response<JSON> response) {
        JSONObject result = (JSONObject) response.get();
        int status = result.getIntValue("error_code");
        L.i("请求：" + what + " 返回结果：" + status);
        if (status == 0) {
            dialog.dismiss();
            vResult.setVisibility(View.VISIBLE);
            final JSONObject data = result.getJSONObject("data");
            final JSONObject company = data.getJSONObject("company");
            vExpressName.setText(company.getString("fullname"));
            vExpressTel.setText(company.getString("tel"));
            list.clear();
            list.addAll(data.getJSONObject("info").getJSONArray("context"));
            adapter.notifyItemRangeChanged(0, list.size());
//            adapter.notifyDataSetChanged();
            L.i("logo地址：" + company.getJSONObject("icon").getString("normal"));

            Glide.with(this)
                    .load(company.getJSONObject("icon").getString("normal"))
                    .into(vLogo);
//            adapter.notifyItemRangeInserted(0,list.size());
        } else {
            dialog.dismiss();
            DialogUtils.showShortToast(this, "根据单号未获得快递信息！");
//            vResult.setText(result.getString("msg"));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommonUtil.hideInputMethod(this, vSearchView);
        final String expNo = noList.get(position).getExpNo();
        vSearchView.setText(expNo);
        querByNO(expNo);
        vNos.setVisibility(View.GONE);
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        dialog.dismiss();
        DialogUtils.showShortToast(this, "网络连接错误！" + message);
//        vResult.setText("请求失败："+message);
    }

}
