package com.mr.storemanagement;

import com.mr.lib_base.network.BaseModel;
import com.mr.lib_base.network.RetrofitManager;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class AppNetModel extends BaseModel {

    /**
     * 登录
     */
    public Observable<ResponseBody> getUserInfo(String name) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.login(getHeader(), name);
    }

    /**
     * 获取站点列表
     */
    public Observable<ResponseBody> getSiteList() {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getSiteList(getHeader());
    }

    /**
     * 获取单号列表
     */
    public Observable<ResponseBody> getAsnList() {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getAsnList(getHeader());
    }

    /**
     * 获取单号列表
     */
    public Observable<ResponseBody> getAsnCheck(String asnCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getAsnCheck(getHeader(), asnCode);
    }

    /**
     * 获取单号列表
     */
    public Observable<ResponseBody> allocateLocation(String containerCode, String userCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.allocateLocation(getHeader(), containerCode, userCode);
    }

}
