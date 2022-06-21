package com.mr.storemanagement.presenter;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class OutStockConfirmPresenter extends SMBasePresenter<String> {

    private String mSiteCode;
    private String mUserCode;

    private RequestBody mRequestBody;

    public OutStockConfirmPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void save(String siteCode, String userCode, JSONObject data) {
        mSiteCode = siteCode;

        mUserCode = userCode;

        mRequestBody = RequestBody.create(MediaType.parse("application/json"), data.toString());

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.checkConfirm(mSiteCode, mUserCode, mRequestBody);
    }

    @Override
    protected Class<String> getEntityClass() {
        return null;
    }

}
