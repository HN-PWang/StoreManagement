package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class AsnSaveDetailPresenter extends SMBasePresenter<String> {

    private String mAsnCode;
    private String mContainerCode;
    private String mUserCode;
    private String mKeyId;
    private String mQty;

    public List<String> mItems;

    private RequestBody mRequestBody;

    public AsnSaveDetailPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void save(String AsnCode, String ContainerCode, String UserCode, String KeyId, String Qty
            , List<String> list) {
        mAsnCode = AsnCode;
        mContainerCode = ContainerCode;
        mUserCode = UserCode;
        mKeyId = KeyId;
        mQty = Qty;
        mItems = list;

        JSONArray array = new JSONArray();
        if (mItems != null) {
            for (String item : mItems) {
                array.put(item);
            }
        }

        mRequestBody = RequestBody.create(MediaType.parse("application/json"), array.toString());

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.asnSaveDetail(mAsnCode, mContainerCode, mUserCode, mKeyId, mQty, mRequestBody);
    }

    @Override
    protected Class<String> getEntityClass() {
        return String.class;
    }
}
