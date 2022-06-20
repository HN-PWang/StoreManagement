package com.mr.storemanagement.util;

import android.text.TextUtils;

public class DataUtil {

    public static String getIntStr(String str) {
        if (TextUtils.isEmpty(str))
            return "0";
        return str;
    }

}
