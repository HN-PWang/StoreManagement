package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.InvDetailsBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/7/6
 * @email: 1929774468@qq.com
 * @description:
 */
public class GetInvDetailsPresenter extends SMBasePresenter<InvDetailsBean> {

    private String mInvCode;

    public GetInvDetailsPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void get(String invCode) {
        this.mInvCode = invCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getInvDetails(mInvCode);
    }

    @Override
    protected Class<InvDetailsBean> getEntityClass() {
        return InvDetailsBean.class;
    }

}
