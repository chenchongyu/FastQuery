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
//    public static final String API_GET_BANK_INFO = "http://apis.baidu.com/datatiny/cardinfo/cardinfo";
    public static final String API_GET_BANK_INFO = "http://route.showapi.com/896-1";
    public static final String API_GET_TEXT_JOKE = "http://apis.baidu.com/showapi_open_bus/showapi_joke/joke_text";
    public static final String API_GET_PIC_JOKE = "http://apis.baidu.com/showapi_open_bus/showapi_joke/joke_pic";
    public static final String API_GET_TANSLATE_INFO = "http://fanyi.youdao.com/openapi.do?keyfrom=FastQuery2&key=760605702&type=data&doctype=json&version=1.1";
    public static final String API_GET_SOUND_BY_TEXT = "http://apis.baidu.com/apistore/baidutts/tts";
    public static final String API_GET_IMG_INFO = "http://api1.wozhitu.com:8080/imagesearch/api/v1.0/kwdsuggest";
    public static final String API_GET_YAOHAO = "https://sp0.baidu.com/9_Q4sjW91Qh3otqbppnN2DJv/pae/common/api/yaohao";
    public static final String API_GET_CURRENCY = "http://apis.baidu.com/apistore/currencyservice/currency";

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
    public static final int API_GET_TANSLATE_WHAT = 100;
    public static final int API_GET_IMG_WHAT = 101;
    public static final int API_GET_SOUND_WHAT = 102;
    public static final int API_GET_YAOHAO_WHAT = 201;
    public static final int API_GET_CURRENCY_WHAT = 301;
    private static Map<String,Integer> URL_MAP = new HashMap<>(13);

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
        URL_MAP.put(API_GET_TANSLATE_INFO,API_GET_TANSLATE_WHAT);
        URL_MAP.put(API_GET_IMG_INFO,API_GET_IMG_WHAT);
        URL_MAP.put(API_GET_SOUND_BY_TEXT,API_GET_SOUND_WHAT);
        URL_MAP.put(API_GET_YAOHAO,API_GET_YAOHAO_WHAT);
        URL_MAP.put(API_GET_CURRENCY,API_GET_CURRENCY_WHAT);
    }

    public static int getWhat(String url){
        return URL_MAP.get(url);
    }

}
