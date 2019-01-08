package net.runningcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.sixth.adwoad.AdwoAdView;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.NativeAdListener;
import com.sixth.adwoad.NativeAdView;
import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.yolanda.nohttp.Response;

import net.runningcode.constant.Constants;
import net.runningcode.constant.URLConstant;
import net.runningcode.express.ExpressActivity;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.simple_activity.IDActivity;
import net.runningcode.simple_activity.LotteryActivity;
import net.runningcode.simple_activity.NumberActivity;
import net.runningcode.simple_activity.PhoneActivity;
import net.runningcode.simple_activity.SalaryActivity;
import net.runningcode.simple_activity.ShortUrlActivity;
import net.runningcode.simple_activity.TranslateActivity;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DateUtil;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;
import net.runningcode.utils.PermissionUtils;
import net.runningcode.utils.RecyclerViewOnItemClickListener;
import net.runningcode.utils.ThreadPool;
import net.runningcode.view.RecycleViewItemDecoration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static net.runningcode.net.FastJsonRequest.getNewInstance;


/**
 * Created by Administrator on 2016/1/15.
 * // TODO: 2017/1/19
 * 2、增加常用地市工资计算
 * 3、消息通知，弹出提示
 */
public class IndexActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON>,
        AMapLocationListener, NativeAdListener, RecyclerViewOnItemClickListener {
    private final static int ELEMENT_SIZE = 8;
    private static final int REQUEST_CODE_READ_PHONE = 101;
    private TextView vWeather, vTemperature, vCity, vDate, vWD, vAir, vNotice;
    private ImageView vWeatherIcon, vWeatherBg;
    private RelativeLayout vContent;
    private RecyclerView vTable;
    private SparseArray<String> map;
    private SparseArray<String> adMap;
    private List<Integer> list;

    ItemsAdapter adapter;
    private String city;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private NativeAdView ad;
    private MyHandler mHandler;

    private static class MyHandler extends Handler {
        private final WeakReference<IndexActivity> mActivityReference;

        public MyHandler(IndexActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            IndexActivity mActivity = mActivityReference.get();

            if (mActivity != null) {
                switch (msg.what) {
                    case 1:
                        String cityCode = (String) msg.obj;
                        mActivity.getWeather(cityCode);
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        mHandler = new MyHandler(this);
    }


    private void initView() {
        toolbar.setNavigationIcon(null);
        vContent = $(R.id.v_content);


        vNotice = $(R.id.v_notice);
        vWeather = $(R.id.v_weather_text);
        vWeatherIcon = $(R.id.v_weather_icon);
        vTemperature = $(R.id.v_temperature);
        vWeatherBg = $(R.id.v_weather_bg);
        vWD = $(R.id.v_ws);
        vCity = $(R.id.v_city);
        vDate = $(R.id.v_date);
        vAir = $(R.id.v_air);

        vCity.setOnClickListener(this);

        vTable = $(R.id.v_table);
        vTable.setLayoutManager(new GridLayoutManager(this, 4));
        vTable.setItemAnimator(new DefaultItemAnimator());
        vTable.addItemDecoration(new RecycleViewItemDecoration(5));

        setTitle(R.string.app_name);

        initData();
        setToolBarColor(R.drawable.gradient_toolbar);

        try {
            initAD();
        } catch (Exception e) {
            L.e("初始化广告失败：" + e);
        }
//        }

        PermissionUtils.checkAndRequestPermission(this, new PermissionUtils.OnPermissionGrantCallback() {
            @Override
            public void onGranted(String[] permissions) {

            }

            @Override
            public void onDenied(String[] strings) {

            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }


    // TODO: 2016/7/26 汇率转换、成语、周公解梦  改用RxAndroid  butterknife
    // TODO: 2017/3/23 动态权限
    private void initData() {
        initPosition();

        vDate.setText(DateUtil.getCurrentMDE());
        map = new SparseArray<>(ELEMENT_SIZE);
        adMap = new SparseArray<>(1);
        map.put(R.drawable.icon_phone, "手机归属地");
        map.put(R.drawable.icon_id, "身份证查询");
//        map.put(R.drawable.icon_weather,"天气预报");
        map.put(R.drawable.icon_lottery, "彩票查询");
        map.put(R.drawable.icon_express, "快递查询");
        map.put(R.drawable.icon_translate, "翻译");
        map.put(R.drawable.icon_num, "数字转大写");
        map.put(R.drawable.icon_salary, "薪资计算");
//        map.put(R.drawable.icon_url, "短链接");
        map.put(R.drawable.icon_feedback, "吐槽反馈");

        list = new ArrayList<>(ELEMENT_SIZE);
        list.add(R.drawable.icon_phone);
        list.add(R.drawable.icon_lottery);
        list.add(R.drawable.icon_express);
        list.add(R.drawable.icon_id);
//        list.add(R.drawable.icon_weather);
        list.add(R.drawable.icon_translate);
        list.add(R.drawable.icon_num);
        list.add(R.drawable.icon_salary);
//        list.add(R.drawable.icon_url);
        list.add(R.drawable.icon_feedback);
//        list.add(R.drawable.icon_bank);

        adapter = new ItemsAdapter(list);
        vTable.setAdapter(adapter);
//        getIp();
    }

    private void initPosition() {
        // 先判断是否有权限。
        if (PermissionUtils.hasPermission(this,
                ACCESS_COARSE_LOCATION, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE)) {
            // 有权限，直接do anything.
            locatePos();
        } else {
            // 申请权限。
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_COARSE_LOCATION, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_PHONE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
        if (requestCode == REQUEST_CODE_READ_PHONE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locatePos();
        } else {
            L.i("onRequestPermissionsResult FAILED!");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void locatePos() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void getCityCode() {
        ThreadPool.submit(new Runnable() {
            @Override
            public void run() {

                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                try {
                    msg.obj = readCityData();
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private String readCityData() throws IOException {
        BufferedReader reader = null;
        StringBuilder fileContent = new StringBuilder();
        String cityCode = "";
        try {
            InputStream in = getAssets().open("city.json");
            InputStreamReader is = new InputStreamReader(in, "UTF-8");
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
            L.i("fileContent:" + fileContent.toString());

            JSONArray jsonArray = JSONObject.parseArray(fileContent.toString());
            JSONObject json;
            for (int i = 0, j = jsonArray.size(); i < j; i++) {
                json = (JSONObject) jsonArray.get(i);
                if (TextUtils.equals(city, json.getString("city_name"))) {
                    cityCode = json.getString("city_code");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return cityCode;
    }

    private void getWeather(String code) {
        FastJsonRequest request = getNewInstance(URLConstant.API_GET_WEATHER + code);

        CallServer.getRequestInstance().add(this, request, this, true, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_city:
                initPosition();
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        }
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_index;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.colorPrimaryDark;
    }


    @Override
    public void onSucceed(int what, Response<JSON> response) {
        L.i("返回：" + response.get());
        JSONObject result = (JSONObject) response.get();
        if (result == null) {
            DialogUtils.showLongToast(this, "获取天气信息失败！");
            return;
        }
        setWeather(result);

    }

    private void setWeather(JSONObject result) {
        if (result.getIntValue("status") != 200) {
            final String reason = result.getString("message");

            vWeather.setText(TextUtils.isEmpty(reason) ? "获取天气信息失败" : reason);
            return;
        }
        JSONObject data = result.getJSONObject("data");
        if (data == null) {
            final String reason = result.getString("message");

            vWeather.setText(TextUtils.isEmpty(reason) ? "获取天气信息失败" : reason);
            return;
        }
        int pm25 = data.getIntValue("pm25");
        JSONArray forecast = data.getJSONArray("forecast");
        if (forecast != null && !forecast.isEmpty()) {
            //取出第一条数据（今天）
            JSONObject today = (JSONObject) forecast.get(0);
            String wdStr = today.getString("fx") + " " + today.getString("fl");
            vWD.setText(wdStr);
            final String du = getString(R.string.du);

            final String text = data.getString("wendu") + du + "c";
            final String weatherString = today.getString("type");
            vTemperature.setText(text);
            vWeather.setText(weatherString);
            int drawble = CommonUtil.getDrawbleByWeather(weatherString);
            vWeatherIcon.setImageResource(drawble);
            vWeatherBg.setImageResource(CommonUtil.getBgDrawbleByWeather(weatherString));
            vNotice.setText(today.getString("notice"));
        }

        String text = "空气" + data.getString("quality") + "(" + pm25 + ")";
        vAir.setText(text);
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        DialogUtils.showLongToast(this, "获取天气信息失败！");
    }

    private void sendEmail() {
        String[] reciver = new String[]{"wochenchongyu@126.com"};
        String[] mySbuject = new String[]{"我要吐槽"};
        String myCc = "cc";
        String mybody = "我要吐槽：";
        Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
        myIntent.setType("plain/text");
        myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
        myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
        myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);
        startActivity(Intent.createChooser(myIntent, "我要吐槽"));
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                L.i("定位成功：" + amapLocation.getCityCode());//城市编码);
                String fullname = amapLocation.getCity();
                city = amapLocation.getCity().substring(0, fullname.length() - 1);
                vCity.setText(city);//城市信息
                getCityCode();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                L.e("location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                final String errorReason = "定位失败-" + amapLocation.getErrorInfo() + "";
                vCity.setText(errorReason);
            }
        }
    }

    private void initAD() {
        ad = new NativeAdView(this, Constants.ANWO_PUBLISHER_ID, false, this);
        ad.prepareAd();


        AdwoAdView adView = new AdwoAdView(this, Constants.ANWO_PUBLISHER_ID, false, 10);
//        adView.setListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adView.setLayoutParams(params);
        adView.setBannerMatchScreenWidth(true);

        vContent.addView(adView);
        adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiStatInterface.recordCountEvent("click ad", "banner ad click");
            }
        });
    }

    @Override
    public void onReceiveAd(String s) {
        L.i("get ad:" + s);
        JSONObject adJson = JSON.parseObject(s);
        map.put(-1, adJson.getString("title"));
        adMap.put(-1, adJson.getString("icon"));
        list.add(-1);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailedToReceiveAd(View view, ErrorCode errorCode) {

    }

    @Override
    public void onPresentScreen() {

    }

    @Override
    public void onDismissScreen() {

    }

    @Override
    public void onItemClick(View view, int position) {
        int icon = list.get(position);
        switch (icon) {
            case R.drawable.icon_express:
                startActivity(new Intent(this, ExpressActivity.class), view);
                break;
            case R.drawable.icon_phone:
                startActivity(new Intent(this, PhoneActivity.class), view);
                break;
            case R.drawable.icon_id:
                startActivity(new Intent(this, IDActivity.class), view);
                break;
            case R.drawable.icon_lottery:
                startActivity(new Intent(this, LotteryActivity.class), view);
                break;
            case R.drawable.icon_bank:
//                startActivity(new Intent(this, BankActivity.class),view);
                sendEmail();
                break;
            case R.drawable.icon_translate:
                startActivity(new Intent(this, TranslateActivity.class), view);
                break;
            case R.drawable.icon_num:
                startActivity(new Intent(this, NumberActivity.class), view);
                break;
            case R.drawable.icon_salary:
                startActivity(new Intent(this, SalaryActivity.class), view);
                break;
            case R.drawable.icon_url:
                startActivity(new Intent(this, ShortUrlActivity.class), view);
                break;
            case R.drawable.icon_feedback:
                sendEmail();
//                startActivity(new Intent(this, CarActivity.class),view);
                break;

            case -1:
                MiStatInterface.recordCountEvent("click ad", "native ad click");
                DialogUtils.showShortToast(this, "推广链接，谢谢点击！");
                break;
            default:
                DialogUtils.showShortToast(this, "敬请期待");
                break;

        }
    }

    class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.AdViewHolder> {
        private List<Integer> list;

        public ItemsAdapter(List<Integer> list) {
            this.list = list;
        }

        @Override
        public AdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemsAdapter.AdViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(final AdViewHolder holder, final int position) {
            final int icon = list.get(position);
            String title = map.get(icon);
            if (icon < 0) {
                L.i("加载广告图片：" + adMap.get(icon) + ":" + title);
                RunningCodeApplication.loadImg(holder.vIcon, adMap.get(icon));
                final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        holder.vIcon.getMeasuredWidth(),
                        holder.vIcon.getMeasuredHeight());
                ad.setLayoutParams(layoutParams);
                holder.vRoot.addView(ad);
            } else {
//                Glide.clear(holder.vIcon);
                holder.vIcon.setImageResource(icon);
            }

            holder.vText.setText(title);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IndexActivity.this.onItemClick(holder.itemView, position);
                }
            });
        }

        @Override
        public long getItemId(int i) {
            return list.get(i);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class AdViewHolder extends RecyclerView.ViewHolder {
            ViewGroup vRoot;
            ImageView vIcon;
            TextView vText;

            public AdViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_ad, parent, false));
                vRoot = (FrameLayout) itemView.findViewById(R.id.v_root);
                vIcon = (ImageView) itemView.findViewById(R.id.v_icon);
                vText = (TextView) itemView.findViewById(R.id.v_item);
            }
        }
    }


}
