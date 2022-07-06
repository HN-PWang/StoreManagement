package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * 非自动盘点
 */
public class GetInvSetNonAgvTaskPresenter extends SMBasePresenter<String> {

    private String mAsnCode;
    private String mSiteCode;
    private String mUserCode;

    public GetInvSetNonAgvTaskPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void get(String asnCode, String siteCode, String userCode) {
        mAsnCode = asnCode;
        mSiteCode = siteCode;
        mUserCode = userCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getInvSetNonAgvTask(mAsnCode, mSiteCode, mUserCode);
    }

    @Override
    protected Class<String> getEntityClass() {
        return null;
    }
}
