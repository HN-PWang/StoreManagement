package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/6/19
 * @description: 强制关闭收货
 */
public class AsnCloseOrderPresenter extends SMBasePresenter<String> {

    private String mAsnCode;
    private String mUserCode;

    public AsnCloseOrderPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void close(String AsnCode, String UserCode) {
        mAsnCode = AsnCode;
        mUserCode = UserCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.asnCloseOrder(mAsnCode, mUserCode);
    }

    @Override
    protected Class<String> getEntityClass() {
        return String.class;
    }
}
