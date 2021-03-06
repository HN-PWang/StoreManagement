package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.UserInfoBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class LoginPresenter extends SMBasePresenter<UserInfoBean> {

    private String mUser;

    private String mPwd;

    public LoginPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void login(String name, String pwd) {
        this.mUser = name;
        this.mPwd = pwd;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getUserInfo(mUser, mPwd);
    }

    @Override
    protected Class<UserInfoBean> getEntityClass() {
        return UserInfoBean.class;
    }
}
