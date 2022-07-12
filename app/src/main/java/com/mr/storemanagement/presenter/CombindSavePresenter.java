package com.mr.storemanagement.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/7/11
 * @description:
 */
public class CombindSavePresenter extends SMBasePresenter {

    private RequestBody mRequestBody;

    public CombindSavePresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void save(String SiteCode, String ContainerFrom, String ContainerTo, String ItemCode
            , int StockInfoId, int MoveQty, String UserCode, String SN) {

        JSONObject object = new JSONObject();

        if (!TextUtils.isEmpty(SiteCode))
            object.put("SiteCode", SiteCode);

        if (!TextUtils.isEmpty(ContainerFrom))
            object.put("ContainerFrom", ContainerFrom);

        if (!TextUtils.isEmpty(ContainerTo))
            object.put("ContainerTo", ContainerTo);

        if (!TextUtils.isEmpty(ItemCode))
            object.put("ItemCode", ItemCode);

            object.put("StockInfoId", StockInfoId);

            object.put("MoveQty", MoveQty);

        if (!TextUtils.isEmpty(UserCode))
            object.put("UserCode", UserCode);

        if (!TextUtils.isEmpty(SN))
            object.put("SN", SN);

        mRequestBody = RequestBody.create(MediaType.parse("application/json"), object.toJSONString());

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.combindSave(mRequestBody);
    }

    @Override
    protected Class getEntityClass() {
        return null;
    }
}
