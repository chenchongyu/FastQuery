package net.runningcode.net;

import com.alibaba.fastjson.JSONObject;

import net.runningcode.constant.Constants;

/**
 * Created by Administrator on 2016/1/15.
 */
public class ParamsFactory {
    public static JSONObject getParams(){
        JSONObject params = new JSONObject();
        params.put(Constants.KEY_TOKEN,"token");

        return params;
    }
}
