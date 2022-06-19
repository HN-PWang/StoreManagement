package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.UserInfoBean;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.BindLocationPresenter;

/**
 * 库位绑定界面
 */
public class WarehouseBindActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvLocation;

    private TextView tvRfid;

    private String mLocationCode;

    private String mRfid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_bind);

        tvLocation = findViewById(R.id.tv_location);
        tvRfid = findViewById(R.id.tv_rfid);
    }

    private void bindLocation() {
        BindLocationPresenter presenter = new BindLocationPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {

            }

            @Override
            public void loadFailure(SMException exception) {

            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("正在执行", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });

        UserInfoBean bean = AccountManger.getInstance().getAccount();

        if (TextUtils.isEmpty(mLocationCode)) {
            ToastUtils.show("未扫描库位");
            return;
        }

        if (TextUtils.isEmpty(mRfid)) {
            ToastUtils.show("未读取RFID");
            return;
        }

        if (bean != null) {
            presenter.bind(mLocationCode, mRfid, bean.userCode);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_save:
                bindLocation();
                break;
        }
    }

}