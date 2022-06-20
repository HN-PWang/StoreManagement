package com.mr.storemanagement.manger;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.util.SPUtils;
import com.mr.storemanagement.bean.UserInfoBean;


public class AccountManger {

    private static final String ACCOUNT_INFO_KEY = "account_info_key";

    private static volatile AccountManger instance;

    private AccountManger() {

    }

    public static AccountManger getInstance() {
        if (instance == null) {
            synchronized (AccountManger.class) {
                if (instance == null)
                    instance = new AccountManger();
            }
        }
        return instance;
    }


    public void login(UserInfoBean bean) {
        if (bean != null) {
            SPUtils.saveStringWithOther(ACCOUNT_INFO_KEY, JSONObject.toJSONString(bean));
        }
    }

    public void logout() {
        SPUtils.saveStringWithOther(ACCOUNT_INFO_KEY, "");
    }

    public UserInfoBean getAccount() {
        String strAcc = SPUtils.getStringWithOther(ACCOUNT_INFO_KEY, "");

        if (!TextUtils.isEmpty(strAcc)) {
            UserInfoBean bean = JSONObject.parseObject(strAcc, UserInfoBean.class);
            return bean;
        }

        return null;
    }

    public String getUserCode() {
        if (getAccount() != null)
            return getAccount().UserCode;
        return "";
    }

}
