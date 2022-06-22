package com.mr.storemanagement.util;

import android.text.TextUtils;

import com.mr.lib_base.util.SMLog;

public class DataUtil {

    public static String getIntStr(String str) {
        if (TextUtils.isEmpty(str))
            return "0";
        return str;
    }

    public static int getInt(String str) {
        int value = 0;
        if (TextUtils.isEmpty(str))
            return value;

        try {
            value = Integer.parseInt(str);
        } catch (Exception e) {
            SMLog.i(e.toString());
        }
        return value;
    }

}
