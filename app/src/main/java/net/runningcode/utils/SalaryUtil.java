package net.runningcode.utils;

import android.util.ArrayMap;

/**
 * Created by Administrator on 2017/4/18.
 */

public class SalaryUtil {
    //不同地市公积金缴纳比例（整数），实际使用需除以100
    public static ArrayMap<String, Integer> GJJ_RATE = new ArrayMap<String, Integer>();

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
    }

    //不同地市失业险缴纳比例（整数），实际使用需除以100
    private static ArrayMap<String, Double> SY_RATE = new ArrayMap<String, Double>();
    private static final double DEFAULST_SY_RATE = 0.5;

    static {
        SY_RATE.put("beijing", 0.2);
        SY_RATE.put("shenzhen", 1.0);
    }

    public static double getSYRate(String city) {
        return SY_RATE.containsKey(city) ? SY_RATE.get(city) : DEFAULST_SY_RATE;
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
}
