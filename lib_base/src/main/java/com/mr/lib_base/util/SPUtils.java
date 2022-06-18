package com.mr.lib_base.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mr.lib_base.BaseApplication;

import java.lang.reflect.Field;

/**
 * SP文件操作工具类
 */
public class SPUtils {

    public static final String ACCOUNT_SP_END_NAME = "_account_sp"; // 用来保存账号相关的

    public static final String OTHER_SP_END_NAME = "_other_sp"; // 用来保存其他的SP

    public static final String SP_DEF_STR_VALUE = ""; //string默认值

    public static final int SP_DEF_INT_VALUE = 0; //int默认值

    public static SharedPreferences getAccountSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName() + ACCOUNT_SP_END_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getOtherSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName() + OTHER_SP_END_NAME, Context.MODE_PRIVATE);
    }

    /**
     * SP_ACCOUNT存储对象，所有账号相关的存储均以customerId.属性名称作为SP中的键
     * 例如，customerId是5，存储的是Accout类，Account类里有name，customerId等
     * 那么，SP的键是5.name或者5.customerId
     */
    public static void saveObjectWithAccount(Object object, String userId) throws IllegalArgumentException, IllegalAccessException {
        SharedPreferences sp = getAccountSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            saveField(field, userId, sp, object);
        }
    }

    /**
     * SP存储属性值
     */
    private static void saveField(Field field, String userId, SharedPreferences sp, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        String tag = userId + ".";
        field.setAccessible(true);
        Class fildType = field.getType();
        if (String.class == fildType || Character.class == fildType) {
            String value = String.valueOf(field.get(object));
            sp.edit().putString(tag + field.getName(), value).apply();
        } else if (Integer.TYPE == fildType || Integer.class == fildType) {
            int value = field.getInt(object);
            sp.edit().putInt(tag + field.getName(), value).apply();
        } else if (Boolean.TYPE == fildType || Boolean.class == fildType) {
            sp.edit().putBoolean(tag + field.getName(), field.getBoolean(object)).apply();
        } else if (Long.class == fildType || Long.TYPE == fildType) {
            long value = field.getLong(object);
            sp.edit().putLong(tag + field.getName(), value).apply();
        } else if (Float.class == fildType || Float.TYPE == fildType) {
            sp.edit().putFloat(tag + field.getName(), field.getFloat(object)).apply();
        }
    }

    /**
     * 获得SP_ACCOUNT存储对象
     */
    public static Object getObjectWithAccount(Class<?> classzz, String userId) throws InstantiationException,
            IllegalAccessException, ClassCastException {
        Object object = classzz.newInstance();
        SharedPreferences sp = getAccountSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            setValueToFiled(field, object, getField(field, userId, sp));
        }
        return object;
    }

    /**
     * 从SP中获取值
     */
    private static Object getField(Field field, String userId, SharedPreferences sp) throws ClassCastException {
        String tag = userId + ".";
        field.setAccessible(true);
        Class fildType = field.getType();
        if (String.class == fildType || Character.class == fildType) {
            return sp.getString(tag + field.getName(), SP_DEF_STR_VALUE);
        } else if (Integer.TYPE == fildType || Integer.class == fildType) {
            return String.valueOf(sp.getInt(tag + field.getName(), 0));
        } else if (Boolean.class == fildType || Boolean.TYPE == fildType) {
            return String.valueOf(sp.getBoolean(tag + field.getName(), false));
        } else if (Long.TYPE == fildType || Long.class == fildType) {
            return String.valueOf(sp.getLong(tag + field.getName(), 0L));
        } else if (Float.TYPE == fildType || Float.class == fildType) {
            return String.valueOf(sp.getFloat(tag + field.getName(), 0F));
        }
        return SP_DEF_STR_VALUE;
    }

    /**
     * 把从SP中获取的值设到Bean中
     */
    private static void setValueToFiled(Field field, Object object, Object fieldValue)
            throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);// 允许插入
        Class fieldType = field.getType();
        if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {// int
            Integer value = Integer.parseInt(fieldValue.toString());
            field.set(object, value);
        } else if (String.class == fieldType) {// string
            field.set(object, fieldValue);
        } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {// long
            Long value = Long.parseLong(fieldValue.toString());
            field.set(object, value);
        } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {// float
            Float value = Float.parseFloat(fieldValue.toString());
            field.set(object, value);
        } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {// short
            Short value = Short.parseShort(fieldValue.toString());
            field.set(object, value);
        } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {// double
            Double value = Double.parseDouble(fieldValue.toString());
            field.set(object, value);
        } else if (Character.TYPE == fieldType || Character.class == fieldType) {// char
            Character value = Character.valueOf(fieldValue.toString().charAt(0));
            field.set(object, value);
        } else if (Boolean.TYPE == fieldType || Boolean.class == fieldType) { // boolean
            Boolean value = Boolean.parseBoolean(fieldValue.toString());
            field.setBoolean(object, value);
        }
    }

    /**
     * 使用SP_ACCOUNT保存字符串
     */
    public static void saveStringWithAccount(String key, String value, String userId) {
        SharedPreferences sp = getAccountSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(userId + "." + key, value);
        editor.apply();
    }

    /**
     * 使用SP_OTHER保存字符串
     *
     * @param key
     * @param value
     */
    public static void saveStringWithOther(String key, String value) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * SP_OTHER获取字符串
     */
    public static String getStringWithOther(String key) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return sp.getString(key, SP_DEF_STR_VALUE);
    }

    public static String getStringWithOther(String key, String defValue) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return sp.getString(key, defValue);
    }

    /**
     * 使用SP_OTHER保存boolean
     *
     * @param key
     * @param value
     */
    public static void saveBooleanWithOther(String key, boolean value) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * SP_OTHER获取boolean
     *
     * @param key
     * @return
     */
    public static boolean getBooleanWithOther(String key) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return sp.getBoolean(key, false);
    }

    /**
     * 使用SP_OTHER保存 int
     *
     * @param key
     * @param value
     */
    public static void saveIntWithOther(String key, int value) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * SP_OTHER获取int
     */
    public static int getIntWithOther(String key) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return sp.getInt(key, SP_DEF_INT_VALUE);
    }

    /**
     * 使用SP_OTHER保存 float
     *
     * @param key
     * @param value
     */
    public static void saveFloatWithOther(String key, float value) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * SP_OTHER获取float
     */
    public static float getFloatWithOther(String key) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return sp.getFloat(key, SP_DEF_INT_VALUE);
    }

    /**
     * 使用SP_OTHER保存 long
     *
     * @param key
     * @param value
     */
    public static void saveLongWithOther(String key, long value) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * SP_OTHER获取long
     */
    public static long getLongWithOther(String key) {
        SharedPreferences sp = getOtherSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return sp.getLong(key, SP_DEF_INT_VALUE);
    }
}
