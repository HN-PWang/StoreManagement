package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.AsnCodeBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class GetAsnPresenter extends SMBasePresenter<AsnCodeBean> {

    public GetAsnPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getAsn() {

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getAsnList();
    }

    @Override
    protected Class<AsnCodeBean> getEntityClass() {
        return AsnCodeBean.class;
    }
}
