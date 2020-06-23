package com.onegold.ble_tool;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.onegold.ble_tool.Callback.BLEGattServerCallback;

import java.util.UUID;

import lombok.Builder;
import lombok.Setter;

@Setter
public class Advertising extends Fragment {
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothGattServer bluetoothGattServer;
    private BLEGattServerCallback bleGattServerCallback;

    private Button btn_start;
    private Button btn_stop;
    private TextView txt_state;

    private Boolean isRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_advertising, container, false);

        bluetoothManager = (BluetoothManager)getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        btn_start = viewGroup.findViewById(R.id.btn_start);
        btn_stop = viewGroup.findViewById(R.id.btn_stop);
        txt_state = viewGroup.findViewById(R.id.txt_state);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_state.setText("Scanning");

                if (bluetoothAdapter.isMultipleAdvertisementSupported() && !isRunning){
                    bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

                    AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
                    // <time_low> - <time_mid> - <time_high_and_version> - <variant_and_sequence> - <node>, 128bit
                    // time_log = 4 * <hexOctet>, 32bit
                    // time_mid = 2 * <hexOctet>, 16bit
                    // time_high_and_version = 2 * <hexOctet>, 16bit
                    // variant_and_sequence = 2 * <hexOctet>, 16bit
                    // node = 6 * <hexOctet>, 48bit
                    // hexOctet = <hexDigit><hexDigit>, 8bit
                    dataBuilder.addServiceUuid(new ParcelUuid(UUID.fromString("00000000-0000-1000-8000-00805f9b34fb")));
                    dataBuilder.setIncludeDeviceName(true);

                    AdvertiseSettings.Builder settingBuilder = new AdvertiseSettings.Builder();
                    settingBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
                    settingBuilder.setTimeout(0);

                    settingBuilder.setConnectable(true);

                    AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
                        @Override
                        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                            super.onStartSuccess(settingsInEffect);
                            txt_state.setText("Scan success");

                            bleGattServerCallback = new BLEGattServerCallback();
                            bluetoothGattServer = bluetoothManager.openGattServer(getContext(), bleGattServerCallback);
                            bleGattServerCallback.setBluetoothGattServer(bluetoothGattServer);

                            BluetoothGattService service = new BluetoothGattService(UUID.fromString("00000000-0000-2000-9000-001111111111"), BluetoothGattService.SERVICE_TYPE_PRIMARY);

                            BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(UUID.fromString("00000000-0000-3000-a000-002222222222"),
                                    BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                                    BluetoothGattCharacteristic.PERMISSION_READ);

                            characteristic.addDescriptor(new BluetoothGattDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattCharacteristic.PERMISSION_WRITE));

                            service.addCharacteristic(characteristic);

                            bluetoothGattServer.addService(service);
                        }

                        @Override
                        public void onStartFailure(int errorCode) {
                            super.onStartFailure(errorCode);
                            txt_state.setText("Scan failure");
                        }
                    };

                    bluetoothLeAdvertiser.startAdvertising(settingBuilder.build(), dataBuilder.build(), advertiseCallback);
                    isRunning = true;
                }else{
                    boolean f = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
                    boolean s = bluetoothAdapter.isMultipleAdvertisementSupported();
                    Log.e("Test", "versionFailure " + f + " " + s);
                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAdvertising();
            }
        });

        return viewGroup;
    }
    private void stopAdvertising(){
        if(isRunning){
            txt_state.setText("Scan stopping");
            AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    txt_state.setText("Scan stop success");
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);
                    txt_state.setText("Scan stop failure");
                }
            };
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            isRunning = false;
        }else{
            txt_state.setText("Not running");
        }
    }

    @Override
    public void onPause() {
        if(bluetoothLeAdvertiser != null) {
            stopAdvertising();
        }
        super.onPause();
    }
}