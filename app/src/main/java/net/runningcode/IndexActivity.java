package net.runningcode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import net.runningcode.simple_activity.TranslateActivity;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DateUtil;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;
import net.runningcode.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import static net.runningcode.net.FastJsonRequest.getNewInstance;


/**
 * Created by Administrator on 2016/1/15.
 * // TODO: 2017/1/19
 * 1、身份证查询接口失效，改用阿凡达接口
 * 2、增加常用地市工资计算
 * 3、消息通知，弹出提示
 */
public class IndexActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON>,
        AdapterView.OnItemClickListener,AMapLocationListener, NativeAdListener {
    private final static int ELEMENT_SIZE = 8;
    private static final int REQUEST_CODE_READ_PHONE = 101;
    private TextView vWeather,vTemperature,vCity,vDate,vWD,vAir;
    private ImageView vWeatherIcon,vWeatherBg;
    private RelativeLayout vContent;
    private GridView vTable;
    private SparseArray<String> map;
    private SparseArray<String> adMap;
    private List<Integer> list;

    ItemsAdapter adapter ;
    private String city;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private NativeAdView ad;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
//        L.i("设备ID:"+CommonUtil.getDeviceInfo(this));
        initView();

    }


    private void initView() {
        toolbar.setNavigationIcon(null);
        vContent = $(R.id.v_content);

        vWeather = $(R.id.v_weather_text);
        vWeatherIcon = $(R.id.v_weather_icon);
        vTemperature = $(R.id.v_temperature);
        vWeatherBg = $(R.id.v_weather_bg);
        vWD = $(R.id.v_ws);
        vCity = $(R.id.v_city);
        vDate = $(R.id.v_date);
        vTable = $(R.id.v_table);
        vAir = $(R.id.v_air);

        vCity.setOnClickListener(this);
        vTable.setOnItemClickListener(this);

        setTitle(R.string.app_name);
        initData();
        setToolBarColor(R.drawable.gradient_toolbar);
        setStatusBarColor(R.color.colorPrimaryDark);

        L.i("version===============:" + Build.VERSION.SDK_INT);
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        try {
            initAD();
        }catch (Exception e){
            L.e("初始化广告失败："+e);
        }
//        }

    }


    // TODO: 2016/7/26 汇率转换、成语、周公解梦  改用RxAndroid  butterknife
    // TODO: 2017/3/23 动态权限
    private void initData() {
        initPosition();

        vDate.setText(DateUtil.getCurrentMDE());
        map = new SparseArray<>(ELEMENT_SIZE);
        adMap = new SparseArray<>(1);
        map.put(R.drawable.icon_phone,"手机归属地");
        map.put(R.drawable.icon_id,"身份证查询");
//        map.put(R.drawable.icon_weather,"天气预报");
        map.put(R.drawable.icon_lottery,"彩票查询");
        map.put(R.drawable.icon_express,"快递查询");
        map.put(R.drawable.icon_translate,"翻译");
        map.put(R.drawable.icon_num,"数字转大写");
        map.put(R.drawable.icon_salary,"工资计算");
        map.put(R.drawable.icon_feedback,"吐槽反馈");
//        map.put(R.drawable.icon_bank,"吐槽反馈");

        list = new ArrayList<>(ELEMENT_SIZE);
        list.add(R.drawable.icon_phone);
        list.add(R.drawable.icon_id);
//        list.add(R.drawable.icon_weather);
        list.add(R.drawable.icon_lottery);
        list.add(R.drawable.icon_express);
        list.add(R.drawable.icon_translate);
        list.add(R.drawable.icon_num);
        list.add(R.drawable.icon_salary);
//        list.add(R.drawable.icon_car);
        list.add(R.drawable.icon_feedback);
//        list.add(R.drawable.icon_bank);

        adapter = new ItemsAdapter(list);
        vTable.setAdapter(adapter);
//        getIp();
    }

    private void initPosition() {
        // 先判断是否有权限。
        if(PermissionUtils.hasPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // 有权限，直接do anything.
            locatePos();
        } else {
            // 申请权限。
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_READ_PHONE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
        if (requestCode == REQUEST_CODE_READ_PHONE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            locatePos();
        }else{
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

    private void getWeather() {
        FastJsonRequest request = getNewInstance(URLConstant.API_GET_WEATHER);
        request.add("cityname", city);

        CallServer.getRequestInstance().add(this, request, this, true, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
    public void onSucceed(int what, Response<JSON> response) {
        L.i("返回："+response.get());
        JSONObject result = (JSONObject) response.get();
        switch (what){
            case URLConstant.API_GET_WEATHER_WHAT:
                setWeather(result);
                break;
            case URLConstant.API_GET_IP_WHAT:
                getWeather();
                break;
        }

    }

    private void setWeather(JSONObject result) {
        if (result.getIntValue("error_code") != 0){
            final String reason = result.getString("reason");

            vWeather.setText(TextUtils.isEmpty(reason) ?"获取天气信息失败":reason);
            return;
        }
        JSONObject data = result.getJSONObject("result");
        JSONObject realtime = data.getJSONObject("realtime");
        JSONObject pm25 = data.getJSONObject("pm25").getJSONObject("pm25");
        JSONObject weather = realtime.getJSONObject("weather");
        JSONObject wd = realtime.getJSONObject("wind");
        final String du = getString(R.string.du);
        final String text = weather.getString("temperature") + du + "c";
        vTemperature.setText(text);
        String wdStr = wd.getString("direct")+" "+wd.getString("power");
        vWD.setText(wdStr);
        final String weatherString = weather.getString("info");
        vWeather.setText(weatherString);
        int drawble = CommonUtil.getDrawbleByWeather(weatherString);
        vWeatherIcon.setImageResource(drawble);
        vWeatherBg.setImageResource(CommonUtil.getBgDrawbleByWeather(weatherString));

        vAir.setText("空气"+pm25.getString("quality")+"("+pm25.getString("pm25")+")");
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        DialogUtils.showLongToast(this,"获取天气信息失败！");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        int icon = list.get(position);
        switch (icon){
            case R.drawable.icon_express:
                startActivity(new Intent(this, ExpressActivity.class),view);
                break;
            case R.drawable.icon_phone:
                startActivity(new Intent(this, PhoneActivity.class),view);
                break;
            case R.drawable.icon_id:
                startActivity(new Intent(this, IDActivity.class),view);
                break;
            case R.drawable.icon_lottery:
                startActivity(new Intent(this, LotteryActivity.class),view);
                break;
            case R.drawable.icon_bank:
//                startActivity(new Intent(this, BankActivity.class),view);
                sendEmail();
                break;
            case R.drawable.icon_translate:
                startActivity(new Intent(this, TranslateActivity.class),view);
                break;
            case R.drawable.icon_num:
                startActivity(new Intent(this, NumberActivity.class),view);
                break;
            case R.drawable.icon_salary:
                startActivity(new Intent(this, SalaryActivity.class),view);
                break;
            case R.drawable.icon_feedback:
                sendEmail();
//                startActivity(new Intent(this, CarActivity.class),view);
                break;

            case -1:
                MiStatInterface.recordCountEvent("click ad","native ad click");
                DialogUtils.showShortToast(this,"推广链接，谢谢点击！");
                break;
            default:
                DialogUtils.showShortToast(this,"敬请期待");
                break;

        }
    }

    private void sendEmail() {
        String[] reciver = new String[] { "wochenchongyu@126.com" };
        String[] mySbuject = new String[] { "我要吐槽" };
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
                L.i("定位成功："+amapLocation.getCityCode());//城市编码);
                String fullname = amapLocation.getCity();
                city = amapLocation.getCity().substring(0,fullname.length()-1);
                vCity.setText(city);//城市信息
                getWeather();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                L.e("location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                final String errorReason = "定位失败-" + amapLocation.getErrorInfo()+"";
                vCity.setText(errorReason);
            }
        }
    }

    private void initAD() {
        ad = new NativeAdView(this,Constants.ANWO_PUBLISHER_ID,false,this);
        ad.prepareAd();


        AdwoAdView adView=new AdwoAdView(this, Constants.ANWO_PUBLISHER_ID,false,10);
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
                MiStatInterface.recordCountEvent("click ad","banner ad click");
            }
        });
    }

    @Override
    public void onReceiveAd(String s) {
        L.i("get ad:"+s);
        JSONObject adJson = JSON.parseObject(s);
        map.put(-1,adJson.getString("title"));
        adMap.put(-1,adJson.getString("icon"));
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

    class ItemsAdapter extends BaseAdapter{
        private List<Integer> list;

        public ItemsAdapter(List<Integer> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list.get(i);
        }

        /*@Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position);
        }*/

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final int icon = list.get(position);
            String title = map.get(icon);
            ViewHolder holder = null;
            if (view == null){
                holder = new ViewHolder();
                view = View.inflate(IndexActivity.this, R.layout.item_item_ad, null);
//
                holder.vRoot = (FrameLayout) view.findViewById(R.id.v_root);
                holder.vIcon = (ImageView) view.findViewById(R.id.v_icon);
                holder.vText = (TextView) view.findViewById(R.id.v_item);
                view.setTag(holder);

            }else {
                holder = (ViewHolder) view.getTag();
            }
            if (icon < 0){
                L.i("加载广告图片："+adMap.get(icon)+":"+title);
                RunningCodeApplication.loadImg(holder.vIcon,adMap.get(icon));
                final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        holder.vIcon.getMeasuredWidth(),
                        holder.vIcon.getMeasuredHeight());
                ad.setLayoutParams(layoutParams);
                holder.vRoot.addView(ad);
            }else{
//                Glide.clear(holder.vIcon);
                holder.vIcon.setImageResource(icon);
            }

            holder.vText.setText(title);

            return view;
        }



        class ViewHolder{
            ViewGroup vRoot;
            ImageView vIcon;
            TextView vText;
        }
    }


}
