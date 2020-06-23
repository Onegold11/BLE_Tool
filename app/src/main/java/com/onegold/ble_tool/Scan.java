package com.onegold.ble_tool;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Scan extends Fragment {
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private DeviceAdapter deviceAdapter;
    private RecyclerView recyclerView;

    private boolean isRunning = false;
    private Handler handler;

    Button btn_scan;

    private static final long SCAN_PERIOD = 10000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_scan, container, false);

        bluetoothManager = (BluetoothManager)getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        handler = new Handler();

        btn_scan = viewGroup.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLeDevice();
            }
        });

        deviceAdapter = new DeviceAdapter();
        deviceAdapter.setOnItemClickListener(new OnDeviceItemClickListener() {
            @Override
            public void onItemClick(DeviceAdapter.ViewHolder holder, View view, int postion) {
                ScanResult result = deviceAdapter.getItem(postion);
                result.getDevice().connectGatt(getContext(), false, bleGattCallback, BluetoothDevice.TRANSPORT_AUTO);
            }
        });

        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(deviceAdapter);
        deviceAdapter.notifyDataSetChanged();
        return viewGroup;
    }

    private void scanLeDevice(){
        if (!isRunning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, SCAN_PERIOD);

            isRunning = true;
            bluetoothLeScanner.startScan(bleScanCallback);
        } else {
            stopScan();
        }
    }

    private void stopScan(){
        if(isRunning){
            isRunning = false;
            bluetoothLeScanner.stopScan(bleScanCallback);
        }
    }

    @Override
    public void onPause() {
        if(isRunning && bluetoothLeScanner != null){
            stopScan();
        }
        super.onPause();
    }

    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            if(!deviceAdapter.isExist(result)){
                deviceAdapter.addItem(result);
            }
        }

        @Override
        public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results) {
            super.onBatchScanResults(results);

            for (ScanResult scanResult : results){
                if(!deviceAdapter.isExist(scanResult)){
                    deviceAdapter.addItem(scanResult);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

    };

    private BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("Test", "Connected to GATT server.");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                Log.i("Test", "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    Log.i("Test", "onServicesDiscovered " + service.getUuid().toString());
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        ///Once you have a characteristic object, you can perform read/write
                        //operations with it
                        gatt.readCharacteristic(characteristic);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Toast.makeText(getContext(), "onCharacteristicRead " + characteristic.getUuid().toString(), Toast.LENGTH_LONG).show();
        }
    };
}