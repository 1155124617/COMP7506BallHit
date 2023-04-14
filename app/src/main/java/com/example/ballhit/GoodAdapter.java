package com.example.ballhit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class GoodAdapter extends ArrayAdapter<Goods> {
    private int resourceId;

    public GoodAdapter(@NonNull Context context, int resource, @NonNull List<Goods> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent){
        Goods goods=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        ImageView goodImage=view.findViewById(R.id.good_img);
        TextView goodPrice=view.findViewById(R.id.good_price);
        TextView goodFunc=view.findViewById(R.id.good_func);
        goodImage.setImageResource(goods.getImg_id());
        goodPrice.setText(goods.getPrice());
        goodFunc.setText(goods.getFunc());
        return view;
    }
}
