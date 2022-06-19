package com.mr.storemanagement;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    /**
     * 登陆
     */
    @GET("api/Mobile/GetLogin/{UserCode}/{Pwd}")
    Observable<ResponseBody> login(@HeaderMap Map<String, String> header
            , @Path("UserCode") String UserCode, @Path("Pwd") String Pwd);

    /**
     * 获取站点信息
     */
    @GET("api/Mobile/GetInboundSiteList")
    Observable<ResponseBody> getSiteList(@HeaderMap Map<String, String> header);

    /**
     * 获取单号信息
     */
    @GET("api/Mobile/GetAsnList")
    Observable<ResponseBody> getAsnList(@HeaderMap Map<String, String> header);

    /**
     * 校验单号,并获取集合
     */
    @GET("api/Mobile/GetAsnCheck/{asnCode}")
    Observable<ResponseBody> getAsnCheck(@HeaderMap Map<String, String> header
            , @Path("asnCode") String asnCode);

    /**
     * 库位
     */
    @GET("api/AllocateLocation/AllocateLocationByContainerCode/{container_code}/{usercode}")
    Observable<ResponseBody> allocateLocation(@HeaderMap Map<String, String> header
            , @Path("container_code") String container_code, @Path("usercode") String usercode);

    /**
     * 绑定库位
     */
    @GET("api/Mobile/SetLocationBindRfid/{LocationCode}/{Rfid}/{UserCode}")
    Observable<ResponseBody> bindLocation(@HeaderMap Map<String, String> header
            , @Path("LocationCode") String LocationCode, @Path("Rfid") String Rfid
            , @Path("UserCode") String UserCode);

    /**
     * 库存查询
     */
    @GET("api/Mobile/GetInventoryList/{asnCode}")
    Observable<ResponseBody> getInventoryList(@HeaderMap Map<String, String> header
            , @Path("asnCode") String asnCode);

}
