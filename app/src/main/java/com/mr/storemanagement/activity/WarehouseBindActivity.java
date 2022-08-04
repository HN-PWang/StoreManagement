package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mr.lib_base.AfterTextChangedListener;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.lib_base.widget.SMEditText;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.UserInfoBean;
import com.mr.storemanagement.dialog.BindTypeDialog;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.BindLocationPresenter;
import com.mr.storemanagement.presenter.BindRfIdSavePresenter;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 库位绑定界面
 */
public class WarehouseBindActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvBindType;

    private SMEditText tvLocation;

    private SMEditText tvRfid;

    private String mLocationCode;

    private String mRfid;

    private List<String> typeList = Arrays.asList(new String[]{"库位", "容器号", "序列号"});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_bind);

        tvBindType = findViewById(R.id.tv_bind_type);
        tvLocation = findViewById(R.id.tv_location);
        tvRfid = findViewById(R.id.tv_rfid);

        findViewById(R.id.tv_bind_type).setOnClickListener(this);
        findViewById(R.id.tv_get_rfid).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                mLocationCode = message;
                tvLocation.setText(mLocationCode);
            }
        });

        setOnRfIdListener(new OnRfIdListener() {
            @Override
            public void onRFIdDataBack(String message) {
                mRfid = message;
                tvRfid.setText(mRfid);
            }
        });

        tvLocation.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                mLocationCode = tvLocation.getText().toString();
            }
        });

        tvRfid.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                mRfid = tvRfid.getText().toString();
            }
        });

        tvRfid.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    bindLocation();
                }
                return false;
            }
        });

        tvBindType.setText(typeList.get(0));
    }

    private void showTypeSelectDialog() {
        BindTypeDialog dialog = new BindTypeDialog(this, typeList);
        dialog.setSelectTypeListener(new BindTypeDialog.OnSelectTypeListener() {
            @Override
            public void onSelect(String type) {
                tvBindType.setText(type);
            }
        });
        dialog.show();
    }

    private void bindLocation() {
        BindRfIdSavePresenter presenter = new BindRfIdSavePresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                ToastUtils.show(getString(R.string.str_operate_successfully));
                mLocationCode = "";
                mRfid = "";
                tvLocation.setText(mLocationCode);
                tvRfid.setText(mRfid);
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
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
            ShowMsgDialogUtil.show(WarehouseBindActivity.this
                    , "未绑定信息");
            return;
        }

        if (TextUtils.isEmpty(mRfid)) {
//            ToastUtils.show("未读取RFID");
            ShowMsgDialogUtil.show(WarehouseBindActivity.this
                    , "未读取RFID");
            return;
        }

        if (bean != null) {
            presenter.bind(mRfid, mLocationCode, tvBindType.getText().toString()
                    , AccountManger.getInstance().getUserCode());
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
            case R.id.tv_get_rfid:
                readMactchData();
                break;
            case R.id.tv_bind_type:
                showTypeSelectDialog();
                break;
        }
    }
}