package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.OrderBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class GetAsnPresenter extends SMBasePresenter<OrderBean> {

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
    protected Class<OrderBean> getEntityClass() {
        return OrderBean.class;
    }
}
