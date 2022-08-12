package com.mr.storemanagement.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
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
public class BindRfIdSavePresenter extends SMBasePresenter<String> {

    private RequestBody mRequestBody;

    public BindRfIdSavePresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void bind(List<String> rfIds, String Value, String ValueType, String UserCode) {

        JSONObject object = new JSONObject();

        if (NullUtils.isNotEmpty(rfIds))
            object.put("Rfid", JSONArray.toJSONString(rfIds));

        if (!TextUtils.isEmpty(Value))
            object.put("Value", Value);

        if (!TextUtils.isEmpty(ValueType))
            object.put("ValueType", ValueType);

        if (!TextUtils.isEmpty(UserCode))
            object.put("UserCode", UserCode);

        String requestStr = object.toJSONString();

        mRequestBody = RequestBody.create(MediaType.parse("application/json"), requestStr);

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.saveRfIdBind(mRequestBody);
    }

    @Override
    protected Class<String> getEntityClass() {
        return null;
    }
}
