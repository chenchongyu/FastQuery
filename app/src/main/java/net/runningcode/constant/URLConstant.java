package net.runningcode.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/15.
 */
public class URLConstant {

    public static final String API_GET_COM_BY_EXPRESS_NO = "http://m.kuaidi100.com/autonumber/auto";
    public static final String API_GET_INFO_BY_COM_AND_EXPRESS = "https://sp0.baidu.com/9_Q4sjW91Qh3otqbppnN2DJv/pae/channel/data/asyncqury";
    public static final String API_GET_WEATHER = "http://apis.baidu.com/apistore/weatherservice/cityname";
    public static final String API_GET_IP_INFO = "http://apis.baidu.com/apistore/iplookupservice/iplookup";
    public static final String API_GET_PHONE_INFO = "http://apis.baidu.com/apistore/mobilenumber/mobilenumber";
    public static final String API_GET_LOTTERY_INFO = "http://apis.baidu.com/apistore/lottery/lotteryquery";
    public static final String API_GET_ID_INFO = "http://apis.baidu.com/chazhao/idcard/idcard";
    public static final String API_GET_BANK_INFO = "http://apis.baidu.com/datatiny/cardinfo/cardinfo";
    public static final String API_GET_TEXT_JOKE = "http://apis.baidu.com/showapi_open_bus/showapi_joke/joke_text";
    public static final String API_GET_PIC_JOKE = "http://apis.baidu.com/showapi_open_bus/showapi_joke/joke_pic";

    public static final int API_GET_COM_BY_EXPRESS_NO_WHAT = 1;
    public static final int API_GET_INFO_BY_COM_AND_EXPRESS_WHAT = 20;
    public static final int API_GET_WEATHER_WHAT = 3;
    public static final int API_GET_IP_WHAT = 4;
    public static final int API_GET_PHONE_WHAT = 5;
    public static final int API_GET_LOTTERY_WHAT = 6;
    public static final int API_GET_ID_WHAT = 7;
    public static final int API_GET_BANK_WHAT = 8;
    public static final int API_GET_JOKE_TEXT_WHAT = 90;
    public static final int API_GET_JOKE_PIC_WHAT = 91;
    private static Map<String,Integer> URL_MAP = new HashMap<>(8);

    static {
        URL_MAP.put(API_GET_COM_BY_EXPRESS_NO,API_GET_COM_BY_EXPRESS_NO_WHAT);
        URL_MAP.put(API_GET_INFO_BY_COM_AND_EXPRESS,API_GET_INFO_BY_COM_AND_EXPRESS_WHAT);
        URL_MAP.put(API_GET_WEATHER,API_GET_WEATHER_WHAT);
        URL_MAP.put(API_GET_IP_INFO,API_GET_IP_WHAT);
        URL_MAP.put(API_GET_PHONE_INFO,API_GET_PHONE_WHAT);
        URL_MAP.put(API_GET_LOTTERY_INFO,API_GET_LOTTERY_WHAT);
        URL_MAP.put(API_GET_ID_INFO,API_GET_ID_WHAT);
        URL_MAP.put(API_GET_BANK_INFO,API_GET_BANK_WHAT);
        URL_MAP.put(API_GET_TEXT_JOKE,API_GET_JOKE_TEXT_WHAT);
        URL_MAP.put(API_GET_PIC_JOKE,API_GET_JOKE_PIC_WHAT);
    }

    public static int getWhat(String url){
        return URL_MAP.get(url);
    }

}
