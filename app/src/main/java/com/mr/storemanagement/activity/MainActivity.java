package com.mr.storemanagement.activity;

import android.os.Bundle;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.SMLog;
import com.mr.storemanagement.presenter.LoginPresenter;
import com.mr.storemanagement.R;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SMLog.i("LoginPresenter S= ");

        LoginPresenter presenter = new LoginPresenter(this
                , new NetResultListener<String>() {
            @Override
            public void loadSuccess(String s) {
                SMLog.i("loadSuccess S= "+ s);
            }

            @Override
            public void loadFailure(SMException exception) {
                SMLog.i("loadFailure exception= "+ exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                SMLog.i("startLoading = ");
            }

            @Override
            public void finishLoading() {
                SMLog.i("finishLoading = ");
            }
        });
        presenter.login();
    }

}