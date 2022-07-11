package com.mr.storemanagement;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
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
     * 获取站点信息
     */
    @GET("api/Mobile/GetOutboundSiteList")
    Observable<ResponseBody> getOutSiteList(@HeaderMap Map<String, String> header);

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
     * 回库扫描
     */
    @GET("api/Mobile/SetContainerBackToLocation/{ContainerCode}/{UserCode}")
    Observable<ResponseBody> setContainerBackToLocation(@HeaderMap Map<String, String> header
            , @Path("ContainerCode") String ContainerCode, @Path("UserCode") String UserCode);

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
    @GET("api/Mobile/GetStockInfo/{value}")
    Observable<ResponseBody> getInventoryList(@HeaderMap Map<String, String> header
            , @Path("value") String value);

    /**
     * 获取料箱号信息
     */
    @GET("api/Mobile/GetFeedBox/{SiteCode}/{AsnCode}/{ItemCode}/{UserCode}")
    Observable<ResponseBody> getFeedBox(@HeaderMap Map<String, String> header
            , @Path("SiteCode") String SiteCode, @Path("AsnCode") String AsnCode
            , @Path("ItemCode") String ItemCode, @Path("UserCode") String UserCode);

    /**
     * 获取Asn明细接口
     */
    @GET("api/Mobile/GetAsnDetail/{AsnCode}")
    Observable<ResponseBody> getAsnDetail(@HeaderMap Map<String, String> header
            , @Path("AsnCode") String AsnCode);

    /**
     * 获取已扫SN列表接口
     */
    @GET("api/Mobile/GetAsnDetailSnList/{AsnCode}/{KeyId}")
    Observable<ResponseBody> getAsnDetailSnList(@HeaderMap Map<String, String> header
            , @Path("AsnCode") String AsnCode, @Path("KeyId") String KeyId);

    /**
     * 强制结束时要先调用保存当前记录接口
     */
    @GET("api/Mobile/AsnCloseOrder/{AsnCode}/{UserCode}")
    Observable<ResponseBody> asnCloseOrder(@HeaderMap Map<String, String> header
            , @Path("AsnCode") String AsnCode, @Path("UserCode") String UserCode);

    /**
     * 保存数据并关闭当前容器。如果所有货都收完则会结束订单。 保存数据接口
     */
    @POST("api/Mobile/AsnSaveDetail2/{AsnCode}/{ContainerCode}/{UserCode}/{Qty}")
    Observable<ResponseBody> asnSaveDetail(@HeaderMap Map<String, String> header
            , @Path("AsnCode") String AsnCode, @Path("ContainerCode") String ContainerCode
            , @Path("UserCode") String UserCode, @Path("Qty") String Qty, @Body RequestBody body);

    /**
     * 领取任务列表
     */
    @GET("api/Mobile/GetPickWorkList/{SiteCode}")
    Observable<ResponseBody> getPickWorkList(@HeaderMap Map<String, String> header
            , @Path("SiteCode") String SiteCode);

    /**
     * 领取任务列表
     */
    @GET("api/Mobile/GetTask/{SiteCode}/{UserCode}")
    Observable<ResponseBody> getTaskList(@HeaderMap Map<String, String> header
            , @Path("SiteCode") String SiteCode, @Path("UserCode") String UserCode);

    /**
     * 校验料箱容器
     */
    @GET("api/Mobile/GetArriveContainerItemList/{siteCode}/{ContainerCode}")
    Observable<ResponseBody> getArriveContainerItemList(@HeaderMap Map<String, String> header
            , @Path("siteCode") String siteCode, @Path("ContainerCode") String ContainerCode);

    /**
     * 拣货确认,提交数据到后台保存
     */
    @POST("api/Mobile/CheckConfirmByContainerAndItem/{site_code}/{usercode}")
    Observable<ResponseBody> checkConfirm(@HeaderMap Map<String, String> header
            , @Path("site_code") String siteCode, @Path("usercode") String usercode
            , @Body RequestBody body);

    /**
     * 获取盘点站点信息
     */
    @GET("api/Mobile/GetInvSiteList")
    Observable<ResponseBody> getSiteListByInv(@HeaderMap Map<String, String> header);

    /**
     * 获取单号信息
     */
    @GET("api/Mobile/GetInvList")
    Observable<ResponseBody> getInvList(@HeaderMap Map<String, String> header);

    /**
     * 校验盘点单号
     */
    @GET("api/Mobile/GetInvCheck/{InvCode}/{UserCode}")
    Observable<ResponseBody> getInvCheck(@HeaderMap Map<String, String> header
            , @Path("InvCode") String InvCode, @Path("UserCode") String UserCode);

    /**
     * 自动盘点
     */
    @GET("api/Mobile/GetInvSetAgvTask/{SiteCode}/{InvCode}/{UserCode}")
    Observable<ResponseBody> getInvSetAgvTask(@HeaderMap Map<String, String> header
            , @Path("SiteCode") String SiteCode, @Path("InvCode") String InvCode
            , @Path("UserCode") String UserCode);

    /**
     * 非自动盘点
     */
    @GET("api/Mobile/GetInvSetNonAgvTask/{SiteCode}/{InvCode}/{UserCode}")
    Observable<ResponseBody> getInvSetNonAgvTask(@HeaderMap Map<String, String> header
            , @Path("SiteCode") String SiteCode, @Path("InvCode") String InvCode
            , @Path("UserCode") String UserCode);

    /**
     * 获取盘点单号详情
     */
    @GET("api/Mobile/GetinvDetails/{InvCode}")
    Observable<ResponseBody> getInvDetails(@HeaderMap Map<String, String> header
            , @Path("InvCode") String InvCode);

    /**
     * 强制盘点单号完成
     */
    @GET("api/Mobile/SetInvComplete/{InvCode}/{UserCode}")
    Observable<ResponseBody> setInvComplete(@HeaderMap Map<String, String> header
            , @Path("InvCode") String InvCode, @Path("UserCode") String UserCode);

    /**
     * 保存盘点单号
     */
    @POST("api/Mobile/SaveInvDetail/{InvCode}/{UserCode}/{DetailID}/{ContainerCode}/{CheckQty}")
    Observable<ResponseBody> saveInvDetail(@HeaderMap Map<String, String> header
            , @Path("InvCode") String InvCode, @Path("UserCode") String UserCode
            , @Path("DetailID") String DetailID, @Path("ContainerCode") String ContainerCode
            , @Path("CheckQty") String CheckQty, @Body RequestBody body);

    /**
     * 获取绑定的料箱信息
     */
    @GET("api/Mobile/GetComBindFeedBox/{ContainerCode}/{SiteCode}/{UserCode}")
    Observable<ResponseBody> getCombindFeedBox(@HeaderMap Map<String, String> header
            , @Path("SiteCode") String SiteCode, @Path("ContainerCode") String ContainerCode
            , @Path("UserCode") String UserCode);

    /**
     * 校验商品册序号信息
     */
    @GET("api/Mobile/CombindCheckItem/{ContainerCode}/{ItemId}")
    Observable<ResponseBody> combindCheckItem(@HeaderMap Map<String, String> header
            , @Path("ContainerCode") String ContainerCode, @Path("ItemId") String ItemId);

    /**
     * 保存合并信息
     */
    @GET("api/Mobile/CombindSave/{SiteCode}/{ContainerFrom}/{ContainerTo}/{ItemCode}/{StockInfoId}/{MoveQty}/{UserCode}/{SN}")
    Observable<ResponseBody> combindSave(@HeaderMap Map<String, String> header
            , @Path("SiteCode") String SiteCode, @Path("ContainerFrom") String ContainerFrom
            , @Path("ContainerTo") String ContainerTo, @Path("ItemCode") String ItemCode
            , @Path("StockInfoId") String StockInfoId, @Path("MoveQty") String MoveQty
            , @Path("UserCode") String UserCode, @Path("SN") String SN);
}
