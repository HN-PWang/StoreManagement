package com.mr.storemanagement.util;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 判断是否为空的工具类
 * Created by danchao on 2019/11/12.
 */
public class NullUtils {

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断Map是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断List是否为空
     */
    public static boolean isEmpty(List<Object> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断List是否不为空
     */
    public static boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

    /**
     * 判断int数值的字符型值为0或者空
     */
    public static boolean isIntEmpty(String str) {
        boolean isIntEmpty = false;
        if (TextUtils.isEmpty(str)) {
            isIntEmpty = true;
            return isIntEmpty;
        }
        try {
            int value = Integer.parseInt(str);
            if (value == 0) {
                isIntEmpty = true;
            } else {
                isIntEmpty = false;
            }
        } catch (Exception e) {
            isIntEmpty = true;
        } finally {
            return isIntEmpty;
        }
    }

    /**
     * 将空的str对象替换成空字符串""
     */
    public static String replaceNull(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * 判断对象的所有属性是否为空
     */
    public static boolean isAllFieldNull(Object obj) {
        if (obj == null) {
            return true;
        }
        Class stuCla = (Class) obj.getClass();
        Field[] fs = stuCla.getDeclaredFields();
        boolean flag = true;
        for (Field f : fs) {
            f.setAccessible(true);
            Object val = null;
            try {
                val = f.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (val != null) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
