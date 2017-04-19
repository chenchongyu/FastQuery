package net.runningcode.simple_activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.utils.DialogUtils;
import net.runningcode.utils.L;
import net.runningcode.utils.SalaryUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/22.
 */

public class SalaryActivity extends BasicActivity {

    @BindView(R.id.v_salary_lable)
    TextView mVSalaryLable;
    @BindView(R.id.v_salary)
    EditText mVSalary;
    @BindView(R.id.v_city_lable)
    TextView mVCityLable;
    @BindView(R.id.v_city)
    EditText mVCity;
    @BindView(R.id.v_compute)
    Button mVCompute;
    @BindView(R.id.v_sb_lable)
    TextView mVSbLable;
    @BindView(R.id.v_sb_rate)
    EditText mVSbRate;
    @BindView(R.id.v_gjj_lable)
    TextView mVGjjLable;
    @BindView(R.id.v_gjj_rate)
    EditText mVGjjRate;
    @BindView(R.id.v_data_box)
    RelativeLayout mVDataBox;
    @BindView(R.id.v_salary_result)
    TextView mVSalaryResult;
    @BindView(R.id.v_ylj_self)
    TextView mVYljSelf;
    @BindView(R.id.v_ylj_qy)
    TextView mVYljQy;
    @BindView(R.id.v_yl_self)
    TextView mVYlSelf;
    @BindView(R.id.v_yl_qy)
    TextView mVYlQy;
    @BindView(R.id.v_sy_self)
    TextView mVSySelf;
    @BindView(R.id.v_sy_qy)
    TextView mVSyQy;
    @BindView(R.id.v_gs_self)
    TextView mVGsSelf;
    @BindView(R.id.v_gs_qy)
    TextView mVGsQy;
    @BindView(R.id.v_sy2_self)
    TextView mVSy2Self;
    @BindView(R.id.v_sy2_qy)
    TextView mVSy2Qy;
    @BindView(R.id.v_gjj_self)
    TextView mVGjjSelf;
    @BindView(R.id.v_gjj_qy)
    TextView mVGjjQy;
    @BindView(R.id.v_grsds)
    TextView mVGrsds;
    @BindView(R.id.v_total_pay_out)
    TextView mVTotalPayOut;
    @BindView(R.id.v_salary_result2)
    TextView mVSalaryResult2;

    double salary,gjjBase,sbBase;//工资、公积金、社保
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i(mVSalaryLable+":"+mVYlSelf+":"+mVSbRate+":"+mVCompute);

        initToolbar(R.color.item_orange,R.drawable.icon_salary);
        setTitle("薪资计算");
    }

    /**
     * 个税计算器计算公式
     * 应纳税所得额 = 工资收入金额 － 各项社会保险费 － 起征点(3500元)
     * 应纳税额 = 应纳税所得额 × 税率 － 速算扣除数
     *
     * @param salary 税前工资
     */
    private void compute(int salary) {
        double gjj =  gjjBase* SalaryUtil.GJJ_RATE.get("beijing") / 100;

        Double syRate = SalaryUtil.SY_RATE.get("beijing");
        if (syRate == null || syRate == 0)
            syRate = SalaryUtil.DEFAULST_SY_RATE;
        double sy = salary * syRate / 100;//失业
        double ylj = sbBase * 8 / 100;//养老金
        double ylbx = salary * 2 / 100;//医疗保险
        double gs = salary * 0 / 100;//工伤
        double syx = salary * 0 / 100;//生育
        double bxf = gjj + sy + ylj + ylbx + gs + syx;

        double ynssde = salary - bxf - 3500;
        double sds = SalaryUtil.getTax(ynssde);
        System.out.println("公积金：" + gjj + "  失业险：" + sy + "  养老金：" + ylj + "   医疗保险：" + ylbx + "总保险额：" + bxf + " 应纳税所得额：" + ynssde + "  所得税：" + sds);

        double result = salary - gjj - sy - ylj - ylbx - sds;
        System.out.println("最终所得：" + result);

        mVSalaryResult.setText(getString(R.string.salary_after_tax,result));
        mVSalaryResult2.setText(Double.toString(result));

        mVYljSelf.setText(Double.toString(ylj));
        mVYlSelf.setText(Double.toString(ylbx));
        mVSySelf.setText(Double.toString(sy));
        mVGrsds.setText(Double.toString(sds));
        mVTotalPayOut.setText(Double.toString(bxf+sds));
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_salary;
    }

    protected void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.drawable.gradient_toolbar_orange);
        setupExitAnimations();
    }

    @OnClick(R.id.v_city)
    public void onMVCityClicked() {

    }

    @OnClick(R.id.v_compute)
    public void onMVComputeClicked() {
        String salaryStr = mVSalary.getText().toString();
        String sbRate = mVSbRate.getText().toString();
        String gjjRate = mVGjjRate.getText().toString();


        if (TextUtils.isEmpty(salaryStr)){
            DialogUtils.showShortToast("你小子居心叵测啊，想让我崩溃吗？");
            return;
        }

        if (TextUtils.isEmpty(sbRate))
            mVSbRate.setText(salaryStr);
        if (TextUtils.isEmpty(gjjRate))
            mVGjjRate.setText(salaryStr);

        sbBase = Double.parseDouble(mVSbRate.getText().toString());
        gjjBase = Double.parseDouble(mVGjjRate.getText().toString());

        compute(Integer.parseInt(salaryStr));
    }
}
