package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.SiteBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class GetSiteByInvPresenter extends SMBasePresenter<SiteBean> {

    public GetSiteByInvPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getSite() {

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getSiteListByInv();
    }

    @Override
    protected Class<SiteBean> getEntityClass() {
        return SiteBean.class;
    }
}
