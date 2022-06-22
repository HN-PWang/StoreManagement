package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.StackBean;
import com.mr.storemanagement.bean.StoreInfoBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * 获取库存信息
 */
public class GetInventoryListPresenter extends SMBasePresenter<StackBean> {

    private String mCode;

    public GetInventoryListPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getData(String locationCode) {
        mCode = locationCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getInventoryList(mCode);
    }

    @Override
    protected Class<StackBean> getEntityClass() {
        return StackBean.class;
    }
}
