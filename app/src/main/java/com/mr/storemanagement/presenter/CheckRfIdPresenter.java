package com.mr.storemanagement.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.util.NullUtils;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/7/11
 * @description:
 */
public class CheckRfIdPresenter extends SMBasePresenter<String> {

    private RequestBody mRequestBody;

    public CheckRfIdPresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void check(String RfIdType, List<String> RfIdList) {

        JSONObject object = new JSONObject();

        if (!TextUtils.isEmpty(RfIdType))
            object.put("RfIdType", RfIdType);

        if (NullUtils.isNotEmpty(RfIdList))
            object.put("RfIdList", RfIdList);

        mRequestBody = RequestBody.create(MediaType.parse("application/json"), object.toJSONString());

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.checkRfId(mRequestBody);
    }

    @Override
    protected Class<String> getEntityClass() {
        return String.class;
    }
}
