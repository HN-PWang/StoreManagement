package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.UserInfoBean;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.SetContainerBackPresenter;

/**
 * 回库扫描页
 */
public class WarehouseBackActivity extends BaseScannerActivity implements View.OnClickListener {

    private EditText etContainer;

    private TextView tvShelfArea;

    private TextView tvShelfLocation;

    private String mContainerCode;

    private String[] datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_back);

        mContainerCode = getIntent().getStringExtra(Constants.SCANNER_DATA_KEY);

        etContainer = findViewById(R.id.et_container);
        tvShelfArea = findViewById(R.id.tv_shelf_area);
        tvShelfLocation = findViewById(R.id.tv_shelf_location);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                writeContainerCode(message);
            }
        });

        etContainer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    writeContainerCode(etContainer.getText().toString().trim());
                }
                return false;
            }
        });
    }

    private void writeContainerCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            mContainerCode = code;
            etContainer.setText(mContainerCode);

            tvShelfArea.setText("");
            tvShelfLocation.setText("");

            allocate();
        }
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

                setContainerMarker();
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

    private void setContainerMarker() {
//        etContainer.setSelection(0, length);
//        etContainer.setSelection(0);
        etContainer.setSelectAllOnFocus(true);
    }

}