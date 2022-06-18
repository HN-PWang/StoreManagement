package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class AllocateLocationPresenter extends SMBasePresenter<String> {

    private String mContainerCode;

    private String mUserCode;

    public AllocateLocationPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void allocate(String containerCode, String userCode) {
        mContainerCode = containerCode;
        mUserCode = userCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.allocateLocation(mContainerCode, mUserCode);
    }

    @Override
    protected Class<String> getEntityClass() {
        return null;
    }
}
