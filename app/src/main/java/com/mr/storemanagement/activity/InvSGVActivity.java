package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.InvDetailsBean;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.GetInvDetailsPresenter;
import com.mr.storemanagement.presenter.GetInvSetAgvTaskPresenter;
import com.mr.storemanagement.presenter.GetInvSetNonAgvTaskPresenter;
import com.mr.storemanagement.util.NullUtils;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import java.util.List;

public class InvSGVActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvAgvAutoInv;
    private TextView tvTaskUnsubmit;
    private TextView tvScannerInv;

    private String mSiteCode;
    private String mAsnCode;
    private boolean mHasTask;
    private boolean mHasNonAgv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_s_g_v);

        mSiteCode = getIntent().getStringExtra(Constants.SITE_CODE_KEY);
        mAsnCode = getIntent().getStringExtra(Constants.HAS_TASK_KEY);
        mHasTask = getIntent().getBooleanExtra(Constants.ASN_DATA_KEY, false);
        mHasNonAgv = getIntent().getBooleanExtra(Constants.HAS_NON_AGV_KEY, false);

        tvAgvAutoInv = findViewById(R.id.tv_agv_auto_inv);
        tvTaskUnsubmit = findViewById(R.id.tv_task_unsubmit);
        tvScannerInv = findViewById(R.id.tv_scanner_inv);
        tvAgvAutoInv.setOnClickListener(this);
        tvScannerInv.setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);

        tvTaskUnsubmit.setSelected(mHasTask);
        tvAgvAutoInv.setEnabled(mHasTask);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_agv_auto_inv:
                autoInv();
                break;
            case R.id.tv_scanner_inv:
                scannerInv();
                break;
        }
    }

    private void autoInv() {
        GetInvSetAgvTaskPresenter presenter = new GetInvSetAgvTaskPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                ShowMsgDialogUtil.show(InvSGVActivity.this
                        , "自动盘点成功");
                tvTaskUnsubmit.setSelected(false);
                tvAgvAutoInv.setEnabled(false);
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(InvSGVActivity.this
                        , exception.getErrorMsg());
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
        presenter.get(mAsnCode, mSiteCode, AccountManger.getInstance().getUserCode());
    }

    private void scannerInv() {
        GetInvSetNonAgvTaskPresenter presenter = new GetInvSetNonAgvTaskPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {

            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(InvSGVActivity.this
                        , exception.getErrorMsg());
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
        presenter.get(mAsnCode, mSiteCode, AccountManger.getInstance().getUserCode());
    }

    private void getInvDetails(String invCode) {
        GetInvDetailsPresenter presenter = new GetInvDetailsPresenter(this
                , new NetResultListener<List<InvDetailsBean>>() {
            @Override
            public void loadSuccess(List<InvDetailsBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    intoScanning(beans);
                } else {
                    ShowMsgDialogUtil.show(InvSGVActivity.this
                            , "没有获取到盘点任务详情信息");
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(InvSGVActivity.this
                        , exception.getErrorMsg());
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
        presenter.get(invCode);
    }

    private void intoScanning(List<InvDetailsBean> beans) {
        if (TextUtils.isEmpty(mSiteCode)) {
            ToastUtils.show("站点信息不能为空");
            return;
        }
        if (TextUtils.isEmpty(mAsnCode)) {
            ToastUtils.show("单号信息不能为空");
            return;
        }
        Intent intent = new Intent(this, InventoryActivity.class);
        intent.putExtra(Constants.SITE_CODE_KEY, mSiteCode);
        intent.putExtra(Constants.HAS_TASK_KEY, mAsnCode);
        intent.putExtra(Constants.INV_DETAILS_DATA_KEY, JSONObject.toJSONString(beans));
        startActivity(intent);
    }

}