package com.emba.bluetoothdemo.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016-12-13
 * 
 * @desc
 */

public class MessageUtils {
	public static byte[] appSelectBleAllData() {
		byte bytes[] = new byte[6];
		bytes[0] = 0x53; // 开头标志
		bytes[1] = 0x06; // 数据长度
		bytes[2] = 0x01; // 指令
		bytes[3] = 0x00; // 预留位
		bytes[4] = 0x00; // 预留位
		byte sum = 0x00;
		for (byte aByte : bytes) {
			sum += aByte;
		}
		bytes[5] = sum;
		return bytes;
	}
	
	public static byte[] appSetLEDStatus(byte state) {
		byte bytes[] = new byte[6];
		bytes[0] = 0x53; // 开头标志
		bytes[1] = 0x06; // 数据长度
		bytes[2] = 0x02; // 指令
		bytes[3] = state; // LED显示（0，1两种状态 0X00为不显示，0X01显示）
		bytes[4] = 0x00; // 预留位
		byte sum = 0x00; // 校验码
		for (byte aByte : bytes) {
			sum += aByte;
		}
		bytes[5] = sum;
		return bytes;
	}
	
	public static String getByteString(byte bytes[]){
		StringBuilder stringBuilder = new StringBuilder();
		for (byte aByte : bytes) {
			String temp = Integer.toHexString(aByte);
			if (temp.length() == 1) {
				temp = 0 + temp;
			}
			stringBuilder.append(temp);
		}
		Log.i("aijie", "stringBuilder==" + stringBuilder.toString());
		byte results[] = HexUtil.hexStringToBytes(stringBuilder.toString());
		for (byte result : results) {
			String temp = Integer.toHexString(result);
			if (temp.length() == 1) {
				temp = 0 + temp;
			}
			Log.i("aijie", "temp==" + temp);
		}
		return stringBuilder.toString();
	}
	
	public static String bytesToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			String hexString = Integer.toHexString(bytes[i] & 0xFF);
			if (hexString.length() == 1) {
				hexString = '0' + hexString;
			}
			result += hexString.toUpperCase();
		}
		return result;
	}

	public static byte[] getHexBytes(String message) {
		int len = message.length() / 2;
		char[] chars = message.toCharArray();
		String[] hexStr = new String[len];
		byte[] bytes = new byte[len];
		for (int i = 0, j = 0; j < len; i += 2, j++) {
			hexStr[j] = "" + chars[i] + chars[i + 1];
			bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
		}
		return bytes;
	}
}
