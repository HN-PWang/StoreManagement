package com.mr.storemanagement.helper;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.presenter.ItemFormCodePresenter;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

/**
 * @auther: pengwang
 * @date: 2022/8/15
 * @description:
 */
public class ItemFormCodeHelper {

    private BaseActivity activity;

    public ItemFormCodeHelper(BaseActivity activity) {
        this.activity = activity;
    }

    public void request(String code, OnItemCodeBackListener backListener) {
        ItemFormCodePresenter presenter = new ItemFormCodePresenter(activity
                , new NetResultListener<String>() {
            @Override
            public void loadSuccess(String s) {
                if (backListener != null)
                    backListener.onBack(s);
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(activity, exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                activity.showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                activity.dismissLoadingDialog();
            }
        });
        presenter.check(code);
    }

    public interface OnItemCodeBackListener {
        void onBack(String backCode);
    }
}
