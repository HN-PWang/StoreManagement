package com.mr.storemanagement.helper;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.presenter.CheckRfIdPresenter;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/8/22
 * @description:
 */
public class CheckRfIdHelper {

    private BaseActivity activity;

    public CheckRfIdHelper(BaseActivity activity) {
        this.activity = activity;
    }

    public void check(List<String> rfIds, OnCheckRfIdBackListener backListener) {
        CheckRfIdPresenter presenter = new CheckRfIdPresenter(activity
                , new NetResultListener<List<String>>() {
            @Override
            public void loadSuccess(List<String> list) {
                if (backListener != null)
                    backListener.onBack(list);
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
        presenter.check("", rfIds);
    }

    public interface OnCheckRfIdBackListener {
        void onBack(List<String> backList);
    }
}
