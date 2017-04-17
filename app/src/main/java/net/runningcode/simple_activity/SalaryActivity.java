package net.runningcode.simple_activity;

import net.runningcode.BasicActivity;
import net.runningcode.constant.Constants;

/**
 * Created by Administrator on 2017/1/22.
 */

public class SalaryActivity extends BasicActivity {

    /**
     * 个税计算器计算公式
     应纳税所得额 = 工资收入金额 － 各项社会保险费 － 起征点(3500元)
     应纳税额 = 应纳税所得额 × 税率 － 速算扣除数

     * @param salary 税前工资
     */
    private void compute(int salary){
        double gjj = salary* Constants.GJJ_RATE.get("beijing")/100;
        double sy = salary*Constants.SY_RATE.get("beijing")/100;
        double ylj = salary*8/100;//养老金
        double ylbx = salary*2/100;//医疗保险
        double gs = salary*0/100;
        double syx = salary*0/100;
        double bxf = gjj+sy+ylj+ylbx+gs+syx;

        double ynssde = salary-bxf-3500;
        double sds = ynssde*10/100-105;
        System.out.println("公积金："+gjj+"  失业险："+sy+"  养老金："+ylj+"   医疗保险："+ylbx+"总保险额："+bxf+" 应纳税所得额："+ynssde+"  所得税："+sds);

        double result = salary - gjj-sy-ylj-ylbx-sds;
        System.out.println("最终所得："+result);
    }
    @Override
    public int getContentViewID() {
        return 0;
    }
}
