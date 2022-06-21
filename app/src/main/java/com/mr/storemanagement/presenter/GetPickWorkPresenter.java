package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.GetTaskBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/6/21
 * @email: 1929774468@qq.com
 * @description: 领取任务
 */
public class GetPickWorkPresenter extends SMBasePresenter<GetTaskBean> {

    private String mSiteCode;

    public GetPickWorkPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getPickWork(String SiteCode) {
        mSiteCode = SiteCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getPickWorkList(mSiteCode);
    }

    @Override
    protected Class<GetTaskBean> getEntityClass() {
        return GetTaskBean.class;
    }
}
