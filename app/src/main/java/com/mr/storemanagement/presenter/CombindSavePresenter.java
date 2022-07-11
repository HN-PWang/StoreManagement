package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/7/11
 * @description:
 */
public class CombindSavePresenter extends SMBasePresenter {

    private String mSiteCode;
    private String mContainerFrom;
    private String mContainerTo;
    private String mItemCode;
    private String mStockInfoId;
    private String mMoveQty;
    private String mUserCode;
    private String mSN;

    public CombindSavePresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void save(String SiteCode, String ContainerFrom, String ContainerTo, String ItemCode
            , String StockInfoId, String MoveQty, String UserCode, String SN) {
        this.mSiteCode = SiteCode;
        this.mContainerFrom = ContainerFrom;
        this.mContainerTo = ContainerTo;
        this.mItemCode = ItemCode;
        this.mStockInfoId = StockInfoId;
        this.mMoveQty = MoveQty;
        this.mUserCode = UserCode;
        this.mSN = SN;
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.combindSave(mSiteCode, mContainerFrom, mContainerTo, mItemCode, mStockInfoId
                , mMoveQty, mUserCode, mSN);
    }

    @Override
    protected Class getEntityClass() {
        return null;
    }
}
