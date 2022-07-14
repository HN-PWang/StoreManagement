package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * 回库扫描
 */
public class SetContainerBackPresenter extends SMBasePresenter<String> {

    private String mContainerCode;

    private String mUserCode;

    private String mSiteCode;

    public SetContainerBackPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void allocate(String containerCode, String userCode,String siteCode) {
        mContainerCode = containerCode;
        mUserCode = userCode;
        mSiteCode = userCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.setContainerBackToLocation(mContainerCode, mUserCode, mSiteCode);
    }

    @Override
    protected Class<String> getEntityClass() {
        return null;
    }
}
