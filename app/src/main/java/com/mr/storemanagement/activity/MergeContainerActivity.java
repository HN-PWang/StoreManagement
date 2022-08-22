package com.mr.storemanagement.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.lib_base.widget.SMEditText;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.CombindCheckBean;
import com.mr.storemanagement.bean.InvDetailsBean;
import com.mr.storemanagement.dialog.ConfirmDialog;
import com.mr.storemanagement.dialog.SearchStockDetailDialog;
import com.mr.storemanagement.helper.ItemFormCodeHelper;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.CombindCheckItemPresenter;
import com.mr.storemanagement.presenter.CombindSavePresenter;
import com.mr.storemanagement.presenter.GetCombindFeedBoxPresenter;
import com.mr.storemanagement.util.DataUtil;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

public class MergeContainerActivity extends BaseScannerActivity implements View.OnClickListener
        , View.OnFocusChangeListener {

    private ConstraintLayout mConstraintLayout;
    private SMEditText tvSearchSite;
    private TextView tvOldCalled;
    private SMEditText etOldContainerNo;

    private SearchStockDetailDialog mStockDetailDialog;

    private String mOldContainerNo;

    private int mScannerInitiator = 0; //0:站点 1:旧料箱 2:新料箱 3:册序号 4:机件号 5:数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_container);

        mConstraintLayout = findViewById(R.id.constraint_layout);
        tvSearchSite = findViewById(R.id.tv_search_site);
        tvOldCalled = findViewById(R.id.tv_old_called);
        etOldContainerNo = findViewById(R.id.et_old_container_no);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_check_stock).setOnClickListener(this);

        tvSearchSite.setOnFocusChangeListener(this);
        etOldContainerNo.setOnFocusChangeListener(this);

        setContainerText( "");

        initListener();

        tvSearchSite.requestFocus();
    }

    private void initListener() {
        tvSearchSite.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    writeSiteCode(v.getText().toString().trim());
                }
                return false;
            }
        });

        etOldContainerNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    writeOldContainer(v.getText().toString().trim());
                }
                return false;
            }
        });

        setOnScannerListener(new BaseScannerActivity.OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (TextUtils.isEmpty(message))
                    return;
                if (mScannerInitiator == 0) {
                    writeSiteCode(message);
                } else if (mScannerInitiator == 1) {
                    writeOldContainer(message);
                }
            }
        });

    }

    private void writeSiteCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            tvSearchSite.setText(code);

            mScannerInitiator = 1;

            setInputViewState();
        }
    }

    private void writeOldContainer(String code) {
        if (!TextUtils.isEmpty(code)) {
            if (etOldContainerNo.isEnabled()) {
                mOldContainerNo = code;
                etOldContainerNo.setText(code);

                getCombindContainer(true);
            }
        }
    }

    private void getCombindContainer(boolean old) {
        GetCombindFeedBoxPresenter presenter = new GetCombindFeedBoxPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                ToastUtils.show("呼叫料箱成功");

                setContainerText( mOldContainerNo);
                mScannerInitiator = 0;
                setInputViewState();
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(MergeContainerActivity.this, exception.getErrorMsg());
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

        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "站点信息不能为空");
            return;
        }

        if (TextUtils.isEmpty(mOldContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "请输入原料箱信息");
            return;
        }

        presenter.getData(tvSearchSite.getText().toString(), mOldContainerNo
                , AccountManger.getInstance().getUserCode());
    }

    private void setInputViewState() {
        tvSearchSite.setSelected(mScannerInitiator == 0);
        etOldContainerNo.setSelected(mScannerInitiator == 1);

        if (mScannerInitiator == 0) {
            if (!tvSearchSite.isFocused()) {
                tvSearchSite.requestFocus();
            }
        } else if (mScannerInitiator == 1) {
            if (!etOldContainerNo.isFocused()) {
                etOldContainerNo.requestFocus();
            }
        } else {
            mConstraintLayout.requestFocus();
        }
    }

    @Override
    public void onFocusChange(View view, boolean isFocus) {
        if (isFocus)
            switch (view.getId()) {
                case R.id.et_old_container_no:
                    mScannerInitiator = 1;
                    setInputViewState();
                    break;
            }
    }

    private void setContainerText(String containerNo) {
        if (TextUtils.isEmpty(containerNo)) {
            tvOldCalled.setText("未呼叫");
        } else {
            tvOldCalled.setText("已呼叫：" + containerNo);
        }
    }

    private void showStockDetailDialog() {
        if (mStockDetailDialog == null || !mStockDetailDialog.isShowing()) {
            mStockDetailDialog = new SearchStockDetailDialog(this, this);
            mStockDetailDialog.setSnClickListener(new SearchStockDetailDialog.OnSnClickListener() {
                @Override
                public void OnSnClick(InvDetailsBean bean) {
//                    showCheckSnDialog(bean.sn_list.SnList, mInvCode);
                }
            });
            mStockDetailDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                //返回
                finish();
                break;
            case R.id.tv_check_stock:
                //查询库存
                showStockDetailDialog();
                break;
        }
    }

}