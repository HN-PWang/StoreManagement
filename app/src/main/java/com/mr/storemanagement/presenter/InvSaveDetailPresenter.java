package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import org.json.JSONArray;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/6/19
 * @description: 盘点单号保存
 */
public class InvSaveDetailPresenter extends SMBasePresenter<String> {

    private String mInvCode;
    private String mContainerCode;
    private String mUserCode;
    private String mCheckQty;
    private String mDetailID;

    public List<String> mItems;

    private RequestBody mRequestBody;

    public InvSaveDetailPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void save(String InvCode, String ContainerCode, String UserCode, String CheckQty
            , String DetailId, List<String> list) {
        mInvCode = InvCode;
        mContainerCode = ContainerCode;
        mUserCode = UserCode;
        mCheckQty = CheckQty;
        mDetailID = DetailId;
        mItems = list;

        JSONArray array = new JSONArray();
        if (mItems != null) {
            for (String item : mItems) {
//                JSONObject ob = new JSONObject();
//                ob.put("SN", item.SN);
//                ob.put("keyid", item.keyid);
                array.put(item);
            }
        }

        mRequestBody = RequestBody.create(MediaType.parse("application/json"), array.toString());

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.saveInvDetail(mInvCode, mUserCode, mDetailID, mContainerCode, mCheckQty
                , mRequestBody);
    }

    @Override
    protected Class<String> getEntityClass() {
        return null;
    }
}
