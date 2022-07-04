package com.mr.storemanagement.presenter;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.AsnSaveBackBean;
import com.mr.storemanagement.bean.StoreInfoBean;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/6/19
 * @description:
 */
public class AsnSaveDetailPresenter extends SMBasePresenter<AsnSaveBackBean> {

    private String mAsnCode;
    private String mContainerCode;
    private String mUserCode;
    private String mQty;

    public List<StoreInfoBean.SenNum> mItems;

    private RequestBody mRequestBody;

    public AsnSaveDetailPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void save(String AsnCode, String ContainerCode, String UserCode, String Qty
            , List<StoreInfoBean.SenNum> list) {
        mAsnCode = AsnCode;
        mContainerCode = ContainerCode;
        mUserCode = UserCode;
        mQty = Qty;
        mItems = list;

        JSONArray array = new JSONArray();
        if (mItems != null) {
            for (StoreInfoBean.SenNum item : mItems) {
                JSONObject ob = new JSONObject();
                ob.put("SN", item.SN);
                ob.put("keyid", item.keyid);
                array.put(ob);
            }
        }

        mRequestBody = RequestBody.create(MediaType.parse("application/json"), array.toString());

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.asnSaveDetail(mAsnCode, mContainerCode, mUserCode, mQty, mRequestBody);
    }

    @Override
    protected Class<AsnSaveBackBean> getEntityClass() {
        return AsnSaveBackBean.class;
    }
}
