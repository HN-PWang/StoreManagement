package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.lib_base.AfterTextChangedListener;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.lib_base.widget.SMEditText;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.CheckSnAdapter;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.UserInfoBean;
import com.mr.storemanagement.dialog.BindTypeDialog;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.BindLocationPresenter;
import com.mr.storemanagement.presenter.BindRfIdSavePresenter;
import com.mr.storemanagement.presenter.CheckRfIdPresenter;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 库位绑定界面
 */
public class WarehouseBindActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvBindType;

    private SMEditText tvLocation;

    private SMEditText tvRfid;

    private RecyclerView rvSn;

    private String mLocationCode;

    private String mRfid;

    private CheckSnAdapter snAdapter;

    private List<String> mDataList = new ArrayList<>();

    private List<String> inputSnList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_bind);

        tvBindType = findViewById(R.id.tv_bind_type);
        tvLocation = findViewById(R.id.tv_location);
        tvRfid = findViewById(R.id.tv_rfid);
        rvSn = findViewById(R.id.rv_sn);

        findViewById(R.id.tv_bind_type).setOnClickListener(this);
        findViewById(R.id.tv_get_rfid).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        snAdapter = new CheckSnAdapter(this, mDataList);
        rvSn.setLayoutManager(new LinearLayoutManager(this));
        rvSn.setAdapter(snAdapter);

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

                if (!inputSnList.contains(message)) {
                    inputSnList.add(message);
                }
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
                    inputSnList.clear();
                    inputSnList.add(tvRfid.getText().toString());

                    checkRfId(inputSnList);
                }
                return false;
            }
        });

        tvBindType.setText(Constants.typeList.get(0));
    }

    @Override
    public void onRfIdReadComplete() {
        checkRfId(inputSnList);
    }

    private void showTypeSelectDialog() {
        BindTypeDialog dialog = new BindTypeDialog(this, Constants.typeList);
        dialog.setSelectTypeListener(new BindTypeDialog.OnSelectTypeListener() {
            @Override
            public void onSelect(String type) {
                tvBindType.setText(type);
            }
        });
        dialog.show();
    }

    private void checkRfId(List<String> rfIds) {
        CheckRfIdPresenter presenter = new CheckRfIdPresenter(this
                , new NetResultListener<List<String>>() {
            @Override
            public void loadSuccess(List<String> list) {
                mDataList.clear();
                mDataList.addAll(list);
                snAdapter.notifyDataSetChanged();
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(WarehouseBindActivity.this
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
        presenter.check(tvBindType.getText().toString(), rfIds);
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
            presenter.bind(mDataList, mLocationCode, tvBindType.getText().toString()
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
                inputSnList.clear();
                readMactchData();
                break;
            case R.id.tv_bind_type:
                showTypeSelectDialog();
                break;
        }
    }
}