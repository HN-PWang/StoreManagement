package com.mr.storemanagement;

import com.mr.lib_base.network.BaseModel;
import com.mr.lib_base.network.RetrofitManager;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class AppNetModel extends BaseModel {

    /**
     * 获取取消订单理由
     */
    public Observable<ResponseBody> getUserInfo(String name) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.login(getHeader(),name);
    }


}
