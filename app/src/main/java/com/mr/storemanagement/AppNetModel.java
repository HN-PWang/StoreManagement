package com.mr.storemanagement;

import com.mr.lib_base.network.BaseModel;
import com.mr.lib_base.network.RetrofitManager;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;

public class AppNetModel extends BaseModel {

    /**
     * 登录
     */
    public Observable<ResponseBody> getUserInfo(String name, String pwd) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.login(getHeader(), name, pwd);
    }

    /**
     * 获取站点列表
     */
    public Observable<ResponseBody> getSiteList() {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getSiteList(getHeader());
    }

    /**
     * 获取站点列表
     */
    public Observable<ResponseBody> getOutSiteList() {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getOutSiteList(getHeader());
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

    /**
     * 回库扫描
     */
    public Observable<ResponseBody> setContainerBackToLocation(String containerCode, String userCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.setContainerBackToLocation(getHeader(), containerCode, userCode);
    }

    /**
     * 库位绑定rfid
     */
    public Observable<ResponseBody> bindLocation(String locationCode, String rfid, String userCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.bindLocation(getHeader(), locationCode, rfid, userCode);
    }

    /**
     * 查询库存信息
     */
    public Observable<ResponseBody> getInventoryList(String locationCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getInventoryList(getHeader(), locationCode);
    }

    /**
     * 获取料箱号信息
     */
    public Observable<ResponseBody> getFeedBox(String SiteCode, String AsnCode, String ItemCode
            , String UserCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getFeedBox(getHeader(), SiteCode, AsnCode, ItemCode, UserCode);
    }

    /**
     * 获取Asn明细接口
     */
    public Observable<ResponseBody> getAsnDetail(String asnCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getAsnDetail(getHeader(), asnCode);
    }

    /**
     * 获取已扫SN列表接口
     */
    public Observable<ResponseBody> getAsnDetailSnList(String asnCode, String KeyId) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getAsnDetailSnList(getHeader(), asnCode, KeyId);
    }

    /**
     * 强制结束时要先调用保存当前记录接口
     */
    public Observable<ResponseBody> asnCloseOrder(String asnCode, String UserCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.asnCloseOrder(getHeader(), asnCode, UserCode);
    }

    /**
     * 保存数据并关闭当前容器。如果所有货都收完则会结束订单。 保存数据接口
     */
    public Observable<ResponseBody> asnSaveDetail(String AsnCode, String ContainerCode
            , String UserCode, String Qty, RequestBody body) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.asnSaveDetail(getHeader(), AsnCode, ContainerCode, UserCode, Qty, body);
    }

    /**
     * 强制结束时要先调用保存当前记录接口
     */
    public Observable<ResponseBody> getPickWorkList(String SiteCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getPickWorkList(getHeader(), SiteCode);
    }

    /**
     * 强制结束时要先调用保存当前记录接口
     */
    public Observable<ResponseBody> getTaskList(String SiteCode, String UserCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getTaskList(getHeader(), SiteCode, UserCode);
    }

    /**
     * 强制结束时要先调用保存当前记录接口
     */
    public Observable<ResponseBody> checkContainer(String SiteCode, String ContainerCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getArriveContainerItemList(getHeader(), SiteCode, ContainerCode);
    }

    /**
     * 拣货确认,提交数据到后台保存
     */
    public Observable<ResponseBody> checkConfirm(String SiteCode, String usercode
            , @Body RequestBody body) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.checkConfirm(getHeader(), SiteCode, usercode, body);
    }

    /**
     * 获取站点列表
     */
    public Observable<ResponseBody> getSiteListByInv() {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getSiteListByInv(getHeader());
    }

    /**
     * 获取盘点单号列表
     */
    public Observable<ResponseBody> getInvList() {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getInvList(getHeader());
    }

    /**
     * 获取单号列表
     */
    public Observable<ResponseBody> getInvCheck(String asnCode, String userCode) {
        ApiService service = RetrofitManager.create(ApiService.class);
        return service.getInvCheck(getHeader(), asnCode, userCode);
    }

}
