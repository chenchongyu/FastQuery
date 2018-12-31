package net.runningcode.simple_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.runningcode.BasicActivity;
import net.runningcode.R;
import net.runningcode.utils.CommonUtil;
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
    TextView mVCity;
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
    @BindView(R.id.v_total_pay_out_qy)
    TextView getmVTotalPayOutQy;
    @BindView(R.id.v_salary_result2)
    TextView mVSalaryResult2;

    @BindView(R.id.v_detail)
    View mVDetil;
    @BindView(R.id.v_title)
    View mVTitle;
    @BindView(R.id.v_result)
    View mVResult;

    double salary, gjjBase, sbBase;//工资、公积金、社保
    private String city = "北京";
    private String cityCode = "beijing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i(mVSalaryLable + ":" + mVYlSelf + ":" + mVSbRate + ":" + mVCompute);

        initToolbar(R.drawable.icon_salary);
        setTitle("薪资计算");

        mVSalary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mVSbRate.setText(s);
                mVGjjRate.setText(s);
            }
        });

        mVSalary.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onMVComputeClicked();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 个税计算器计算公式
     * 应纳税所得额 = 工资收入金额 － 各项社会保险费 － 起征点(5000元)
     * 应纳税额 = 应纳税所得额 × 税率 － 速算扣除数
     *
     * @param salary 税前工资
     */
    private void compute(double salary) {
        CommonUtil.hideInputMethod(this, mVSalary);
        computeSelf(salary); //个人
        computeQy(salary); //企业
    }

    private void computeQy(double salary) {
        double gjj = gjjBase * SalaryUtil.GJJ_RATE.get(cityCode) / 100;

        double sy = salary * SalaryUtil.getSYRateQY(cityCode) / 100;//失业
        double ylj = sbBase * SalaryUtil.getYLRateQY(cityCode) / 100;//养老金
        double ylbx = salary * SalaryUtil.getYLBXRateQY(cityCode) / 100;//医疗保险
        double gs = salary * SalaryUtil.getGSRateQY(cityCode) / 100;//工伤
        double syx = salary * SalaryUtil.getSY2XRateQY(cityCode) / 100;//生育
        double bxf = gjj + sy + ylj + ylbx + gs + syx;

        System.out.println("公积金：" + gjj + "  失业险：" + sy + "  养老金：" + ylj + "   医疗保险：" + ylbx + "总保险额：" + bxf);

        mVGsQy.setText(String.valueOf(gs * 100 / 100));
        mVSy2Qy.setText(String.valueOf(syx * 100 / 100));
        mVGjjQy.setText(String.valueOf(gjj * 100 / 100));
        mVYljQy.setText(String.valueOf(ylj * 100 / 100));
        mVYlQy.setText(String.valueOf(ylbx * 100 / 100));
        mVSyQy.setText(String.valueOf(sy * 100 / 100));
        getmVTotalPayOutQy.setText(String.valueOf(bxf * 100 / 100));
    }

    private void computeSelf(double salary) {
        double gjj = gjjBase * SalaryUtil.GJJ_RATE.get(cityCode) / 100;

        double syRate = SalaryUtil.getSYRate(cityCode);
        double sy = salary * syRate / 100;//失业
        double ylj = sbBase * 8 / 100;//养老金
        double ylbx = salary * 2 / 100;//医疗保险
        double gs = salary * 0 / 100;//工伤
        double syx = salary * 0 / 100;//生育
        double bxf = gjj + sy + ylj + ylbx + gs + syx;

        double ynssde = salary - bxf - 5000;
        double sds = SalaryUtil.getTax(ynssde);
        System.out.println("公积金：" + gjj + "  失业险：" + sy + "  养老金：" + ylj + "   医疗保险：" + ylbx + "总保险额：" + bxf + " 应纳税所得额：" + ynssde + "  所得税：" + sds);

        double result = salary - gjj - sy - ylj - ylbx - sds;
        System.out.println("最终所得：" + result);

        mVResult.setVisibility(View.VISIBLE);
        mVTitle.setVisibility(View.VISIBLE);
        mVDetil.setVisibility(View.VISIBLE);

        mVSalaryResult.setText(getString(R.string.salary_after_tax, result));
        mVSalaryResult2.setText(String.valueOf(result));

        mVGsSelf.setText(String.valueOf(gs * 100 / 100));
        mVSy2Self.setText(String.valueOf(syx * 100 / 100));
        mVGjjSelf.setText(String.valueOf(gjj * 100 / 100));
        mVYljSelf.setText(String.valueOf(ylj * 100 / 100));
        mVYlSelf.setText(String.valueOf(ylbx * 100 / 100));
        mVSySelf.setText(String.valueOf(sy * 100 / 100));
        mVGrsds.setText(String.valueOf(sds * 100 / 100)); //保留两位数
        mVTotalPayOut.setText(String.valueOf((bxf + sds) * 100 / 100));
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_salary;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.item_orange;
    }

    protected void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations(R.drawable.gradient_toolbar_orange);
        setupExitAnimations();
    }

    @OnClick(R.id.v_city)
    public void onMVCityClicked() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("选择城市")
                .setItems(SalaryUtil.CITY_LIST, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        city = SalaryUtil.CITY_LIST[which];
                        mVCity.setText(city);
                        cityCode = SalaryUtil.getCityCode(city);
                        onMVComputeClicked();
                    }
                }).create();
        dialog.show();
    }

    @OnClick(R.id.feed_back)
    public void feedBack() {
        sendEmail();
    }

    @OnClick(R.id.v_compute)
    public void onMVComputeClicked() {
        String salaryStr = mVSalary.getText().toString();
        String sbRate = mVSbRate.getText().toString();
        String gjjRate = mVGjjRate.getText().toString();


        if (TextUtils.isEmpty(salaryStr)) {
            DialogUtils.showShortToast("你小子居心叵测啊，想让我崩溃吗？");
            return;
        }

        if (TextUtils.isEmpty(sbRate)) {
            mVSbRate.setText(salaryStr);
        }
        if (TextUtils.isEmpty(gjjRate)) {
            mVGjjRate.setText(salaryStr);
        }

        sbBase = Double.parseDouble(mVSbRate.getText().toString());
        gjjBase = Double.parseDouble(mVGjjRate.getText().toString());

        compute(Double.parseDouble(salaryStr));
    }


    private void sendEmail() {
        String[] reciver = new String[]{"wochenchongyu@126.com"};
        String[] mySbuject = new String[]{"计算有误：" + city};
        String myCc = "cc";
        String mybody = "我要吐槽：";
        Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
        myIntent.setType("plain/text");
        myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
        myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
        myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);
        startActivity(Intent.createChooser(myIntent, "我要吐槽"));
    }
}
