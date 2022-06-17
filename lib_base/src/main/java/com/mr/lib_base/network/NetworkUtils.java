package com.mr.lib_base.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 网络相关工具类
 *
 * @author zhangbh
 * @date 2018/10/31
 */

public class NetworkUtils {

    //允许获取Mac地址标记
    public volatile static boolean ALLOW_ACCESS_MAC_ADDRESS = false;

    /**
     * 判断是否已连接网络
     */
    public static boolean checkNetwork(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected() && info.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前网络状态
     * 0-无网路连接
     * 1-wifi
     * 2-其他网络
     */
    public static int getNetworkState(Context context) {
        int result = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected() && info.isAvailable()) {
                result = info.getType() == ConnectivityManager.TYPE_WIFI ? 1 : 2;
            }
        }
        return result;
    }

    /**
     * 获取Wifi IP地址
     */
    public static String getWifiIpAddress(Context context) {
        String ipAddress = "";
        if (!ALLOW_ACCESS_MAC_ADDRESS)
            return ipAddress;

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                ipAddress = intIP2StringIP(wifiInfo.getIpAddress()); // 得到IPV4地址
            }
        }
        return ipAddress;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

}
