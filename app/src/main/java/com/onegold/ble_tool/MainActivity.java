package com.onegold.ble_tool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.onegold.ble_tool.Callback.BLEGattServerCallback;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private Advertising advertising;
    private Scan scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoPermissions.Companion.loadAllPermissions(this, 1);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
        }

        bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        advertising = new Advertising();
        scan = new Scan();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, scan).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_scan:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, scan).commit();
                        return true;
                    case R.id.item_advertising:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, advertising).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDenied(int i, String[] strings) {

    }

    @Override
    public void onGranted(int i, String[] strings) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }
}