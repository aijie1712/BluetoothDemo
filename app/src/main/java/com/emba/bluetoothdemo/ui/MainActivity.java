//package com.emba.bluetoothdemo.ui;
//
///**
// * Created by Administrator on 2016-12-19
// *
// * @desc
// */
//
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCallback;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattDescriptor;
//import android.bluetooth.BluetoothGattService;
//import android.bluetooth.BluetoothManager;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.method.ScrollingMovementMethod;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.emba.bluetoothdemo.utils.HexUtil;
//import com.emba.bluetoothdemo.utils.SampleGattAttributes;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//public class MainActivity extends Activity {
//    public static final UUID UUID_HEART_RATE_MEASUREMENT;
//    private static final UUID UUID_READ;
//    private static final UUID UUID_SERVICE = UUID.fromString(SampleGattAttributes.MYCJ_BLE);
//    private static final UUID UUID_WRITE;
//    private static final UUID uuid;
//    ImageButton b1;
//    ImageButton b2;
//    ImageButton b3;
//    ImageButton b4;
//    boolean bo_b1 = false;
//    boolean bo_b2 = false;
//    boolean bo_b3 = false;
//    boolean bo_b4 = false;
//    Button clear_button;
//    boolean connected = false;
//    private byte[] data;
//    private ArrayList<String> deviceAddressList;
//    Handler handler11 = new Handler() {
//        public void handleMessage(Message paramMessage) {
//            if (paramMessage.what == 10000) {
//                System.out.println("连接 成功 ");
//                MainActivity.this.enable_JDY_ble(true, 1);
//                System.out.println("状态:已连接");
//            }
//            super.handleMessage(paramMessage);
//        }
//    };
//    View.OnClickListener listener = new View.OnClickListener() {
//        public void onClick(View paramView) {
//            switch (paramView.getId()) {
//                default:
//                    return;
//                case 2131230734:
//                    MainActivity.this.rxd_count = 0;
//                    MainActivity.this.txd_count = 0;
//                    MainActivity.this.r_text.setText("接收数据：0");
//                    MainActivity.this.t_text.setText("发送数据：0");
//                    return;
//                case 2131230720:
//                    if (!MainActivity.this.bo_b1) {
//                        MainActivity.this.bo_b1 = true;
//                        MainActivity.this.b1.setImageResource(2130837504);
//                        return;
//                    }
//                    MainActivity.this.bo_b1 = false;
//                    MainActivity.this.b1.setImageResource(2130837505);
//                    return;
//                case 2131230723:
//                    if (!MainActivity.this.bo_b2) {
//                        MainActivity.this.bo_b2 = true;
//                        MainActivity.this.b2.setImageResource(2130837504);
//                        return;
//                    }
//                    MainActivity.this.bo_b2 = false;
//                    MainActivity.this.b2.setImageResource(2130837505);
//                    return;
//                case 2131230726:
//                    if (!MainActivity.this.bo_b3) {
//                        MainActivity.this.bo_b3 = true;
//                        MainActivity.this.b3.setImageResource(2130837504);
//                        return;
//                    }
//                    MainActivity.this.bo_b3 = false;
//                    MainActivity.this.b3.setImageResource(2130837505);
//                    return;
//                case 2131230729:
//            }
//            if (!MainActivity.this.bo_b4) {
//                MainActivity.this.bo_b4 = true;
//                MainActivity.this.b4.setImageResource(2130837504);
//                return;
//            }
//            MainActivity.this.bo_b4 = false;
//            MainActivity.this.b4.setImageResource(2130837505);
//        }
//    };
//    private ListView lv;
//    private BluetoothAdapter myBluetoothAdapter;
//    private BluetoothDevice myBluetoothDevice;
//    private String myBluetoothDeviceAddress;
//    private BluetoothGatt myBluetoothGatt;
//    private BluetoothManager myBluetoothManager;
//    private BluetoothGattCallback myGattCallback = new BluetoothGattCallback() {
//        public void onCharacteristicChanged(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic) {
//            super.onCharacteristicChanged(paramBluetoothGatt, paramBluetoothGattCharacteristic);
//            MainActivity.this.data = paramBluetoothGattCharacteristic.getValue();
//            if ((MainActivity.this.data != null) && (MainActivity.this.data.length > 0)) {
//                MainActivity.this.strData = (HexUtil.encodeHexStr(MainActivity.this.data) + "\n");
//                System.out.println("收到的数据是 ： " + MainActivity.this.strData);
//                MainActivity.this.sbValues.insert(0, MainActivity.this.strData);
//                MainActivity.this.myHandler.post(new Runnable() {
//                    public void run() {
//                        MainActivity.this.textViewValues.setText(MainActivity.this.strData);
//                        MainActivity localMainActivity = MainActivity.this;
//                        localMainActivity.rxd_count += MainActivity.this.data.length;
//                        MainActivity.this.r_text.setText("接收数据：" + MainActivity.this.rxd_count);
//                    }
//                });
//            }
//        }
//
//        public void onCharacteristicRead(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt) {
//            super.onCharacteristicRead(paramBluetoothGatt, paramBluetoothGattCharacteristic, paramInt);
//        }
//
//        public void onCharacteristicWrite(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt) {
//            super.onCharacteristicWrite(paramBluetoothGatt, paramBluetoothGattCharacteristic, paramInt);
//        }
//
//        public void onConnectionStateChange(BluetoothGatt paramBluetoothGatt, int paramInt1, int paramInt2) {
//            super.onConnectionStateChange(paramBluetoothGatt, paramInt1, paramInt2);
//            if (paramInt2 == 2) {
//                System.err.println("连接上GATT！");
//                MainActivity.this.myBluetoothGatt.getServices();
//                MainActivity.this.myBluetoothGatt.discoverServices();
//                MainActivity.this.myHandler.post(new Runnable() {
//                    public void run() {
//                        MainActivity.this.textViewValues.setText("已连接");
//                        MainActivity.this.connected = true;
//                    }
//                });
//            }
//            do
//                return;
//            while (paramInt2 != 0);
//            System.out.println("连接断开！");
//            MainActivity.this.myHandler.post(new Runnable() {
//                public void run() {
//                    MainActivity.this.textViewValues.setText("连接断开！");
//                    MainActivity.this.connected = false;
//                }
//            });
//        }
//
//        public void onReadRemoteRssi(BluetoothGatt paramBluetoothGatt, int paramInt1, int paramInt2) {
//            super.onReadRemoteRssi(paramBluetoothGatt, paramInt1, paramInt2);
//        }
//
//        public void onServicesDiscovered(BluetoothGatt paramBluetoothGatt, int paramInt) {
//            super.onServicesDiscovered(paramBluetoothGatt, paramInt);
//            if (MainActivity.this.myBluetoothGatt != null)
//                MainActivity.this.myGattService = MainActivity.this.myBluetoothGatt.getService(MainActivity.uuid);
//            if (MainActivity.this.myGattService != null) {
//                String str = MainActivity.this.myGattService.getUuid().toString();
//                System.out.println("获得BLE GATT Services 成功 : " + str);
//                MainActivity.this.readGattCharacteristic = MainActivity.this.myGattService.getCharacteristic(MainActivity.UUID_READ);
//                MainActivity.this.setCharacteristicNotification(MainActivity.this.readGattCharacteristic, true);
//                MainActivity.this.textViewValues.setText("获得BLE GATT Services 成功 ");
//            }
//            if (MainActivity.this.readGattCharacteristic != null) {
//                System.out.println("找到了READ和WRITE");
//                MainActivity.this.setCharacteristicNotification(MainActivity.this.readGattCharacteristic, true);
//                boolean bool = MainActivity.this.myBluetoothGatt.readCharacteristic(MainActivity.this.readGattCharacteristic);
//                System.out.println("READ状态 ： " + bool);
//                MainActivity.this.textViewValues.setText("找到了READ和WRITE");
//            }
//        }
//    };
//    private BluetoothGattService myGattService;
//    private Handler myHandler;
//    private BluetoothAdapter.LeScanCallback myReLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        public void onLeScan(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfByte) {
//            System.out.println("找到了 + Rssi : " + paramInt + "  deviceAddress : " + paramBluetoothDevice.getAddress());
//            if (!MainActivity.this.deviceAddressList.contains(paramBluetoothDevice.getAddress()))
//                MainActivity.this.deviceAddressList.add(paramBluetoothDevice.getAddress());
//        }
//    };
//    private Runnable myRunnable;
//    private ProgressBar pbar;
//    TextView r_text;
//    private BluetoothGattCharacteristic readGattCharacteristic;
//    int rxd_count = 0;
//    private StringBuffer sbValues;
//    private Button sendBtn2;
//    private Button sendBtn3;
//    private Button sendBtn4;
//    private Button sendBtn5;
//    private EditText sendText2;
//    private EditText sendText3;
//    private EditText sendText4;
//    private EditText sendText5;
//    private String strData;
//    TextView t_text;
//    private TextView textViewValues;
//    private TextView tv_hint;
//    int txd_count = 0;
//    private BluetoothGattCharacteristic writeGattCharacteristic;
//
//    static {
//        UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
//        uuid = UUID.fromString(SampleGattAttributes.MYCJ_BLE);
//        UUID_READ = UUID.fromString(SampleGattAttributes.MYCJ_BLE_READ);
//        UUID_WRITE = UUID.fromString(SampleGattAttributes.MYCJ_BLE_WRITE);
//    }
//
//    private void actionAlertDialog() {
//        View localView = getLayoutInflater().inflate(2130903041, null);
//        this.lv = ((ListView) localView.findViewById(2131230736));
//        this.tv_hint = ((TextView) localView.findViewById(2131230738));
//        this.pbar = ((ProgressBar) localView.findViewById(2131230737));
//        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
//        localBuilder.setView(localView);
//        localBuilder.setPositiveButton("重新扫描", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                MainActivity.this.reScanLeDevice(true);
//                MainActivity.this.actionAlertDialog();
//            }
//        });
//        localBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                System.out.println("取消");
//                paramDialogInterface.dismiss();
//            }
//        });
//        AlertDialog localAlertDialog = localBuilder.create();
//        localAlertDialog.show();
//        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener(localAlertDialog) {
//            public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
//                TextView localTextView = (TextView) paramView;
//                MainActivity.this.myBluetoothDeviceAddress = localTextView.getText().toString().trim();
//                this.val$alertDialog.cancel();
//                MainActivity.this.connect(MainActivity.this.myBluetoothDeviceAddress);
//            }
//        });
//    }
//
//    private void getRssi() {
//        this.myBluetoothGatt.readRemoteRssi();
//    }
//
//    private void writeStringToGatt(String paramString) {
//        if ((paramString != null) && (this.connected)) {
//            byte[] arrayOfByte = DataUtil.getBytesByString(paramString);
//            BluetoothGattCharacteristic localBluetoothGattCharacteristic = this.myBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")).getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
//            localBluetoothGattCharacteristic.setValue(arrayOfByte);
//            this.myBluetoothGatt.writeCharacteristic(localBluetoothGattCharacteristic);
//            this.txd_count += arrayOfByte.length;
//            this.t_text.setText("发送数据：" + this.txd_count);
//        }
//    }
//
//    private void writeStringToGatt_1(String paramString) {
//        if ((paramString != null) && (this.connected)) {
//            byte[] arrayOfByte = paramString.getBytes();
//            BluetoothGattCharacteristic localBluetoothGattCharacteristic = this.myBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")).getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
//            localBluetoothGattCharacteristic.setValue(arrayOfByte);
//            this.myBluetoothGatt.writeCharacteristic(localBluetoothGattCharacteristic);
//            this.txd_count += arrayOfByte.length;
//            this.t_text.setText("发送数据：" + this.txd_count);
//        }
//    }
//
//    public boolean connect(String paramString) {
//        if ((this.myBluetoothAdapter == null) || (paramString == null))
//            return false;
//        if ((this.myBluetoothDeviceAddress != null) && (paramString.equals(this.myBluetoothDeviceAddress)) && (this.myBluetoothGatt != null)) {
//            if (this.myBluetoothGatt.connect()) {
//                System.out.println("mBluetoothGatt不为空");
//                return true;
//            }
//            return false;
//        }
//        BluetoothDevice localBluetoothDevice = this.myBluetoothAdapter.getRemoteDevice(paramString);
//        if (localBluetoothDevice == null)
//            return false;
//        this.myBluetoothGatt = localBluetoothDevice.connectGatt(this, true, this.myGattCallback);
//        this.myBluetoothDeviceAddress = paramString;
//        return true;
//    }
//
//    public void disconnect() {
//        if ((this.myBluetoothAdapter == null) || (this.myBluetoothGatt == null))
//            return;
//        this.myBluetoothGatt.disconnect();
//    }
//
//    public void enable_JDY_ble(boolean paramBoolean, int paramInt) {
//        if ((this.myBluetoothAdapter == null) || (this.myBluetoothGatt == null))
//            return;
//        setCharacteristicNotification(this.writeGattCharacteristic, true);
//    }
//
//    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
//        super.onActivityResult(paramInt1, paramInt2, paramIntent);
//    }
//
//    protected void onCreate(Bundle paramBundle) {
//        super.onCreate(paramBundle);
//        setContentView(2130903040);
//        this.b1 = ((ImageButton) findViewById(2131230720));
//        this.b1.setOnClickListener(this.listener);
//        this.b2 = ((ImageButton) findViewById(2131230723));
//        this.b2.setOnClickListener(this.listener);
//        this.b3 = ((ImageButton) findViewById(2131230726));
//        this.b3.setOnClickListener(this.listener);
//        this.b4 = ((ImageButton) findViewById(2131230729));
//        this.b4.setOnClickListener(this.listener);
//        this.clear_button = ((Button) findViewById(2131230734));
//        this.clear_button.setOnClickListener(this.listener);
//        this.r_text = ((TextView) findViewById(2131230732));
//        this.t_text = ((TextView) findViewById(2131230733));
//        this.r_text.setText("接收数据：0");
//        this.t_text.setText("发送数据：0");
//        this.sendBtn2 = ((Button) findViewById(2131230722));
//        this.sendBtn3 = ((Button) findViewById(2131230725));
//        this.sendBtn4 = ((Button) findViewById(2131230728));
//        this.sendBtn5 = ((Button) findViewById(2131230731));
//        this.sendText2 = ((EditText) findViewById(2131230721));
//        this.sendText3 = ((EditText) findViewById(2131230724));
//        this.sendText4 = ((EditText) findViewById(2131230727));
//        this.sendText5 = ((EditText) findViewById(2131230730));
//        this.textViewValues = ((TextView) findViewById(2131230735));
//        this.textViewValues.setMovementMethod(ScrollingMovementMethod.getInstance());
//        this.sbValues = new StringBuffer();
//        this.myHandler = new Handler();
//        this.deviceAddressList = new ArrayList();
//        this.myBluetoothManager = ((BluetoothManager) getSystemService("bluetooth"));
//        this.myBluetoothAdapter = this.myBluetoothManager.getAdapter();
//        if (this.myBluetoothAdapter.isEnabled()) {
//            System.out.println("蓝牙已开启...");
//            return;
//        }
//        startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));
//    }
//
//    public boolean onCreateOptionsMenu(Menu paramMenu) {
//        System.out.println("-------------Call Menu onCreateOptionsMenu......");
//        getMenuInflater().inflate(2131165184, paramMenu);
//        return true;
//    }
//
//    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
//        switch (paramMenuItem.getItemId()) {
//            default:
//            case 2131230739:
//        }
//        while (true) {
//            return super.onOptionsItemSelected(paramMenuItem);
//            Toast.makeText(getApplicationContext(), "查找蓝牙设备", 0).show();
//            reScanLeDevice(true);
//            actionAlertDialog();
//        }
//    }
//
//    protected void onRestart() {
//        super.onRestart();
//    }
//
//    protected void onResume() {
//        super.onResume();
//        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
//    }
//
//    protected void onStart() {
//        super.onStart();
//        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
//    }
//
//    protected void onStop() {
//        super.onStop();
//    }
//
//    public void reScanLeDevice(boolean paramBoolean) {
//        if (this.deviceAddressList != null)
//            this.deviceAddressList.clear();
//        if (this.myBluetoothGatt != null) {
//            this.myBluetoothGatt.close();
//            this.myBluetoothGatt = null;
//        }
//        if (this.myBluetoothAdapter != null) {
//            this.myBluetoothAdapter.stopLeScan(this.myReLeScanCallback);
//            this.myBluetoothAdapter.startLeScan(this.myReLeScanCallback);
//            this.myHandler.postDelayed(new Runnable() {
//                                           public void run() {
//                                               System.out.println("查找3秒后结束……");
//                                               MainActivity.this.setView();
//                                               MainActivity.this.myBluetoothAdapter.stopLeScan(MainActivity.this.myReLeScanCallback);
//                                           }
//                                       }
//                    , 2000L);
//            System.out.println("开始重新查找蓝牙设备");
//        }
//    }
//
//    public void scanLeDevice() {
//        this.myHandler.post(new Runnable() {
//            public void run() {
//                if (MainActivity.this.myBluetoothAdapter != null)
//                    MainActivity.this.myBluetoothAdapter.startLeScan(MainActivity.this.myReLeScanCallback);
//            }
//        });
//    }
//
//    public void sendBtn2(View paramView) {
//        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
//        if (!this.bo_b1) {
//            writeStringToGatt(this.sendText2.getText().toString().trim());
//            return;
//        }
//        writeStringToGatt_1(this.sendText2.getText().toString().trim());
//    }
//
//    public void sendBtn3(View paramView) {
//        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
//        if (!this.bo_b2) {
//            writeStringToGatt(this.sendText3.getText().toString().trim());
//            return;
//        }
//        writeStringToGatt_1(this.sendText3.getText().toString().trim());
//    }
//
//    public void sendBtn4(View paramView) {
//        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
//        if (!this.bo_b3) {
//            writeStringToGatt(this.sendText4.getText().toString().trim());
//            return;
//        }
//        writeStringToGatt_1(this.sendText4.getText().toString().trim());
//    }
//
//    public void sendBtn5(View paramView) {
//        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
//        if (!this.bo_b4) {
//            writeStringToGatt(this.sendText5.getText().toString().trim());
//            return;
//        }
//        writeStringToGatt_1(this.sendText5.getText().toString().trim());
//    }
//
//    public void setCharacteristicNotification(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean) {
//        if ((this.myBluetoothAdapter == null) || (this.myBluetoothGatt == null)) ;
//        do {
//            return;
//            this.myBluetoothGatt.setCharacteristicNotification(paramBluetoothGattCharacteristic, paramBoolean);
//            if (UUID_HEART_RATE_MEASUREMENT.equals(paramBluetoothGattCharacteristic.getUuid())) {
//                BluetoothGattDescriptor localBluetoothGattDescriptor3 = paramBluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//                localBluetoothGattDescriptor3.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                this.myBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor3);
//                return;
//            }
//            if (!UUID_READ.equals(paramBluetoothGattCharacteristic.getUuid()))
//                continue;
//            BluetoothGattDescriptor localBluetoothGattDescriptor2 = paramBluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            localBluetoothGattDescriptor2.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            this.myBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor2);
//            return;
//        }
//        while (!UUID_WRITE.equals(paramBluetoothGattCharacteristic.getUuid()));
//        BluetoothGattDescriptor localBluetoothGattDescriptor1 = paramBluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//        localBluetoothGattDescriptor1.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        this.myBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor1);
//    }
//
//    public void setView() {
//        if (this.deviceAddressList.size() > 0) {
//            this.lv.setVisibility(0);
//            this.tv_hint.setVisibility(8);
//            this.pbar.setVisibility(8);
//            ArrayAdapter localArrayAdapter = new ArrayAdapter(this, 17367055, this.deviceAddressList);
//            this.lv.setChoiceMode(1);
//            this.lv.setAdapter(localArrayAdapter);
//        }
//        do
//            return;
//        while (this.deviceAddressList.size() != 0);
//        this.lv.setVisibility(8);
//        this.tv_hint.setVisibility(0);
//        this.tv_hint.setText("device not found!");
//        this.pbar.setVisibility(8);
//    }
//}
