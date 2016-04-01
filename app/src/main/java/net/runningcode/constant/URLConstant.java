package net.runningcode.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/15.
 */
public class URLConstant {
    private final static String BASE_URL = "http://nacc.pub:8080/caic-ws/";

    public static final String API_GET_COM_BY_EXPRESS_NO = "http://m.kuaidi100.com/autonumber/auto";
    public static final int API_GET_COM_BY_EXPRESS_NO_WHAT = 1;
    public static final String API_GET_INFO_BY_COM_AND_EXPRESS = "https://sp0.baidu.com/9_Q4sjW91Qh3otqbppnN2DJv/pae/channel/data/asyncqury";
    public static final int API_GET_INFO_BY_COM_AND_EXPRESS_WHAT = 20;
    public static final int API_GET_INFO_BY_COM_AND_EXPRESS_WHAT1 = 21;
    public static final int API_GET_INFO_BY_COM_AND_EXPRESS_WHAT2 = 22;
    private static Map<String,Integer> URL_MAP = new HashMap<>(2);

    static {
        URL_MAP.put(API_GET_COM_BY_EXPRESS_NO,API_GET_COM_BY_EXPRESS_NO_WHAT);
        URL_MAP.put(API_GET_INFO_BY_COM_AND_EXPRESS,API_GET_INFO_BY_COM_AND_EXPRESS_WHAT);
    }

    public static int getWhat(String url){
        return URL_MAP.get(url);
    }

}
