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
 * @description:
 */
public class GetAsnDetailPresenter extends SMBasePresenter<String> {

    private String mAsnCode;

    public GetAsnDetailPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getData(String AsnCode) {
        mAsnCode = AsnCode;
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getAsnDetail(mAsnCode);
    }

    @Override
    protected Class<String> getEntityClass() {
        return String.class;
    }
}
