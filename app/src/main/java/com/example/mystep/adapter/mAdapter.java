package com.example.mystep.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mystep.R;
import com.example.mystep.bean.CookieBean;

import java.util.List;

public class mAdapter extends RecyclerView.Adapter<mAdapter.MyViewHolder> {
    private Context context;
    private List<CookieBean> list;
    private View view;

    public mAdapter(Context context, List<CookieBean> list) {
        this.context = context;
        this.list = list;
    }

    private OnMyItemClickListener listener;

    public void setOnMyItemClickListener(OnMyItemClickListener listener){
        this.listener = listener;

    }

    public interface OnMyItemClickListener{
        void myClick(View v, int pos);
        void mLongClick(View v, int pos);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = View.inflate(context, R.layout.recycleview_style,null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.phone.setText(list.get(position).getPhone());
        holder.userid.setText(list.get(position).getUserid());
        holder.token.setText(list.get(position).getCookie());
        if (listener!=null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.myClick(v,position);
                }
            });

            // set LongClick
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.mLongClick(v,position);
                    return true;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView phone, userid,token ;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.id_phone);
            userid = itemView.findViewById(R.id.id_userid);
            token = itemView.findViewById(R.id.id_token);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }


    public void addItem(int position){
      /*  mDatas.add(position,"New Data");
        notifyItemInserted(position);
        notifyItemRangeChanged(position,mDatas.size());*/
    }


    public void removeData(int position){
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,list.size());

    }



}
