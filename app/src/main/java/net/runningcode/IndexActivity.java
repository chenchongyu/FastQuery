package net.runningcode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yolanda.nohttp.Response;

import net.runningcode.constant.URLConstant;
import net.runningcode.express.ExpressActivity;
import net.runningcode.net.CallServer;
import net.runningcode.net.FastJsonRequest;
import net.runningcode.net.HttpListener;
import net.runningcode.phone.PhoneActivity;
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
        AdapterView.OnItemClickListener,AMapLocationListener {
    private TextView vWeather,vTemperature,vCity,vDate;
    private ImageView vWeatherIcon,vWeatherBg;
    private GridView vTable;
    private SparseArray<String> map;
    private List<Integer> list;
    ItemsAdapter adapter ;
    private String city;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    private void initView() {
        toolbar.setNavigationIcon(null);

        vWeather = $(R.id.v_weather_text);
        vWeatherIcon = $(R.id.v_weather_icon);
        vTemperature = $(R.id.v_temperature);
        vWeatherBg = $(R.id.v_weather_bg);
        vCity = $(R.id.v_city);
        vDate = $(R.id.v_date);
        vTable = $(R.id.v_table);
        vTable.setOnItemClickListener(this);

        setTitle(R.string.app_name);
        initData();
//        setToolBarColor(R.color.colorPrimary);
//        setStatusBarColor(R.color.colorPrimaryDark);
    }

    private void initData() {
        initPosition();

        vDate.setText(DateUtil.getCurrentMDE());
        map = new SparseArray<>();
        map.put(R.drawable.icon_phone,"手机归属地");
        map.put(R.drawable.icon_ip,"IP查询");
        map.put(R.drawable.icon_weather,"天气预报");
        map.put(R.drawable.icon_express,"快递查询");

        list = new ArrayList<>(4);
        list.add(R.drawable.icon_phone);
        list.add(R.drawable.icon_ip);
        list.add(R.drawable.icon_weather);
        list.add(R.drawable.icon_express);

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
                if (result.getIntValue("errNum") != 0){
                    vWeather.setText("获取天气信息失败");
                    return;
                }
                JSONObject weather = result.getJSONObject("retData");
                final String du = getString(R.string.du);
                final String text = weather.getString("l_tmp") + du + "c" + "~" + weather.getString("h_tmp") + du + "c";
                vTemperature.setText(text);
                final String weatherString = weather.getString("weather");
                vWeather.setText(weatherString);
                int drawble = CommonUtil.getDrawbleByWeather(weatherString);
                vWeatherIcon.setImageResource(drawble);
                break;
            case URLConstant.API_GET_IP_WHAT:
                getWeather();
                break;
        }

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
            case R.drawable.icon_ip:
//                startActivity(new Intent(this, ExpressActivity.class));
//                break;
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
                /*amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                amapLocation.getAoiName();//获取当前定位点的AOI信息*/
                L.i("定位成功："+amapLocation.getCityCode());//城市编码);
                String fullname = amapLocation.getCity();
                city = amapLocation.getCity().substring(0,fullname.length()-1);
                vCity.setText(city);//城市信息
                getWeather();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                vCity.setText("定位失败");
            }
        }
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

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            int icon = list.get(position);
            String title = map.get(icon);
            ViewHolder holder = null;
            if (view == null){
                holder = new ViewHolder();
                view = View.inflate(IndexActivity.this, R.layout.item_item, null);
                holder.vIcon = (ImageView) view.findViewById(R.id.v_icon);
                holder.vText = (TextView) view.findViewById(R.id.v_item);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            holder.vIcon.setImageResource(icon);
            holder.vText.setText(title);

            return view;
        }

        class ViewHolder{
            ImageView vIcon;
            TextView vText;
        }
    }
}
