package net.runningcode.bank;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.constant.Constants;
import net.runningcode.constant.URLConstant;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.net.WaitDialog;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;

/**
 * Created by Administrator on 2016/1/15.
 */
public class BankActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON> {
    private static final int SCANNIN_GREQUEST_CODE = 1;
    private EditText vBankNo;
    private ImageView vLogo,vScan,vQuery,vClear;
    private TextView vBankName,vBankTel;
    private WaitDialog dialog;
    private String cardNo;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }
    private void initView() {
        vClear = $(R.id.v_clear);

        vScan = $(R.id.v_scan);
        vLogo = $(R.id.v_logo);
        vBankNo = $(R.id.v_no);
        vQuery = $(R.id.v_query);
        vBankName = $(R.id.v_bank_name);
        vBankTel = $(R.id.v_bank_tel);

        vBankNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    querByNO();
                    return true;
                }
                return false;
            }
        });
        vBankNo.addTextChangedListener(new TextWatcher() {
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
        vClear.setOnClickListener(this);

        dialog = new WaitDialog(this);

        initToolbar(R.color.green_light,R.drawable.icon_bank);

    }

    private void loadData(String s) {
        /*if (TextUtils.isEmpty(s)){
            vNos.setVisibility(View.GONE);
            vClear.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
            return;
        }else{
            vClear.setVisibility(View.VISIBLE);
            vLine.setVisibility(View.VISIBLE);
        }
        RealmResults<Express> tmp = Realm.getDefaultInstance().where(Express.class).contains("cardNo",s).findAll();
//        List tmp = dao.queryBuilder().where(ExpressDao.Properties.ExpNo.like(s+"%")).list();
        if (tmp.size() > 0){
            noList.clear();
            noList.addAll(tmp);
            noAdapter.notifyDataSetChanged();

            vNos.setVisibility(View.VISIBLE);
        }else {
            vNos.setVisibility(View.GONE);
        }*/

    }


    protected void setupWindowAnimations() {
//        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.color.green_light);
        setupExitAnimations();
    }

    @Override
    public void onClick(View view) {
        L.i("v getId:"+view.getId()+"  view:"+R.id.view+"  vBankNo:"+ vBankNo.getId());
        switch (view.getId()){
            case R.id.v_query:
                querByNO();
                break;
            case R.id.v_scan:
                scanQRCode();
                break;
            case R.id.v_clear:
                vBankNo.setText("");
                break;
        }

    }


    private void scanQRCode() {
//        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
//        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false); // default: false
//        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
//        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

//        startActivityForResult(scanIntent, SCANNIN_GREQUEST_CODE);

    }

    private void querByNO() {
        String num = vBankNo.getText().toString().replace(" ","");
        if (TextUtils.isEmpty(num)){
            DialogUtils.showShortToast(this,"卡号没填你查个毛线？！");
            return;
        }
//        try {
//            Express express = new Express(num);
//            Dao.saveAsync(express);
//        }catch (Exception e){
//
//        }

        cardNo = num;
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_BANK_INFO);
        request.add("showapi_appid", Constants.YY_APP_ID);
        request.add("showapi_sign", Constants.YY_APP_SECRET);
        request.add("cardnum", cardNo);
        dialog.show();
        CallServer.getRequestInstance().add(this, request, this, true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
            String resultDisplayStr;
           /* if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
//                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                resultDisplayStr = "Card Number: " + scanResult.getFormattedCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
                vBankNo.setText(scanResult.getFormattedCardNumber());
                querByNO();
            }else {
                resultDisplayStr = "Scan was canceled.";
            }*/
//            L.i("扫码的信息："+resultDisplayStr);
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);



                break;
        }
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_bank;
    }


    @Override
    public void onSucceed(int what, Response<JSON> response) {
        L.i(what+" 返回："+response.get());
        JSONObject result = (JSONObject) response.get();

        setResult(result);
    }

    private void setResult(JSONObject data) {
        String error = data.getString("showapi_res_error");
        if (!TextUtils.isEmpty(error)){
            DialogUtils.showShortToast(this,error);
            return;
        }
        String cardType = data.getString("cardtype");
        String bankName = data.getString("bankname");
        String cardName = data.getString("cardname");

        L.i("bankName:"+bankName+"  cardName:"+cardName+"   cardType:"+cardType);

            dialog.dismiss();

            vBankName.setText(bankName);

//            adapter.notifyDataSetChanged();
//            L.i("logo地址："+company.getJSONObject("icon").getString("normal"));

//            Glide.with(this)
//                    .load(company.getJSONObject("icon").getString("normal"))
//                    .into(vLogo);
//            adapter.notifyItemRangeInserted(0,list.size());

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        dialog.dismiss();
        DialogUtils.showShortToast(this,"网络连接错误！"+message);
//        vResult.setText("请求失败："+message);
    }
}
