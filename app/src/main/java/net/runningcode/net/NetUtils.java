//package net.runningcode.net;
//
//import com.alibaba.fastjson.JSONObject;
//
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.Callback;
//import okhttp3.HttpUrl;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//
///**
// * Created by Administrator on 2016/1/15.
// *
// *
// */
//// TODO: 2016/3/4 改用NoHttp
//public class NetUtils {
//    private static OkHttpClient instance = new OkHttpClient();
//    private static final String MEDIA_JSON_STR = "application/json; charset=utf-8";
//    private static MediaType MEDIA_JSON = MediaType.parse(MEDIA_JSON_STR);
//
//
//    static {
//        instance.newBuilder().connectTimeout(30, TimeUnit.SECONDS).retryOnConnectionFailure(true).build();
//    }
//
//    public static void get(String url,JSONObject params, Callback callback){
//        Request request = new Request.Builder().url(getURL(url,params)).build();
//        instance.newCall(request).enqueue(callback);
//    }
//
//    public static void post(String url,JSONObject params,Callback callback){
//        RequestBody requestBody = RequestBody.create(MEDIA_JSON,params.toString());
//        Request request = new Request.Builder().url(url).post(requestBody).build();
//        instance.newCall(request).enqueue(callback);
//    }
//
//    private static HttpUrl getURL(String url, JSONObject params) {
//        HttpUrl.Builder urlBuilder = new HttpUrl.Builder().host(url);
//        for (Map.Entry<String,Object> entry : params.entrySet()){
//            urlBuilder.addQueryParameter(entry.getKey(),String.valueOf(entry.getValue()));
//        }
//
//        return urlBuilder.build();
//    }
//}
