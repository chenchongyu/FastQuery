package net.runningcode.express;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.runningcode.R;
import net.runningcode.bean.Express;
import net.runningcode.utils.L;

import java.util.List;

/**
 * Created by Administrator on 2016/4/11.
 */
public class ExpNoAdapter extends BaseAdapter{

    private List<Express> list;
    Context context;

    public ExpNoAdapter(Context context, List<Express> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Express getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_exp,null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        L.i("adapter单号："+getItem(position).getExpNo());
        holder.textView.setText(getItem(position).getExpNo());
        return convertView;
    }

    static class ViewHolder{
        TextView textView;
    }
}
