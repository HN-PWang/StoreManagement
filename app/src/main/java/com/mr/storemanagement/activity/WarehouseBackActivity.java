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
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.UserInfoBean;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.AllocateLocationPresenter;

/**
 * 回库扫描页
 */
public class WarehouseBackActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvContainer;

    private TextView tvShelfArea;

    private TextView tvShelfLocation;

    private String mContainerCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_back);

        mContainerCode = getIntent().getStringExtra(Constants.SCANNER_DATA_KEY);

        tvContainer = findViewById(R.id.tv_container);
        tvShelfArea = findViewById(R.id.tv_shelf_area);
        tvShelfLocation = findViewById(R.id.tv_shelf_location);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);

        allocate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_confirm:
                //不知道出于什么想法
                break;
        }
    }

    private void allocate() {
        AllocateLocationPresenter presenter = new AllocateLocationPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {

            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });

        UserInfoBean bean = AccountManger.getInstance().getAccount();
        if (!TextUtils.isEmpty(mContainerCode) && bean != null) {
            presenter.allocate(mContainerCode, bean.userCode);
        }
    }

}