package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.AfterTextChangedListener;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.ContainerGoodsBean;
import com.mr.storemanagement.presenter.CheckContainerPresenter;
import com.mr.storemanagement.util.NullUtils;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 拣货扫描料箱界面
 */
public class PickingFeedBoxScannerActivity extends BaseScannerActivity implements View.OnClickListener {

    private EditText tvRdIdRead;

    private String mContainerCode;

    private String mSiteCode;

    private String mTaskData;

    private List<ContainerGoodsBean> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_feed_box_scanner);

        mSiteCode = getIntent().getStringExtra(Constants.SITE_CODE_KEY);
        mTaskData = getIntent().getStringExtra(Constants.TASK_DATA_KEY);

        tvRdIdRead = findViewById(R.id.tv_rdid_read);

        findViewById(R.id.tv_from_rfid).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        tvRdIdRead.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                mContainerCode = tvRdIdRead.getText().toString();
            }
        });

        tvRdIdRead.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    setSerialCodeToView();
                    check();
                }
                return false;
            }
        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (!TextUtils.isEmpty(message)) {
                    mContainerCode = message;
                    setSerialCodeToView();
                    check();
                }
            }
        });
    }

    private void setSerialCodeToView() {
        tvRdIdRead.setText(mContainerCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_from_rfid:
                readMactchData();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_next:
                toScannerGoods();
                break;
        }
    }

    private void toScannerGoods() {
        if (NullUtils.isEmpty(mDataList)) {
//            ToastUtils.show("料箱返回数据为空");
            ShowMsgDialogUtil.show(PickingFeedBoxScannerActivity.this
                    , "料箱返回数据为空");
            return;
        }

        Intent intent = new Intent(this, ScannerOutStockActivity.class);
        intent.putExtra(Constants.SITE_CODE_KEY, mSiteCode);
        intent.putExtra(Constants.TASK_DATA_KEY, mTaskData);
        intent.putExtra(Constants.CONTAINER_CODE_KEY, mContainerCode);
        String cgd = JSONObject.toJSONString(mDataList);
        intent.putExtra(Constants.CONTAINER_GOODS_DATA_KEY, cgd);
        startActivity(intent);

        finish();
    }

    private void check() {
        CheckContainerPresenter presenter = new CheckContainerPresenter(this
                , new NetResultListener<List<ContainerGoodsBean>>() {
            @Override
            public void loadSuccess(List<ContainerGoodsBean> beans) {
                mDataList.clear();
                if (NullUtils.isNotEmpty(beans)) {
                    mDataList.addAll(beans);
                }
                toScannerGoods();
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
        presenter.check(mSiteCode, mContainerCode);
    }

}