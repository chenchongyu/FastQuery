package net.runningcode;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yolanda.nohttp.Response;

import net.runningcode.constant.URLConstant;
import net.runningcode.net.HttpListener;
import net.runningcode.net.WaitDialog;

import java.util.List;

/**
 * Created by Administrator on 2016/1/15.
 */
public class IndexActivity extends BasicActivity implements View.OnClickListener, HttpListener<JSON> {
    private TextView vWeather,vTemperature,vPm,vCity,vDate;
    private ImageView vIcon;
    private GridView vTable;
    private WaitDialog dialog;
    private JSONArray coms;
    private SparseArray<String> map;
    private List<Integer> list;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
    }

    @Override
    protected boolean showActionbar() {
        return true;
    }

    private void initView() {
        vWeather = $(R.id.v_weather_text);
        vIcon = $(R.id.v_weather_icon);
        vTemperature = $(R.id.v_temperature);
        vPm = $(R.id.v_pm);
        vCity = $(R.id.v_city);
        vDate = $(R.id.v_date);
        vTable = $(R.id.v_table);

        initData();
//        setActionBarColor(R.color.colorPrimary);
//        setStatusBarColor(R.color.colorPrimaryDark);
    }

    private void initData() {
        map = new SparseArray<>();
        map.put(R.drawable.icon_express,"快递查询");
        map.put(R.drawable.icon_car,"汽车摇号");

        list.add(R.drawable.icon_express);
        list.add(R.drawable.icon_car);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        }
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_index;
    }


    @Override
    public void onSucceed(int what, Response<JSON> response) {
        switch (what){
            case URLConstant.API_GET_COM_BY_EXPRESS_NO_WHAT:

                break;
            case URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT:
            case URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT1:
            case URLConstant.API_GET_INFO_BY_COM_AND_EXPRESS_WHAT2:

                break;
        }

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception message, int responseCode, long networkMillis) {
        dialog.dismiss();

    }

    class ItemsAdapter extends BaseAdapter{
        private List<Integer> list;

        public ItemsAdapter(List<Integer> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list.get(i);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            int icon = list.get(position);
            String title = map.get(icon);
            ViewHolder holder = null;
            if (view == null){
                holder = new ViewHolder();
                view = View.inflate(IndexActivity.this, R.layout.item_item, null);
                holder.vIcon = (ImageView) view.findViewById(R.id.v_icon);
            }

            return null;
        }

        class ViewHolder{
            ImageView vIcon;
            TextView vText;
        }
    }
}
