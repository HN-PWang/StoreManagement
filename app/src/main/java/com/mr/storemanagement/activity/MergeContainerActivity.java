package com.mr.storemanagement.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.widget.SMEditText;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.helper.SiteChooseHelper;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.CombindCheckItemPresenter;
import com.mr.storemanagement.presenter.CombindSavePresenter;
import com.mr.storemanagement.presenter.GetCombindFeedBoxPresenter;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

public class MergeContainerActivity extends BaseActivity implements View.OnClickListener
        , View.OnFocusChangeListener {

    private ConstraintLayout mConstraintLayout;
    private TextView tvSearchSite;
    private TextView tvOldCalled;
    private SMEditText etOldContainerNo;
    private TextView tvNewCalled;
    private SMEditText etNewContainerNo;
    private SMEditText etCxNo;
    private SMEditText etCount;

    private SiteChooseHelper siteChooseHelper;

    private SiteBean mSite = null;

    private String mOldContainerNo;

    private String mNewContainerNo;

    private int mScannerInitiator = 1; //1:旧料箱 2:新料箱 3:册序号 4:数量

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
        etCount = findViewById(R.id.et_count);
        tvSearchSite.setOnClickListener(this);
        findViewById(R.id.tv_complete).setOnClickListener(this);
        findViewById(R.id.tv_to_scanner).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_check_stock).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        siteChooseHelper = new SiteChooseHelper(this, 2);
        siteChooseHelper.setSiteClickListener(new SiteChooseHelper.OnSiteEventListener() {
            @Override
            public void onClick(SiteBean site) {
                mSite = site;
                setSiteInfo();
            }

            @Override
            public void onFirst(SiteBean site) {
                mSite = site;
                setSiteInfo();
            }
        });

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

        etCount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    writeCount(v.getText().toString().trim());
                }
                return false;
            }
        });
    }

    private void writeOldContainer(String code) {
        if (!TextUtils.isEmpty(code)) {
            mOldContainerNo = code;
        }
    }

    private void writeNewContainer(String code) {
        if (!TextUtils.isEmpty(code)) {
            mNewContainerNo = code;
        }
    }

    public void writeItemCode(String code) {

    }

    public void writeCount(String count) {

    }

    private void setSiteInfo() {
        if (mSite != null) {
            tvSearchSite.setText(mSite.site_code);
        }
    }

    private void setInputViewState() {
        etOldContainerNo.setSelected(mScannerInitiator == 1);
        etNewContainerNo.setSelected(mScannerInitiator == 2);
        etCxNo.setSelected(mScannerInitiator == 3);
        etCount.setSelected(mScannerInitiator == 4);

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
            if (!etCount.isFocused()) {
                etCount.requestFocus();
            }
        } else {
            mConstraintLayout.requestFocus();
        }
    }

    private void getCombindContainer() {
        GetCombindFeedBoxPresenter presenter = new GetCombindFeedBoxPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {

            }

            @Override
            public void loadFailure(SMException exception) {

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

        if (mSite == null) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "请选择站点信息");
            return;
        }

        if (TextUtils.isEmpty(mOldContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "请输入原料箱信息");
            return;
        }

        presenter.getData(mSite.site_code, mOldContainerNo);
    }

    private void combindCheckItem() {
        CombindCheckItemPresenter presenter = new CombindCheckItemPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {

            }

            @Override
            public void loadFailure(SMException exception) {

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

        if (TextUtils.isEmpty(mOldContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "请输入原料箱信息");
            return;
        }

        presenter.getData("", mOldContainerNo);
    }

    private void combindSave() {
        CombindSavePresenter presenter = new CombindSavePresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {

            }

            @Override
            public void loadFailure(SMException exception) {

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

        if (mSite == null) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "请选择站点信息");
            return;
        }

        if (TextUtils.isEmpty(mOldContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "请输入原料箱信息");
            return;
        }

        if (TextUtils.isEmpty(mNewContainerNo)) {
            ShowMsgDialogUtil.show(MergeContainerActivity.this
                    , "请输入目标料箱信息");
            return;
        }

        presenter.save(mSite.site_code, mOldContainerNo, mNewContainerNo, "", ""
                , "", AccountManger.getInstance().getUserCode(), "");
    }

    @Override
    public void onFocusChange(View view, boolean isFocus) {
        if (isFocus && !view.isFocused())
            switch (view.getId()) {
                case R.id.et_old_container_no:
                    mScannerInitiator = 1;
                    setInputViewState();
                    break;
                case R.id.et_new_container_no:
                    mScannerInitiator = 2;
                    setInputViewState();
                    break;
                case R.id.et_cx_no:
                    mScannerInitiator = 3;
                    setInputViewState();
                    break;
                case R.id.et_count:
                    mScannerInitiator = 4;
                    setInputViewState();
                    break;
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_site:
                siteChooseHelper.selectSite();
                break;
            case R.id.tv_complete:
                //并箱完成
                break;
            case R.id.tv_to_scanner:
                //扫描序列号
                break;
            case R.id.tv_back:
                //返回
                finish();
                break;
            case R.id.tv_check_stock:
                //查询库存
                break;
            case R.id.tv_save:
                //保存
                break;
        }
    }
}