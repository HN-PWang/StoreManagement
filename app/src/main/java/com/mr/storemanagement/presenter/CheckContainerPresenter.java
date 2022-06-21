package com.mr.storemanagement.presenter;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.base.SMBasePresenter;
import com.mr.storemanagement.bean.ContainerGoodsBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * @auther: pengwang
 * @date: 2022/6/21
 * @email: 1929774468@qq.com
 * @description: 校验容器，校验成功进入扫描商品界面
 */
public class CheckContainerPresenter extends SMBasePresenter<ContainerGoodsBean> {

    private String mAsnCode;
    private String mContainerCode;

    public CheckContainerPresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        super(baseActivity, resultListener, loadingListener);
    }

    public void check(String AsnCode, String ContainerCode) {
        mAsnCode = AsnCode;
        mContainerCode = ContainerCode;

        executeRequest();
    }

    @Override
    protected Observable<ResponseBody> toPerformApi() {
        return netModel.checkContainer(mAsnCode, mContainerCode);
    }

    @Override
    protected Class<ContainerGoodsBean> getEntityClass() {
        return ContainerGoodsBean.class;
    }

}
