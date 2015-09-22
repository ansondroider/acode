package com.anson.acode.aos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.http.util.ByteArrayBuffer;

import com.anson.acode.ALog;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class DeviceInfo {
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public static String getCPUSerial() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			// 读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			// 查找CPU序列号
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					// 查找到序列号所在行
					if (str.indexOf("Serial") > -1) {
						// 提取序列号
						strCPU = str.substring(str.indexOf(":") + 1,
								str.length());
						// 去空格
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					// 文件结尾
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		ALog.alog("Tool", "result ********************" + cpuAddress);
		return cpuAddress;
	}

	public static String getCpuInfo() {
		String cpuadd = "000000000000000000";
		ByteArrayBuffer buff = null;
		byte[] cache = new byte[1024];
		try {
			FileInputStream fis = new FileInputStream(new File("/proc/cpuinfo"));
			buff = new ByteArrayBuffer(fis.available());
			int readed = 0;
			while ((readed = fis.read(cache)) > 0) {
				buff.append(cache, 0, readed);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			byte[] result = buff.toByteArray();
			cpuadd = new String(result);
		}
		ALog.alog("Tool", cpuadd);
		return cpuadd;
	}
}
