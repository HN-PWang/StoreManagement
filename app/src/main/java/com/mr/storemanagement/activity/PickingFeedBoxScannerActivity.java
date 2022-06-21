package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.CheckBoxBackBean;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.CheckFeedBoxPresenter;

import java.util.List;

/**
 * 拣货扫描料箱界面
 */
public class PickingFeedBoxScannerActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvRdIdRead;

    private String mSerialCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_feed_box_scanner);

        tvRdIdRead = findViewById(R.id.tv_rdid_read);

        findViewById(R.id.tv_rdid_read).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (!TextUtils.isEmpty(message)) {
                    mSerialCode = message;
                    setSerialCodeToView();
                }
            }
        });
    }

    private void setSerialCodeToView() {
        tvRdIdRead.setText(mSerialCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_rdid_read:
                readMactchData();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_next:
                check();
                break;
        }
    }

    private void check() {
        CheckFeedBoxPresenter presenter = new CheckFeedBoxPresenter(this
                , new NetResultListener<List<CheckBoxBackBean>>() {
            @Override
            public void loadSuccess(List<CheckBoxBackBean> beans) {

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
        presenter.check(mSerialCode, AccountManger.getInstance().getUserCode());
    }

}