package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/8/4
 * @description:
 */
public class GetInvFeedBoxPresenter extends SMBasePresenter {

    private String mInvCode;
    private String mSiteCode;
    private String mContainerCode;
    private String mUserCode;

    public GetInvFeedBoxPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getContainerCode(String InvCode, String SiteCode, String ContainerCode
            , String UserCode) {
        this.mInvCode = InvCode;
        this.mSiteCode = SiteCode;
        this.mContainerCode = ContainerCode;
        this.mUserCode = UserCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getInvFeedBox(mInvCode, mSiteCode, mContainerCode, mUserCode);
    }

    @Override
    protected Class getEntityClass() {
        return null;
    }
}
