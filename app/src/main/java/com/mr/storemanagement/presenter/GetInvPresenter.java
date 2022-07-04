package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.AsnCodeBean;
import com.mr.storemanagement.bean.InvCodeBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class GetInvPresenter extends SMBasePresenter<InvCodeBean> {

    public GetInvPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getAsn() {

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getInvList();
    }

    @Override
    protected Class<InvCodeBean> getEntityClass() {
        return InvCodeBean.class;
    }
}
