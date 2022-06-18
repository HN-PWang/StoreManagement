package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.StoreInfoBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class GetAsnCheckPresenter extends SMBasePresenter<StoreInfoBean> {

    private String mAsnCode;

    public GetAsnCheckPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void check(String asnCode){
        mAsnCode = asnCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getAsnCheck(mAsnCode);
    }

    @Override
    protected Class<StoreInfoBean> getEntityClass() {
        return StoreInfoBean.class;
    }
}
