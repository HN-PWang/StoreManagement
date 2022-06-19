package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class BindLocationPresenter extends SMBasePresenter<String> {

    private String mLocationCode;

    private String mRfid;

    private String mUserCode;

    public BindLocationPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void bind(String locationCode, String rfid, String userCode) {
        mLocationCode = locationCode;
        mRfid = rfid;
        mUserCode = userCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.bindLocation(mLocationCode, mRfid, mUserCode);
    }

    @Override
    protected Class<String> getEntityClass() {
        return String.class;
    }
}
