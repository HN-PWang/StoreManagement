package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/7/11
 * @description:
 */
public class GetCombindFeedBoxPresenter extends SMBasePresenter {

    private String mSiteCode;

    private String mContainerCode;

    private String mUserCode;

    public GetCombindFeedBoxPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getData(String siteCode, String containerCode,String userCode) {
        this.mSiteCode = siteCode;
        this.mContainerCode = containerCode;
        this.mUserCode = userCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getCombindFeedBox(mSiteCode, mContainerCode,mUserCode);
    }

    @Override
    protected Class getEntityClass() {
        return null;
    }
}
