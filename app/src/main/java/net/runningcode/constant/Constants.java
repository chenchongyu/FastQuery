package net.runningcode.constant;

import android.util.ArrayMap;

/**
 * Created by Administrator on 2016/1/15.
 */
public class Constants {
    public final static String KEY_USER_NAME = "user_name";
    public final static String KEY_TOKEN = "token";
    public final static String KEY_CHANNEL = "channel";
    public final static String DEFAULT_CHANNEL = "default";

    //百度api
    public final static String BAIDU_API_KEY = "139694d38786afa74c61c486d76d7f0d";
    public final static String BAIDU_TRANS_APP_ID="20160621000023710";
    public final static String BAIDU_TRANS_APP_TOKEN="qCVTqei8Z4Ml56JCl3De";
    //阿凡达API
//    public final static String AVARDA_KEY = "f611175c9ec94f95a8fef3f15ac8666c";//正式
    public final static String AVARDA_KEY = "300f1037633d4af191fc4577fa6cda18";//测试
    //高德地图
    public final static String GAODE_API_KEY = "80e8e106504b233f97f429699d55602d";

    //安沃
    public static final String ANWO_PUBLISHER_ID = "62f88e53bc714b33b93f9a2ca291426b";
    //测试
//    public static final String ANWO_PUBLISHER_ID = "2b8dbd92edd74a97b3ba6b0189bef125";
    //mipush appkey
    public static final String MI_APP_ID = "2882303761517476257";
    public static final String MI_APP_KEY = "5741747692257";

    //易源接口
    public static final String YY_APP_ID="20714";
    public static final String YY_APP_SECRET="7648cc23a03445feacf909557c4a0d63";
    //自定义消息类型
    //打开应用商店
    public static final String TYPE_OPEN = "update";


    public static final String KEY_CAR_NO = "car_no";

    //不同地市公积金缴纳比例（整数），实际使用需除以100
    public static ArrayMap GJJ_RATE = new ArrayMap();
    static {
        GJJ_RATE.put("beijing",12);
        GJJ_RATE.put("shanghai",7);
        GJJ_RATE.put("tianjin",11);
        GJJ_RATE.put("shenzhen",20);
        GJJ_RATE.put("guangzhou",20);
        GJJ_RATE.put("zhengzhou",12);
        GJJ_RATE.put("chongqing",12);
        GJJ_RATE.put("hangzhou",12);
        GJJ_RATE.put("wuhan",12);
    }
}
