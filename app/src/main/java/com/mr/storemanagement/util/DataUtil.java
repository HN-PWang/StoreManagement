package com.mr.storemanagement.util;

import android.text.TextUtils;

import com.mr.lib_base.util.SMLog;

import java.math.BigDecimal;

public class DataUtil {

    public static String getIntStr(String str) {
        if (TextUtils.isEmpty(str))
            return "0";
        return str;
    }

    public static String getIntStr(BigDecimal dec) {
        if (dec == null)
            return "0";
        return dec.toString();
    }

    public static String getIntStr(Double dec) {
        if (dec == null)
            return "0";
        return String.valueOf(dec);
    }

    public static String getIntStr(Integer str) {
        if (str == null)
            return "0";
        return String.valueOf(str);
    }

    public static int getInt(String str) {
        int value = 0;
        if (TextUtils.isEmpty(str))
            return value;

        try {
            double v = Double.parseDouble(str);
            value = (int) v;
        } catch (Exception e) {
            SMLog.i(e.toString());
        }
        return value;
    }

    public static int getInt(Integer v) {
        int value = 0;
        if (v == null)
            return value;

        return v;
    }

}
