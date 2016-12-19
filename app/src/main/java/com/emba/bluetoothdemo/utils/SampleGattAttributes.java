package com.emba.bluetoothdemo.utils;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "0000C004-0000-1000-8000-00805f9b34fb";
    public static String MYCJ_BLE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String MYCJ_BLE_READ = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String MYCJ_BLE_WRITE = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.两个服务
        attributes.put("0000fff00000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        
        attributes.put(MYCJ_BLE, "MYCJ_BLE");
        attributes.put(MYCJ_BLE_READ, "READER");
        attributes.put(MYCJ_BLE_WRITE, "WRITER");
        
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}