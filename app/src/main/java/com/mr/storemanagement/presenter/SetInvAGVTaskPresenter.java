package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.InvCheckBackBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * 自动盘点
 */
public class SetInvAGVTaskPresenter extends SMBasePresenter<String> {

    private String mAsnCode;
    private String mSiteCode;
    private String mUserCode;

    public SetInvAGVTaskPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void set(String asnCode, String siteCode, String userCode) {
        mAsnCode = asnCode;
        mSiteCode = siteCode;
        mUserCode = userCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.setInvAgvTask(mAsnCode, mSiteCode, mUserCode);
    }

    @Override
    protected Class<String> getEntityClass() {
        return null;
    }
}
