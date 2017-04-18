package net.runningcode.express;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.runningcode.R;
import net.runningcode.utils.DateUtil;

/**
 * Created by Administrator on 2015/9/16.
 */
public class ExpressListAdapter extends RecyclerView.Adapter<ExpressListAdapter.ViewHolder>{

    private JSONArray list;
    Context context;
    public ExpressListAdapter(Context context,JSONArray list){
        this.list = list;
        this.context = context;
    }
    @Override
    public ExpressListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject item = (JSONObject)list.get(position);
        if (position == 0){
            holder.vTime.setTextColor(context.getResources().getColor(R.color.express_red));
            holder.vDesc.setTextColor(context.getResources().getColor(R.color.express_red));
            holder.vUpLine.setVisibility(View.INVISIBLE);
            holder.vDownLine.setVisibility(View.VISIBLE);
            holder.vType.setImageResource(R.drawable.icon_right);
        }else{
            if (position == list.size() -1){
                holder.vUpLine.setVisibility(View.VISIBLE);
                holder.vDownLine.setVisibility(View.INVISIBLE);
            }
            holder.vType.setImageResource(R.drawable.icon_time);
            holder.vTime.setTextColor(context.getResources().getColor(R.color.black));
            holder.vDesc.setTextColor(context.getResources().getColor(R.color.black));
        }


        holder.vTime.setText(" "+DateUtil.getMillon(Long.parseLong(item.getString("time"))*1000));
        holder.vDesc.setText(item.getString("desc"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView vTime,vDesc;
        public ImageView vType;
        public View vUpLine,vDownLine;
        public ViewHolder(View v) {
            super(v);
            vTime = (TextView) v.findViewById(R.id.v_time);
            vDesc = (TextView) v.findViewById(R.id.v_desc);
            vType = (ImageView) v.findViewById(R.id.v_type);
            vUpLine = v.findViewById(R.id.v_line_up);
            vDownLine = v.findViewById(R.id.v_line_down);

        }
    }

}
