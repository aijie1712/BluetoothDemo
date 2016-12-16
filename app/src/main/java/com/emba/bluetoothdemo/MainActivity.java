package com.emba.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.emba.bluetoothdemo.adapter.LeDeviceListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描并显示BLE设备
 */
public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private LeDeviceListAdapter mLeDeviceListAdapter;

    private BluetoothAdapter mBluetoothAdapter;
    // 是否正在扫描
    private boolean mScanning;

    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // 15s后停止扫描
    private static final long SCAN_PERIOD = 15000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setTitle(R.string.title_devices);
        initView();

    }

    private void initView(){
        listView = (ListView) findViewById(R.id.listView);
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        listView.setAdapter(mLeDeviceListAdapter);

        mHandler = new Handler();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };
}
