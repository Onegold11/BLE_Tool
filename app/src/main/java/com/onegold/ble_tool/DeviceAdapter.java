package com.onegold.ble_tool;

import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> implements OnDeviceItemClickListener {
    ArrayList<ScanResult> items = new ArrayList<>();
    OnDeviceItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TextView textView2;

        public ViewHolder(@NonNull final View itemView, final OnDeviceItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();

                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, v, postion);
                    }
                }
            });
        }

        public void setItem(ScanResult item){
            if(item.getDevice().getName() == null){
                textView.setText("No Name");
            }else{
                textView.setText(item.getDevice().getName());
            }
            textView2.setText(item.getDevice().toString());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.device_item, parent, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int postion) {
        if(listener != null){
            listener.onItemClick(holder, view, postion);
        }
    }

    public void setOnItemClickListener(OnDeviceItemClickListener listener){
        this.listener = listener;
    }

    public void addItem(ScanResult item){
        items.add(item);
        this.notifyDataSetChanged();
    }

    public void setItems(ArrayList<ScanResult> items){
        this.items = items;
    }

    public ScanResult getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, ScanResult item){
        items.set(position, item);
    }

    public boolean isExist(ScanResult item){
        for(ScanResult result : items){
            if(result.getDevice().toString().equals(item.getDevice().toString())){
                return true;
            }
        }
        return false;
    }
}
