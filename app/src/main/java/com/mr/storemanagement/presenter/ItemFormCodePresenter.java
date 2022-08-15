package com.mr.storemanagement.presenter;

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
public class ItemFormCodePresenter extends SMBasePresenter<String> {

    private RequestBody mRequestBody;

    public ItemFormCodePresenter(BaseActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void check(String code) {

//        JSONObject object = new JSONObject();


        mRequestBody = RequestBody.create(MediaType.parse("application/json"), code);

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.itemFromQrCode(mRequestBody);
    }

    @Override
    protected Class<String> getEntityClass() {
        return String.class;
    }
}
