package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.InvCheckBackBean;
import com.mr.storemanagement.bean.StoreInfoBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class GetInvCheckPresenter extends SMBasePresenter<InvCheckBackBean> {

    private String mAsnCode;
    private String mUserCode;

    public GetInvCheckPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void check(String asnCode, String userCode) {
        mAsnCode = asnCode;
        mUserCode = userCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getInvCheck(mAsnCode, mUserCode);
    }

    @Override
    protected Class<InvCheckBackBean> getEntityClass() {
        return InvCheckBackBean.class;
    }
}
