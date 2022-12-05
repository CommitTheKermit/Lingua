package com.example.lingua;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class HorizontalAdapter extends RecyclerView.Adapter<HorizontalViewHolder> {

    private ArrayList<ButtonData> ButtonDatas;
    public void setData(ArrayList<ButtonData> list){
        ButtonDatas = list;
    }

    public void clear() {
        try{
            int size = this.ButtonDatas.size();
            this.ButtonDatas.clear();
            notifyItemRangeRemoved(0,size);
        }
        catch (Exception e){
            return;
        }

    }
    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) ((Context) context).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.word_button,parent,false);

        HorizontalViewHolder holder = new HorizontalViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        ButtonData data = ButtonDatas.get(position);

        holder.btnWordDict.setText(data.getContent());
    }

    @Override
    public int getItemCount() {
        return ButtonDatas.size();
    }
}
