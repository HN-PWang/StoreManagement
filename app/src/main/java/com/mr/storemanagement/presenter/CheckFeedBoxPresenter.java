package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.CheckBoxBackBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/6/21
 * @email: 1929774468@qq.com
 * @description:
 */
public class CheckFeedBoxPresenter extends SMBasePresenter<CheckBoxBackBean> {

    private String mAsnCode;
    private String mUserCode;

    public CheckFeedBoxPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void check(String AsnCode, String UserCode) {
        mAsnCode = AsnCode;
        mUserCode = UserCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.checkFeedBox(mAsnCode, mUserCode);
    }

    @Override
    protected Class<CheckBoxBackBean> getEntityClass() {
        return CheckBoxBackBean.class;
    }

}
