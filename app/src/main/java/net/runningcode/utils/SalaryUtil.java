package net.runningcode.utils;

import android.util.ArrayMap;

/**
 * Created by Administrator on 2017/4/18.
 */

public class SalaryUtil {
    public static String[] CITY_LIST = new String[9];
    //不同地市公积金缴纳比例（整数），实际使用需除以100
    public static ArrayMap<String, Integer> GJJ_RATE = new ArrayMap<String, Integer>(9);

    static {

        GJJ_RATE.put("beijing", 12);
        GJJ_RATE.put("shanghai", 7);
        GJJ_RATE.put("tianjin", 11);
        GJJ_RATE.put("shenzhen", 20);
        GJJ_RATE.put("guangzhou", 20);
        GJJ_RATE.put("chengdu", 12);
        GJJ_RATE.put("hangzhou", 12);
        GJJ_RATE.put("xiamen", 12);
        GJJ_RATE.put("xian", 15);

        CITY_LIST[0] = "北京";
        CITY_LIST[1] = "上海";
        CITY_LIST[2] = "天津";
        CITY_LIST[3] = "深圳";
        CITY_LIST[4] = "成都";
        CITY_LIST[5] = "杭州";
        CITY_LIST[6] = "厦门";
        CITY_LIST[7] = "西安";
        CITY_LIST[8] = "广州";
    }

    //不同地市失业险缴纳比例（整数），实际使用需除以100
    private static ArrayMap<String, Double> SY_RATE_SELF = new ArrayMap<String, Double>();
    private static ArrayMap<String, Double> SY_RATE_QY = new ArrayMap<String, Double>();
    //养老金企业缴纳税率
    private static ArrayMap<String, Double> YL_RATE_QY = new ArrayMap<String, Double>();
    //医疗保险
    private static ArrayMap<String, Double> YLBX_RATE_QY = new ArrayMap<String, Double>();
    private static ArrayMap<String, Double> GS_RATE_QY = new ArrayMap<String, Double>();
    //生育险
    private static ArrayMap<String, Double> SY2_RATE_QY = new ArrayMap<String, Double>();
    private static final double DEFAULST_SY_RATE = 1.0;
    private static final double DEFAULST_SY_RATE_QY = 2.0;
    private static final double DEFAULST_YL_RATE_QY = 20;
    private static final double DEFAULST_YLBX_RATE_QY = 10;
    private static final double DEFAULST_GS_RATE_QY = 0.5;
    private static final double DEFAULST_SY2_RATE_QY = 0.8;

    static {
        SY_RATE_SELF.put("beijing", 0.2);
        SY_RATE_SELF.put("guangzhou", 0.5);
        SY_RATE_SELF.put("shanghai", 0.5);

        SY_RATE_QY.put("beijing", 0.8);
        SY_RATE_QY.put("shanghai", 1.5);
        SY_RATE_QY.put("guangzhou", 1.5);

        YL_RATE_QY.put("shanghai", 21.0);
        YL_RATE_QY.put("guangzhou", 12.0);
        YL_RATE_QY.put("hangzhou", 14.0);

        YLBX_RATE_QY.put("shanghai", 11.0);
        YLBX_RATE_QY.put("hangzhou", 11.5);
        YLBX_RATE_QY.put("guangzhou", 7.0);
        YLBX_RATE_QY.put("xian", 7.0);
        YLBX_RATE_QY.put("chengdu", 7.5);

        GS_RATE_QY.put("beijing", 1.0);
        GS_RATE_QY.put("xian", 1.0);
        GS_RATE_QY.put("chengdu", 0.6);

        SY2_RATE_QY.put("shanghai", 1.0);
        SY2_RATE_QY.put("hangzhou", 0.6);
        SY2_RATE_QY.put("xian", 0.5);
        SY2_RATE_QY.put("chengdu", 0.6);
    }

    public static double getSYRate(String city) {
        return SY_RATE_SELF.containsKey(city) ? SY_RATE_SELF.get(city) : DEFAULST_SY_RATE;
    }

    public static double getSYRateQY(String city) {
        return SY_RATE_QY.containsKey(city) ? SY_RATE_QY.get(city) : DEFAULST_SY_RATE_QY;
    }

    public static double getYLRateQY(String city) {
        return YL_RATE_QY.containsKey(city) ? YL_RATE_QY.get(city) : DEFAULST_YL_RATE_QY;
    }

    public static double getYLBXRateQY(String city) {
        return YLBX_RATE_QY.containsKey(city) ? YLBX_RATE_QY.get(city) : DEFAULST_YLBX_RATE_QY;
    }


    public static double getGSRateQY(String city) {
        return GS_RATE_QY.containsKey(city) ? GS_RATE_QY.get(city) : DEFAULST_GS_RATE_QY;
    }

    public static double getSY2XRateQY(String city) {
        return SY2_RATE_QY.containsKey(city) ? SY2_RATE_QY.get(city) : DEFAULST_SY2_RATE_QY;
    }


    /**
     * 【新版】
     * 个人所得税 = 应纳税所得额 × 税率 － 速算扣除数
     * 根据应纳税所得额获取应缴纳所得税
     *
     * @param money 应纳税所得额
     * @return
     */
    public static double getTax(double money) {
        if (money < 3000) {
            return money * 0.03;
        } else if (money <= 12000) {
            return money * 0.1 - 210;
        } else if (money <= 25000) {
            return money * 0.2 - 1410;
        } else if (money <= 35000) {
            return money * 0.25 - 2660;
        } else if (money <= 55000) {
            return money * 0.3 - 4410;
        } else if (money <= 80000) {
            return money * 0.35 - 7160;
        } else {
            return money * 0.45 - 15160;
        }
    }

    public static String getCityCode(String city) {
        switch (city) {
            case "北京":
                return "beijing";
            case "上海":
                return "shanghai";
            case "天津":
                return "tianjin";
            case "广州":
                return "guangzhou";
            case "深圳":
                return "shenzhen";
            case "成都":
                return "chengdu";
            case "西安":
                return "xian";
            case "杭州":
                return "hangzhou";
            case "厦门":
                return "xiamen";
            default:
                return "";
        }
    }
}
