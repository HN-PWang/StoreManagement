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
import com.mr.storemanagement.util.ShowMsgDialogUtil;

/**
 * 回库扫描页
 */
public class WarehouseBackActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvSearchSite;

    private EditText etContainer;

    private TextView tvShelfArea;

    private TextView tvShelfLocation;

    private String mContainerCode;

    private String[] datas;

//    private SiteChooseHelper siteChooseHelper;

//    private SiteBean currentSiteBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_back);

        mContainerCode = getIntent().getStringExtra(Constants.SCANNER_DATA_KEY);

        tvSearchSite = findViewById(R.id.tv_search_site);
        etContainer = findViewById(R.id.et_container);
        tvShelfArea = findViewById(R.id.tv_shelf_area);
        tvShelfLocation = findViewById(R.id.tv_shelf_location);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);
//        tvSearchSite.setOnClickListener(this);

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                writeContainerCode(message);
            }
        });

//        siteChooseHelper = new SiteChooseHelper(this, 0);
//        siteChooseHelper.setSiteClickListener(new SiteChooseHelper.OnSiteEventListener() {
//            @Override
//            public void onClick(SiteBean site) {
//                currentSiteBean = site;
//                setSiteInfo();
//            }
//
//            @Override
//            public void onFirst(SiteBean site) {
//                currentSiteBean = site;
//                setSiteInfo();
//            }
//        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (TextUtils.isEmpty(message)) {
                    tvSearchSite.setText(message);
                }
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
//            case R.id.tv_search_site:
//                siteChooseHelper.selectSite();
//                break;
        }
    }

//    private void setSiteInfo() {
//        if (currentSiteBean != null) {
//            tvSearchSite.setText(currentSiteBean.site_code);
//        }
//    }

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

        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ShowMsgDialogUtil.show(WarehouseBackActivity.this, "请选择站点");
            return;
        }

        if (TextUtils.isEmpty(mContainerCode)) {
            ShowMsgDialogUtil.show(WarehouseBackActivity.this, "请输入料箱号");
            return;
        }

        UserInfoBean bean = AccountManger.getInstance().getAccount();
        if (bean != null) {
            presenter.allocate(mContainerCode, AccountManger.getInstance().getUserCode()
                    , tvSearchSite.getText().toString());
        }
    }

    private void setContainerMarker() {
//        etContainer.setSelection(0, length);
//        etContainer.setSelection(0);
        etContainer.setSelectAllOnFocus(true);
    }

}