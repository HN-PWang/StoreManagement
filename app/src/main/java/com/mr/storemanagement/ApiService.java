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

    @GET("api/Mobile/GetLogin/{UserCode}/{Pwd}")
    Observable<ResponseBody> login(@HeaderMap Map<String, String> header
            , @Path("UserCode") String UserCode, @Path("Pwd") String Pwd);

    @GET("api/Mobile/GetInboundSiteList")
    Observable<ResponseBody> getSiteList(@HeaderMap Map<String, String> header);

    @GET("api/Mobile/GetAsnList")
    Observable<ResponseBody> getAsnList(@HeaderMap Map<String, String> header);

    @GET("api/Mobile/GetAsnCheck/{asnCode}")
    Observable<ResponseBody> getAsnCheck(@HeaderMap Map<String, String> header
            , @Path("asnCode") String asnCode);

    @GET("api/AllocateLocation/AllocateLocationByContainerCode/{container_code}/{usercode}")
    Observable<ResponseBody> allocateLocation(@HeaderMap Map<String, String> header
            , @Path("container_code") String container_code, @Path("usercode") String usercode);

    @GET("api/Mobile/SetLocationBindRfid/{LocationCode}/{Rfid}/{UserCode}")
    Observable<ResponseBody> bindLocation(@HeaderMap Map<String, String> header
            , @Path("LocationCode") String LocationCode, @Path("Rfid") String Rfid
            , @Path("UserCode") String UserCode);

}
