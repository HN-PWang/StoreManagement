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
public class GetFeedBoxPresenter extends SMBasePresenter<String> {

    private String mSiteCode;
    private String mAsnCode;
    private String mItemCode;
    private String mUserCode;

    public GetFeedBoxPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void getFeedBox(String SiteCode, String AsnCode, String ItemCode, String UserCode) {
        mSiteCode = SiteCode;
        mAsnCode = AsnCode;
        mItemCode = ItemCode;
        mUserCode = UserCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.getFeedBox(mSiteCode, mAsnCode, mItemCode, mUserCode);
    }

    @Override
    protected Class<String> getEntityClass() {
        return String.class;
    }
}
