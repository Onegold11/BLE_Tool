package com.onegold.ble_tool;

import android.view.View;

import com.onegold.ble_tool.DeviceAdapter;

public interface OnDeviceItemClickListener {
        public void onItemClick(DeviceAdapter.ViewHolder holder, View view, int postion);
}
