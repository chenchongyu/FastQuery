package net.runningcode.simple_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;
import com.yolanda.nohttp.Response;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.constant.Constants;
import net.runningcode.constant.URLConstant;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.net.WaitDialog;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;
import net.runningcode.utils.StreamUtil;
import net.runningcode.utils.StringUtils;
import net.runningcode.utils.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by Administrator on 2016/1/15.
 */
public class TranslateActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON> {
    private static final int SCANNIN_GREQUEST_CODE = 1;
    private static final int MSG_PLAY_MP3 = 1;
    private EditText vText;
    private ImageView vScan,vQuery,vClear,vPlay;
    private TextView vTransResult,vMainResult;
    private View vResult;
    private WaitDialog dialog;
    private Uri picPath;
    private String soundPath;
    private MyHandler mMyHandler;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }
    private void initView() {

//        rlt = Native.openOcrEngine(RunningCodeApplication.getInstance().orcPath); // step 1: open OCR engine
//        rlt = Native.setOcrLanguage(Native.TIANRUI_LANGUAGE_ENGLISH); // step 2: set recognition language
//        if (rlt != 1){
//            DialogUtils.showShortToast(this,"OCR图形引擎启动失败！");
//        }

        mMyHandler = new MyHandler(this);
        vClear = $(R.id.v_clear);

        vScan = $(R.id.v_scan);
        vText = $(R.id.v_no);
        vQuery = $(R.id.v_query);
        vResult = $(R.id.v_result);
        vPlay = $(R.id.v_play);
        vTransResult = $(R.id.v_translate_result);
        vMainResult = $(R.id.v_main_result);

        vText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    querByNO();
                    return true;
                }
                return false;
            }
        });

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

        vQuery.setOnClickListener(this);
        vScan.setOnClickListener(this);
        vClear.setOnClickListener(this);
        vPlay.setOnClickListener(this);
        vMainResult.setOnClickListener(this);

        dialog = new WaitDialog(this);

        initToolbar(R.color.translate_purple,R.drawable.icon_translate);
        setTitle("翻译");
    }


    protected void setupWindowAnimations() {
//        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.color.translate_purple);
        setupExitAnimations();
    }

    @Override
    public void onClick(View view) {
        L.i("v getId:"+view.getId()+"  view:"+R.id.view+"  vText:"+ vText.getId());
        switch (view.getId()){
            case R.id.v_query:
                querByNO();
                break;
            case R.id.v_scan:
                getPicture();
                break;
            case R.id.v_clear:
                vText.setText("");
                break;
            case R.id.v_main_result:
            case R.id.v_play:
                getSound();
                break;
        }

    }

    private void getSound() {
        soundPath = CommonUtil.genMP3Path(vText.getText().toString());
        if (new File(soundPath).exists()){
            playSound();
        }else {
            String text = vMainResult.getText().toString();
            FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_SOUND_BY_TEXT);
//        try {
//            request.add("text", URLEncoder.encode(text,"UTF-8"));
            request.add("text", text);
            request.add("ctp", 1);
            request.add("per", 0);

//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

            CallServer.getRequestInstance().add(this,request,this,true,false);
        }


    }


    private void getPicture() {

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        picPath = Uri.fromFile(new File(CommonUtil.genPicPath()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picPath);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent,SCANNIN_GREQUEST_CODE);
    }

    private void querByNO() {
        String q = vText.getText().toString();
        if (TextUtils.isEmpty(q)){
            DialogUtils.showShortToast(this,"你想查个毛线？！");
            return;
        }
//        try {
//            Express express = new Express(num);
//            Dao.saveAsync(express);
//        }catch (Exception e){
//
//        }

        final int salt = new Random().nextInt(10000);
        StringBuilder md5str = new StringBuilder();
        md5str.append(Constants.BAIDU_TRANS_APP_ID).append(q).append(salt).append(Constants.BAIDU_TRANS_APP_TOKEN);
        final String md5 = StringUtils.md5(md5str.toString()).toLowerCase();
        L.i("md5:"+md5);

        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_TANSLATE_INFO);
        request.add("q", q);
/*        request.add("from", "auto");
        request.add("to", "zh");
        request.add("appid", Constants.BAIDU_TRANS_APP_ID);
        request.add("salt", salt);
        request.add("sign", md5);*/
        dialog.show();
        CallServer.getRequestInstance().add(this, request, this, true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SCANNIN_GREQUEST_CODE){
//            String path =  data.getStringExtra(MediaStore.EXTRA_OUTPUT);
//            Uri uri = data.getData();
//            L.i(path+":"+uri.toString());
            getTextFromPic();
        }

    }

    private void getTextFromPic() {
        ThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                File file = new File(picPath.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                int picw = bitmap.getWidth();
                int pich = bitmap.getHeight();
                int[] pix = new int[picw * pich];
                L.i("图片宽高："+picw+":"+pich);
               /* int result = Native.recognizeImage(pix, picw, pich);
                if (result == 1){
                    String[] mwholeWord = Native.getWholeWordResult();
                    for (String s:mwholeWord){
                        L.i(s);
                    }
                    String[] mwholeTextLine = Native.getWholeTextLineResult();
                    for (String s:mwholeTextLine){
                        L.i(s);
                    }
                    int[] mwholeWordRect = Native.getWholeWordRect();
                    int[] mwholdTextLineRect = Native.getWholeTextLineRect();
                }else {
                    L.i("图片识别失败！");
                }*/
            }
        });



    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_translate;
    }


    @Override
    public void onSucceed(int what, Response<JSON> response) {
        JSONObject result = (JSONObject) response.get();
        L.i(what+" 返回："+result);
        if (what == URLConstant.API_GET_SOUND_WHAT){
            genMp3AndPlay(result);
        }else {
            dialog.dismiss();

            setResult(result);
        }

    }

    private void genMp3AndPlay(final JSONObject result) {
        int error = result.getIntValue("errNum");
        if (error!=0){
            DialogUtils.showShortToast(this,result.getString("retMsg"));
            return;
        }
        final String base64 = result.getString("retData");
        ThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = Base64.decodeFast(base64);
                StreamUtil.byte2File(bytes,soundPath);
                playSound();
            }
        });

    }

    private void playSound() {
        try {
            if (mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(soundPath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();//释放音频资源
                    mediaPlayer = null;
                    L.i("播放完毕");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setResult(JSONObject data) {
        int error = data.getIntValue("errorCode");
        if (error != 0){
            DialogUtils.showShortToast(this,error);
            return;
        }
        vResult.setVisibility(View.VISIBLE);
        CommonUtil.hideInputMethod(this,vResult);
        JSONArray result = data.getJSONArray("translation");
        vMainResult.setText( getTranslation(result));

        final StringBuilder text = new StringBuilder();
        final JSONObject basic = data.getJSONObject("basic");
        if (basic != null){
            JSONArray basicResult = basic.getJSONArray("explains");
            text.append(getTranslation(basicResult));
        }

        JSONArray webResult = data.getJSONArray("web");
        if (webResult != null){
            StringBuilder web = new StringBuilder();
            for(int i=0;i<webResult.size();i++){
                JSONObject jsonObject = webResult.getJSONObject(i);
                JSONArray v = jsonObject.getJSONArray("value");
                String k = jsonObject.getString("key");
                if (TextUtils.equals(k.toLowerCase(),vText.getText().toString().toLowerCase())){
                    web.append(getTranslationOneLine(v,"，")).append("\n");
                }
            }

            text.append("\n【网络释义】\n").append(web);
        }

        vTransResult.setText(text.toString());

    }

    private String getTranslation(JSONArray result) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<result.size();i++){
            stringBuilder.append(result.getString(i)).append("\n");
        }

        return stringBuilder.toString();
    }
    private String getTranslationOneLine(JSONArray result,String ext) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<result.size();i++){
            stringBuilder.append(result.getString(i));
            if (i!=result.size()-1)
                stringBuilder.append(ext);
        }

        return stringBuilder.toString();
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        dialog.dismiss();
        DialogUtils.showShortToast(this,"网络连接错误！"+message);
//        vResult.setText("请求失败："+message);
    }

    private static class MyHandler extends Handler {
        private WeakReference<TranslateActivity> activityRefefrence;
        public MyHandler(TranslateActivity activity){
            activityRefefrence = new WeakReference<TranslateActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            TranslateActivity activity = activityRefefrence.get();
            if(activity != null){

            }
        }

    }
}
