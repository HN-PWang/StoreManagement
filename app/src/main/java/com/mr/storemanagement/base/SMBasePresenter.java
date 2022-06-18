package com.mr.storemanagement.base;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.BasePresenter;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.AppNetModel;

public abstract class SMBasePresenter<T> extends BasePresenter<T> {

    protected AppNetModel netModel;

    public SMBasePresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
        mBaseModel = new AppNetModel();
        netModel = (AppNetModel) mBaseModel;
    }

}
