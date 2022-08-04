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
public class BindRfIdSavePresenter extends SMBasePresenter {

    private RequestBody mRequestBody;

    public BindRfIdSavePresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void bind(String Rfid, String Value, String ValueType, String UserCode) {

        JSONObject object = new JSONObject();

        if (!TextUtils.isEmpty(Rfid))
            object.put("Rfid", Rfid);

        if (!TextUtils.isEmpty(Value))
            object.put("Value", Value);

        if (!TextUtils.isEmpty(ValueType))
            object.put("ValueType", ValueType);

        if (!TextUtils.isEmpty(UserCode))
            object.put("UserCode", UserCode);

        mRequestBody = RequestBody.create(MediaType.parse("application/json"), object.toJSONString());

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.saveRfIdBind(mRequestBody);
    }

    @Override
    protected Class getEntityClass() {
        return null;
    }
}
