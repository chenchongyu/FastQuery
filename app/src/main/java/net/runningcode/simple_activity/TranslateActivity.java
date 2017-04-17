package net.runningcode.simple_activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
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
import net.runningcode.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Random;

import static net.runningcode.net.FastJsonRequest.getNewInstance;

/**
 * Created by Administrator on 2016/1/15.
 */
public class TranslateActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON> , SpeechSynthesizerListener {
    private static final int SCANNIN_GREQUEST_CODE = 1;
    private static final int MSG_PLAY_MP3 = 1;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";

    private EditText vText;
    private ImageView vScan,vQuery,vClear,vPlay;
    private TextView vTransResult,vMainResult;
    private View vResult;
    private WaitDialog dialog;
    private MyHandler mMyHandler;

    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;

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

        setEditBottomColor(vText,R.color.translate_purple);
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

        initialEnv();
        initialTts();
    }

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
//        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialTts() {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(this);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);

        this.mSpeechSynthesizer.setAppId(Constants.BAIDU_TTS_APP_ID);
        this.mSpeechSynthesizer.setApiKey(Constants.BAIDU_TTS_API_KEY,Constants.BAIDU_TTS_SECRET_KEY);
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "3");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        //设 置 播 放 器 的 音 频 流 类 型 ， 默 认 值 为 AudioManager.STREAM_MUSIC,AudioManager.STREAM_MUSIC 指的是用与音乐播放的音频流。
        this.mSpeechSynthesizer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能）
        mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);

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
            case R.id.v_clear:
                vText.setText("");
                break;
            case R.id.v_main_result:
            case R.id.v_play:
                play();
                break;
        }

    }

    private void play() {
        String text = vMainResult.getText().toString();
        L.i("开始播放："+text);
        int result = this.mSpeechSynthesizer.speak(text);
        if (result < 0) {
            toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }


    private void querByNO() {
        String q = vText.getText().toString();
        if (TextUtils.isEmpty(q)){
            DialogUtils.showShortToast(this,"你想查个毛线？！");
            return;
        }


        final int salt = new Random().nextInt(10000);
        StringBuilder md5str = new StringBuilder();
        md5str.append(Constants.BAIDU_TRANS_APP_ID).append(q).append(salt).append(Constants.BAIDU_TRANS_APP_TOKEN);
        final String md5 = StringUtils.md5(md5str.toString()).toLowerCase();
        L.i("md5:"+md5);

        FastJsonRequest request = getNewInstance(URLConstant.API_GET_TANSLATE_INFO);
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
    public int getContentViewID() {
        return R.layout.activity_translate;
    }


    @Override
    public void onSucceed(int what, Response<JSON> response) {
        JSONObject result = (JSONObject) response.get();
        L.i(what+" 返回："+result);

        dialog.dismiss();

        setResult(result);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mSpeechSynthesizer.release();
    }

    /*
     * @param arg0
     */
    @Override
    public void onSynthesizeStart(String utteranceId) {
        toPrint("onSynthesizeStart utteranceId=" + utteranceId);
    }

    /**
     * 合成数据和进度的回调接口，分多次回调
     *
     * @param utteranceId
     * @param data 合成的音频数据。该音频数据是采样率为16K，2字节精度，单声道的pcm数据。
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
         L.i("onSynthesizeDataArrived");
//        mMyHandler.sendMessage(mMyHandler.obtainMessage(UI_CHANGE_SYNTHES_TEXT_SELECTION, progress, 0));
    }

    /**
     * 合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSynthesizeFinish(String utteranceId) {
        toPrint("onSynthesizeFinish utteranceId=" + utteranceId);
    }

    /**
     * 播放开始，每句播放开始都会回调
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechStart(String utteranceId) {
        toPrint("onSpeechStart utteranceId=" + utteranceId);
    }

    /**
     * 播放进度回调接口，分多次回调
     *
     * @param utteranceId
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
         L.i("onSpeechProgressChanged");
//        mHandler.sendMessage(mHandler.obtainMessage(UI_CHANGE_INPUT_TEXT_SELECTION, progress, 0));
    }

    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
        toPrint("onSpeechFinish utteranceId=" + utteranceId);
    }

    /**
     * 当合成或者播放过程中出错时回调此接口
     *
     * @param utteranceId
     * @param error 包含错误码和错误信息
     */
    @Override
    public void onError(String utteranceId, SpeechError error) {
        toPrint("onError error=" + "(" + error.code + ")" + error.description + "--utteranceId=" + utteranceId);
    }


    private void toPrint(String str) {
        L.i(str);
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
                activity.print(msg);
            }
        }

    }

    private void print(Message msg) {
        String message = (String) msg.obj;
        if (message != null) {
            L.i(message);
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
