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
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.CombindCheckItemPresenter;
import com.mr.storemanagement.presenter.CombindSavePresenter;
import com.mr.storemanagement.presenter.GetCombindFeedBoxPresenter;
import com.mr.storemanagement.util.DataUtil;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

public class MergeContainerActivity extends BaseScannerActivity implements View.OnClickListener
        , View.OnFocusChangeListener {

    private ConstraintLayout mConstraintLayout;
    private TextView tvSearchSite;
    private TextView tvOldCalled;
    private SMEditText etOldContainerNo;
    private TextView tvNewCalled;
    private SMEditText etNewContainerNo;
    private SMEditText etCxNo;
    private ImageView ivScanRfId;
    private SMEditText tvScanSerial;
    private TextView tvScanSerialTag;
    private SMEditText etCount;

//    private SiteChooseHelper siteChooseHelper;

    private SearchStockDetailDialog mStockDetailDialog;

//    private SiteBean mSite = null;

    private String mOldContainerNo;

    private String mNewContainerNo;

    private String mItemId;

    private String mSNCode;

    private CombindCheckBean mCombindCheckBean;

    private int mScannerInitiator = 1; //1:????????? 2:????????? 3:????????? 4:????????? 5:??????

    private int IS_SN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_container);

        mConstraintLayout = findViewById(R.id.constraint_layout);
        tvSearchSite = findViewById(R.id.tv_search_site);
        tvOldCalled = findViewById(R.id.tv_old_called);
        etOldContainerNo = findViewById(R.id.et_old_container_no);
        tvNewCalled = findViewById(R.id.tv_new_called);
        etNewContainerNo = findViewById(R.id.et_new_container_no);
        etCxNo = findViewById(R.id.et_cx_no);
        ivScanRfId = findViewById(R.id.iv_scan_rfid);
        tvScanSerial = findViewById(R.id.tv_scan_serial);
        tvScanSerialTag = findViewById(R.id.tv_scan_serial_tag);
        etCount = findViewById(R.id.et_count);
        tvSearchSite.setOnClickListener(this);
        ivScanRfId.setOnClickListener(this);
        findViewById(R.id.tv_complete).setOnClickListener(this);
        findViewById(R.id.tv_to_scanner).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_check_stock).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        etOldContainerNo.setOnFocusChangeListener(this);
        etNewContainerNo.setOnFocusChangeListener(this);
        etCxNo.setOnFocusChangeListener(this);
        etCount.setOnFocusChangeListener(this);

//        siteChooseHelper = new SiteChooseHelper(this, 2);
//        siteChooseHelper.setSiteClickListener(new SiteChooseHelper.OnSiteEventListener() {
//            @Override
//            public void onClick(SiteBean site) {
//                mSite = site;
//                setSiteInfo();
//            }
//
//            @Override
//            public void onFirst(SiteBean site) {
//                mSite = site;
//                setSiteInfo();
//            }
//        });

        setContainerText(true, "");
        setContainerText(false, "");

        initListener();
    }

    private void initListener() {
        etOldContainerNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    writeOldContainer(v.getText().toString().trim());
                }
                return false;
            }
        });

        etNewContainerNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    writeNewContainer(v.getText().toString().trim());
                }
                return false;
            }
        });

        etCxNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    writeItemCode(v.getText().toString().trim());
                }
                return false;
            }
        });

        tvScanSerial.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    writeSnCode(v.getText().toString().trim());
                }
                return false;
            }
        });

        etCount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    writeCount(v.getText().toString().trim());
                }
                return false;
            }
        });

        setOnScannerListener(new BaseScannerActivity.OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (TextUtils.isEmpty(message))
                    return;

                if (mScannerInitiator == 1) {
                    writeOldContainer(message);
                } else if (mScannerInitiator == 2) {
                    writeNewContainer(message);
                } else if (mScannerInitiator == 3) {
                    writeItemCode(message);
                }
            }
        });

        setOnRfIdListener(new OnRfIdListener() {
            @Override
            public void onRFIdDataBack(String message) {
                if (TextUtils.isEmpty(message))
                    return;

                if (mScannerInitiator == 4) {
                    writeSnCode(message);

                    combindSave();
                }
            }
        });
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

    private void writeNewContainer(String code) {
        if (!TextUtils.isEmpty(code)) {
            if (etNewContainerNo.isEnabled()) {
                mNewContainerNo = code;
                etNewContainerNo.setText(code);

                getCombindContainer(false);
            }
        }
    }

    public void writeItemCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            mItemId = code;

            combindCheckItem();
        }
    }

    public void writeSnCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            mSNCode = code;

            combindSave();
        }
    }

    public void writeCount(String count) {
        mScannerInitiator = -1;

        setInputViewState();
    }

//    private void setSiteInfo() {
//        if (mSite != null) {
//            tvSearchSite.setText(mSite.site_code);
//        }
//    }

    private int getCount() {
        int count = 0;
        if (IS_SN == 1) {
            count = TextUtils.isEmpty(mSNCode) ? 0 : 1;
        } else {
            String str = etCount.getText().toString();
            count = DataUtil.getInt(str);
        }

        return count;
    }

    private void getCombindContainer(boolean old) {
        GetCombindFeedBoxPresenter presenter = new GetCombindFeedBoxPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                ToastUtils.show("??????????????????");

                String containerNo = old ? mOldContainerNo : mNewContainerNo;
                setContainerText(old, containerNo);

                if (old) {
                    mScannerInitiator = 2;
                } else {
                    mScannerInitiator = 3;
                }
                setInputViewState();
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(MergeContainerActivity.this, exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("?????????", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });

        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "????????????????????????");
            return;
        }

        if (TextUtils.isEmpty(mOldContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "????????????????????????");
            return;
        }

        presenter.getData(tvSearchSite.getText().toString(), mOldContainerNo
                , AccountManger.getInstance().getUserCode());
    }

    private void combindCheckItem() {
        CombindCheckItemPresenter presenter = new CombindCheckItemPresenter(this
                , new NetResultListener<CombindCheckBean>() {
            @Override
            public void loadSuccess(CombindCheckBean bean) {
                if (bean != null) {
                    mCombindCheckBean = bean;

                    setCombindCheckInfo();
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(MergeContainerActivity.this, exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("?????????", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });

        if (TextUtils.isEmpty(mOldContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "????????????????????????");
            return;
        }

        presenter.getData(mItemId, mOldContainerNo);
    }

    private void combindSave() {
        CombindSavePresenter presenter = new CombindSavePresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                handlerSave();
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(MergeContainerActivity.this
                        , exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("?????????", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });

        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "?????????????????????");
            return;
        }

        if (TextUtils.isEmpty(mOldContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "???????????????????????????");
            return;
        }

        if (TextUtils.isEmpty(mNewContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "???????????????????????????");
            return;
        }

        if (mCombindCheckBean == null) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "??????????????????");
            return;
        }

        if (TextUtils.isEmpty(mSNCode)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "??????SN???");
            return;
        }

        if (getCount() == 0) {
            ConfirmDialog mConfirmDialog = new ConfirmDialog(this, "?????????????????????????????????,????????????????????????", "??????"
                    , "??????", new ConfirmDialog.OnConfirmClickListener() {
                @Override
                public void onClick(boolean confirm) {
                    if (confirm) {
                        presenter.save(tvSearchSite.getText().toString(), mOldContainerNo, mNewContainerNo
                                , mCombindCheckBean.item_Code
                                , DataUtil.getInt(mCombindCheckBean.StockInfoId), getCount()
                                , AccountManger.getInstance().getUserCode(), mSNCode);
                    }
                }
            });
            mConfirmDialog.show();
        } else {
            presenter.save(tvSearchSite.getText().toString(), mOldContainerNo, mNewContainerNo, mCombindCheckBean.item_Code
                    , DataUtil.getInt(mCombindCheckBean.StockInfoId), getCount()
                    , AccountManger.getInstance().getUserCode(), mSNCode);
        }
    }

    private void setInputViewState() {
        etOldContainerNo.setSelected(mScannerInitiator == 1);
        etNewContainerNo.setSelected(mScannerInitiator == 2);
        etCxNo.setSelected(mScannerInitiator == 3);
        tvScanSerial.setSelected(mScannerInitiator == 4);
        etCount.setSelected(mScannerInitiator == 5);

        if (mScannerInitiator == 1) {
            if (!etOldContainerNo.isFocused()) {
                etOldContainerNo.requestFocus();
            }
        } else if (mScannerInitiator == 2) {
            if (!etNewContainerNo.isFocused()) {
                etNewContainerNo.requestFocus();
            }
        } else if (mScannerInitiator == 3) {
            if (!etCxNo.isFocused()) {
                etCxNo.requestFocus();
            }
        } else if (mScannerInitiator == 4) {
            if (!tvScanSerial.isFocused()) {
                tvScanSerial.requestFocus();
            }
        } else if (mScannerInitiator == 5) {
            if (!etCount.isFocused()) {
                etCount.requestFocus();
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
                    remake();
                    break;
                case R.id.et_new_container_no:
                    mScannerInitiator = 2;
                    setInputViewState();
                    break;
                case R.id.et_cx_no:
                    mScannerInitiator = 3;
                    setInputViewState();
                    break;
                case R.id.tv_scan_serial:
                    mScannerInitiator = 4;
                    setInputViewState();
                    break;
                case R.id.et_count:
                    mScannerInitiator = 5;
                    setInputViewState();
                    break;
            }
    }

    private void setContainerText(boolean old, String containerNo) {
        if (old) {
            if (TextUtils.isEmpty(containerNo)) {
                tvOldCalled.setText("?????????");
            } else {
                tvOldCalled.setText("????????????" + containerNo);
            }
        } else {
            if (TextUtils.isEmpty(containerNo)) {
                tvNewCalled.setText("?????????");
            } else {
                tvNewCalled.setText("????????????" + containerNo);
            }
        }
    }

    public void setContainerState(boolean lock) {
        if (lock) {
            etOldContainerNo.setEnabled(false);
            etNewContainerNo.setEnabled(false);
        } else {
            etOldContainerNo.setEnabled(true);
            etNewContainerNo.setEnabled(true);
        }
    }

    private void setCombindCheckInfo() {
        if (mCombindCheckBean != null) {
            setContainerState(true);

            if (DataUtil.getInt(mCombindCheckBean.is_SN) == 1) {
                IS_SN = 1;
                tvScanSerialTag.setSelected(true);
                ivScanRfId.setEnabled(true);
                etCount.setEnabled(false);
                mScannerInitiator = 4;
            } else {
                IS_SN = 0;
                tvScanSerialTag.setSelected(false);
                ivScanRfId.setEnabled(false);
                etCount.setEnabled(true);
                mScannerInitiator = 5;
            }

            setInputViewState();
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
//            case R.id.tv_search_site:
//                siteChooseHelper.selectSite();
//                break;
            case R.id.tv_complete:
                //????????????
                setContainerState(false);
                combindSave();
                break;
            case R.id.tv_to_scanner:
                //???????????????
                break;
            case R.id.tv_back:
                //??????
                finish();
                break;
            case R.id.tv_check_stock:
                //????????????
                showStockDetailDialog();
                break;
            case R.id.tv_save:
                //??????
                combindSave();
                break;
            case R.id.iv_scan_rfid:
                //??????rfid
                readMactchData();
                break;
        }
    }

    private void handlerSave() {
        mItemId = null;
        mSNCode = null;
        mCombindCheckBean = null;
        IS_SN = 0;

        etCxNo.setText("");
        etCount.setText("");
        tvScanSerial.setText("");

        mScannerInitiator = 3;
        setInputViewState();
    }

    private void remake() {
        mOldContainerNo = "";
        mNewContainerNo = "";
        mItemId = "";
        mSNCode = "";
        mCombindCheckBean = null;
        IS_SN = 0;

        etOldContainerNo.setText("");
        etNewContainerNo.setText("");
        etCxNo.setText("");
        tvScanSerial.setText("");
        etCount.setText("");

        tvScanSerialTag.setSelected(false);
        ivScanRfId.setEnabled(false);
        etCount.setEnabled(false);
    }

}