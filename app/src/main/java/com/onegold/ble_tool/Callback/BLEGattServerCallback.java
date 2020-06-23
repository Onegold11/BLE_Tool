package com.onegold.ble_tool.Callback;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.util.Log;

import lombok.Setter;

@Setter
public class BLEGattServerCallback extends BluetoothGattServerCallback {
    private BluetoothGattServer bluetoothGattServer;

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        super.onConnectionStateChange(device, status, newState);
        Log.i("Test", "onConnectionStateChange: " + status + ", new: " + newState );
    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        Log.i("Test", "onCharacteristicReadRequest: ");
        bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
    }

    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        super.onDescriptorReadRequest(device, requestId, offset, descriptor);
    }

    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
    }
}
