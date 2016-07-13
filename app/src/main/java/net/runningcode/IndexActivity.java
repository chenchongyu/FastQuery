package net.runningcode;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import net.runningcode.bank.BankActivity;
import net.runningcode.constant.Constants;
import net.runningcode.constant.URLConstant;
import net.runningcode.express.ExpressActivity;
import net.runningcode.simple_activity.CarActivity;
import net.runningcode.simple_activity.IDActivity;
import net.runningcode.simple_activity.LotteryActivity;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.simple_activity.NumberActivity;
import net.runningcode.simple_activity.PhoneActivity;
import net.runningcode.simple_activity.TranslateActivity;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.DateUtil;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/15.
 */
public class IndexActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON>,
        AdapterView.OnItemClickListener,AMapLocationListener, NativeAdListener {
    private final static int ELEMENT_SIZE = 8;
    private TextView vWeather,vTemperature,vCity,vDate,vWD;
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

        vCity.setOnClickListener(this);
        vTable.setOnItemClickListener(this);

        setTitle(R.string.app_name);
        initData();
        setToolBarColor(R.drawable.gradient_toolbar);
//        setToolBarColor(R.color.colorPrimary);
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
        map.put(R.drawable.icon_car,"汽车摇号");
//        map.put(R.drawable.icon_bank,"银行卡查询");

        list = new ArrayList<>(ELEMENT_SIZE);
        list.add(R.drawable.icon_phone);
        list.add(R.drawable.icon_id);
//        list.add(R.drawable.icon_weather);
        list.add(R.drawable.icon_lottery);
        list.add(R.drawable.icon_express);
        list.add(R.drawable.icon_translate);
        list.add(R.drawable.icon_num);
        list.add(R.drawable.icon_car);
//        list.add(R.drawable.icon_bank);

        adapter = new ItemsAdapter(list);
        vTable.setAdapter(adapter);
//        getIp();
    }

    private void initPosition() {
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

    private void getIp() {
        L.i("IP:"+CommonUtil.getLocalIpAddress());
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_IP_INFO);
        request.add("ip", CommonUtil.getLocalIpAddress());
        CallServer.getRequestInstance().add(this, request, this, true, true);
    }

    private void getWeather() {
        FastJsonRequest request = new FastJsonRequest(URLConstant.API_GET_WEATHER);
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
        if (result.getIntValue("errNum") != 0){
            vWeather.setText("获取天气信息失败");
            return;
        }
        JSONObject weather = result.getJSONObject("retData");
        final String du = getString(R.string.du);
        final String text = weather.getString("l_tmp") + du + "c" + "~" + weather.getString("h_tmp") + du + "c";
        vTemperature.setText(text);
        String wd = weather.getString("WD");
        vWD.setText(wd);
        final String weatherString = weather.getString("weather");
        vWeather.setText(weatherString);
        int drawble = CommonUtil.getDrawbleByWeather(weatherString);
        vWeatherIcon.setImageResource(drawble);
        vWeatherBg.setImageResource(CommonUtil.getBgDrawbleByWeather(weatherString));
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
                startActivity(new Intent(this, BankActivity.class),view);
                break;
            case R.drawable.icon_translate:
                startActivity(new Intent(this, TranslateActivity.class),view);
                break;
            case R.drawable.icon_num:
                startActivity(new Intent(this, NumberActivity.class),view);
                break;
            case R.drawable.icon_car:
                startActivity(new Intent(this, CarActivity.class),view);
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
