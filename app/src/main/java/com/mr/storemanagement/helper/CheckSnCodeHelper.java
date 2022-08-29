package com.mr.storemanagement.helper;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.presenter.CheckRfIdPresenter;
import com.mr.storemanagement.presenter.CheckSnCodePresenter;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/8/22
 * @description:
 */
public class CheckSnCodeHelper {

    private BaseActivity activity;

    public CheckSnCodeHelper(BaseActivity activity) {
        this.activity = activity;
    }

    public void check(String snCode, OnCheckRfIdBackListener backListener) {
        CheckSnCodePresenter presenter = new CheckSnCodePresenter(activity
                , new NetResultListener<String>() {
            @Override
            public void loadSuccess(String data) {
                if (backListener != null)
                    backListener.onBack(data);
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
        presenter.check(snCode);
    }

    public interface OnCheckRfIdBackListener {
        void onBack(String code);
    }
}
