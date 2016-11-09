package com.xrtz.xrlive.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xrtz.xrlive.R;
import com.xrtz.xrlive.bean.NearByBean;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2016/11/3.
 */

public class NearByAdapter extends RecyclerView.Adapter<NearByAdapter.NearByViewHolder> {
    private List<NearByBean> list;
    public NearByAdapter(List<NearByBean> list){
        this.list = list;
    }

    @Override
    public NearByViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext().getApplicationContext());
        View view = inflater.inflate(R.layout.item_nearby_layout,parent,false);
        return new NearByViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NearByViewHolder holder, int position) {
        NearByBean bean = list.get(position);
        holder.distanceTv.setText(bean.getDistance());
        holder.imageView.setImageResource(bean.getResId());
        holder.imageLevel.setImageResource(bean.getLevel());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NearByViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageView imageLevel;
        TextView distanceTv;
        public NearByViewHolder(View itemView) {
            super(itemView);
            imageLevel = (ImageView) itemView.findViewById(R.id.nearby_level);
            imageView = (ImageView) itemView.findViewById(R.id.nearby_icon);
            distanceTv = (TextView) itemView.findViewById(R.id.nearby_distance);
        }
    }
}
