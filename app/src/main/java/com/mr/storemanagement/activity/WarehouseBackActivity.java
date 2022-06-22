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
import com.mr.storemanagement.presenter.SetContainerBackPresenter;

import java.util.Arrays;

/**
 * 回库扫描页
 */
public class WarehouseBackActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvContainer;

    private TextView tvShelfArea;

    private TextView tvShelfLocation;

    private String mContainerCode;

    private String[] datas;

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

        tvContainer.setText(mContainerCode);

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

    private void setDataToView() {
        if (datas != null) {
            if (datas.length > 0) {
                tvShelfArea.setText(datas[0]);
            }
            if (datas.length > 1) {
                tvShelfLocation.setText(datas[1]);
            }
        }
    }

    private void allocate() {
        SetContainerBackPresenter presenter = new SetContainerBackPresenter(this
                , new NetResultListener<String>() {
            @Override
            public void loadSuccess(String date) {
                if (!TextUtils.isEmpty(date)) {
                    datas = date.split(",");
                    setDataToView();
                }
                ToastUtils.show("操作成功");
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
            presenter.allocate(mContainerCode, AccountManger.getInstance().getUserCode());
        }
    }

}