package com.emba.bluetoothdemo;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emba.bluetoothdemo.service.BluetoothLeService;
import com.emba.bluetoothdemo.utils.DbUtils;
import com.emba.bluetoothdemo.utils.HexUtil;
import com.emba.bluetoothdemo.utils.MessageUtils;
import com.emba.bluetoothdemo.utils.SampleGattAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 对于一个BLE设备，该activity向用户提供设备连接，显示数据，显示GATT服务和设备的字符串支持等界面，
 * 另外这个activity还与BluetoothLeService通讯，反过来与Bluetooth LE API进行通讯
 */
public class DeviceControlActivity extends AppCompatActivity implements
        View.OnClickListener {
    private final static String TAG = DeviceControlActivity.class
            .getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    // 连接状态
    private TextView mConnectionState;
    private EditText mDataField;
    private String mDeviceName;
    private String mDeviceAddress;

    private Button button_export;
    private Button button_send_test;
    private Button button_send_value; // 发送按钮
    private EditText edittext_input_value; // 数据在这里输入

    private BluetoothLeService mBluetoothLeService;

    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private Button btn_select, btn_operate_hidden, btn_operate_show;
    private TextView tv_result;

    // 写数据
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattService mnotyGattService;
    ;
    // 读数据
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService readMnotyGattService;
    byte[] WriteBytes = new byte[20];

    // 管理服务的生命周期
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.处理服务所激发的各种事件
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接一个GATT服务
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.从GATT服务中断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.查找GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.从服务中接受数据
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                DbUtils.save("已连接");
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                DbUtils.save("未连接");
                invalidateOptionsMenu();
                clearUI();
            }
            // 发现有可支持的服务
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                // 写数据的服务和characteristic
                mnotyGattService = mBluetoothLeService
                        .getSupportedGattServices(UUID
                                .fromString(SampleGattAttributes.MYCJ_BLE));
                if (mnotyGattService != null) {
                    characteristic = mnotyGattService
                            .getCharacteristic(UUID
                                    .fromString(SampleGattAttributes.MYCJ_BLE_WRITE));
                    DbUtils.save("获得服务成功:" + characteristic.getUuid());
                    if (characteristic != null) {
                        DbUtils.save("获得写特征成功:" + characteristic.getUuid());
                    }
                } else {
                    // 服务为空
                    Toast.makeText(context, "写服务为空，请确认连接正确的蓝牙设备",
                            Toast.LENGTH_SHORT).show();
                }

                // 读数据的服务和characteristic
                readMnotyGattService = mBluetoothLeService
                        .getSupportedGattServices(UUID
                                .fromString(SampleGattAttributes.MYCJ_BLE));
                if (readMnotyGattService != null) {
                    readCharacteristic = readMnotyGattService
                            .getCharacteristic(UUID
                                    .fromString(SampleGattAttributes.MYCJ_BLE_READ));
                    if (readCharacteristic != null) {
                        DbUtils.save("获得读特征成功：" + readCharacteristic.getUuid());
                    }
                } else {
                    Toast.makeText(context, "读服务为空，请确认连接正确的蓝牙设备",
                            Toast.LENGTH_SHORT).show();
                }
            }
            // 显示数据
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // 将数据显示在mDataField上
                String data = intent
                        .getStringExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println("data----" + data);
                DbUtils.save("接受到数据：" + data);
                displayData(data);
            }
        }
    };

    private void clearUI() {
        // mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        Toast.makeText(DeviceControlActivity.this, "连接断开", Toast.LENGTH_SHORT).show();
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        initView();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (EditText) findViewById(R.id.data_value);

        button_send_value = (Button) findViewById(R.id.button_send_value);
        edittext_input_value = (EditText) findViewById(R.id.edittext_input_value);

        button_export = (Button) findViewById(R.id.button_export);
        button_export.setOnClickListener(this);

        button_send_test = (Button) findViewById(R.id.button_send_test);
        button_send_test.setOnClickListener(this);

        button_send_value.setOnClickListener(this);

        getSupportActionBar().setTitle(mDeviceName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initView() {
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_operate_hidden = (Button) findViewById(R.id.btn_operate_hidden);
        btn_operate_show = (Button) findViewById(R.id.btn_operate_show);

        btn_select.setOnClickListener(this);
        btn_operate_hidden.setOnClickListener(this);
        btn_operate_show.setOnClickListener(this);

        tv_result = (TextView) findViewById(R.id.tv_result);
    }

    byte flag = 0x00;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select:
                WriteBytes = MessageUtils.appSelectBleAllData();
                edittext_input_value
                        .setText(MessageUtils.getByteString(WriteBytes));
                DbUtils.save("设置查询：" + edittext_input_value.getText().toString().trim());
                break;
            case R.id.btn_operate_hidden:
                flag = 0x00;
                WriteBytes = MessageUtils.appSetLEDStatus(flag);
                edittext_input_value
                        .setText(MessageUtils.getByteString(WriteBytes));
                DbUtils.save("设置发送：" + edittext_input_value.getText().toString().trim());
                break;
            case R.id.btn_operate_show:
                flag = 0x01;
                WriteBytes = MessageUtils.appSetLEDStatus(flag);
                edittext_input_value
                        .setText(MessageUtils.getByteString(WriteBytes));
                DbUtils.save("设置发送：" + edittext_input_value.getText().toString().trim());
                break;
            case R.id.button_send_value:
                sendData();
                break;
            case R.id.button_send_test:  // 发送测试数据

                break;
            case R.id.button_export:
                exportExcel();
                break;
        }
    }

    private void exportExcel() {
        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在将数据导出到Excel……");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result;
                String path;
                try {
                    path = DbUtils.createExcel();
                    if (TextUtils.isEmpty(path)) {
                        Toast.makeText(getApplicationContext(), "沒有数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    result = "导出到Excel成功！";
                } catch (Exception e) {
                    path = "";
                    e.printStackTrace();
                    result = "导出失败：" + e.getMessage();
                }
                final String finalResult = result;
                final String finalPath = path;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), finalResult, Toast.LENGTH_SHORT).show();
                        //发送到手机QQ
                        if (!TextUtils.isEmpty(finalPath)) {
                            String packageName = "com.tencent.mobileqq";
                            try {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setPackage(packageName);
                                intent.setType("*/*");
                                intent.putExtra(Intent.EXTRA_STREAM, finalPath);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void sendData() {
        read();

        if (characteristic == null) {
            Toast.makeText(getApplicationContext(),
                    "写特征为空，请确认连接正确的蓝牙设备", Toast.LENGTH_SHORT).show();
            return;
        }

        final int charaProp = characteristic.getProperties();

        // 如果该char可写
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            // If there is an active notification on a characteristic,
            // clear
            // it first so it doesn't update the data field on the user
            // interface.
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification(
                        mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            // 读取数据，数据将在回调函数中
            // mBluetoothLeService.readCharacteristic(characteristic);
            byte[] value = new byte[20];
            value[0] = (byte) 0x00;
            if (edittext_input_value.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "请输入！",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                // WriteBytes =
                // edittext_input_value.getText().toString()
                // .getBytes();
                // characteristic.setValue(value[0],BluetoothGattCharacteristic.FORMAT_UINT8,
                // 0);
                // characteristic.setValue(value[0],BluetoothGattCharacteristic.FORMAT_SINT16,
                // 0);
//                characteristic.setValue(WriteBytes);
//                mBluetoothLeService.writeCharacteristic(characteristic);
//                DbUtils.save("写入数据：" + HexUtil.encodeHexStr(WriteBytes));
                mBluetoothLeService.writeStringToGatt(edittext_input_value.getText().toString());
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(
                    characteristic, true);
        }
        edittext_input_value.setText("");
    }

    /*
     * **************************************************************
     * *****************************读函数*****************************
     */
    private void read() {
        // mBluetoothLeService.readCharacteristic(readCharacteristic);
        // readCharacteristic的数据发生变化，发出通知
        if (mBluetoothLeService != null && readCharacteristic != null) {
            mBluetoothLeService.setCharacteristicNotification(
                    readCharacteristic, true);
        } else {
            Toast.makeText(this, "读失败，服务为空，请确认连接正确的蓝牙设备", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                Toast.makeText(DeviceControlActivity.this, "开始连接", Toast.LENGTH_SHORT).show();
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
            // tv_result
            String hexData = data.split("--")[1];
            if (!TextUtils.isEmpty(hexData)) {
                byte[] byteData = HexUtil.hexStringToBytes(data);
                if (byteData != null && byteData.length > 4) {
                    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    byte temperature = byteData[2]; // 温度
                    byte air = byteData[3]; // 空气质量 0--ff
                    byte LED_show = byteData[4]; // LED显示
                    byte baby_sit = byteData[5]; // 宝宝坐下 0-未坐下 1-坐下
                    String temp_10 = Integer.toString(temperature, 10);
                    String air_10 = Integer.toString(air, 10);
                    tv_result.append("时间:" + time);
                    tv_result.append("--温度:" + temp_10);
                    tv_result.append("--空气质量:" + air_10);
                    tv_result.append("--led显示:" + (LED_show == 0 ? "不显示" : "显示"));
                    tv_result.append("--宝宝坐下:" + (baby_sit == 0 ? "没有坐下" : "儿童坐下") + "\n");
                }
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
