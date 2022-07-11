package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.CombindCheckBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/7/11
 * @description:
 */
public class CombindCheckItemPresenter extends SMBasePresenter<CombindCheckBean> {

    private String mItemId;

    private String mContainerCode;

    public CombindCheckItemPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getData(String itemId, String containerCode) {
        this.mItemId = itemId;
        this.mContainerCode = containerCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.combindCheckItem(mContainerCode, mItemId);
    }

    @Override
    protected Class<CombindCheckBean> getEntityClass() {
        return CombindCheckBean.class;
    }
}
