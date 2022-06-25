package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.AsnCodeBean;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.dialog.AsnSelectDialog;
import com.mr.storemanagement.helper.SiteChooseHelper;
import com.mr.storemanagement.presenter.GetAsnPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 入库界面
 */
public class WarehousingActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvSearchSite;

    private EditText tvSearchAsn;

    private List<AsnCodeBean> mAsnCodeBeans = new ArrayList<>();

    private AsnSelectDialog asnSelectDialog;

    private SiteBean currentSiteBean = null;

    private AsnCodeBean currentAsnCodeBean = null;

    private SiteChooseHelper siteChooseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehousing);

        tvSearchSite = findViewById(R.id.tv_search_site);
        tvSearchAsn = findViewById(R.id.tv_asn_code);

        tvSearchSite.setOnClickListener(this);
        tvSearchAsn.setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_select).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        siteChooseHelper = new SiteChooseHelper(this, false);
        siteChooseHelper.setSiteClickListener(new SiteChooseHelper.OnSiteEventListener() {
            @Override
            public void onClick(SiteBean site) {
                currentSiteBean = site;
                setSiteInfo();
            }

            @Override
            public void onFirst(SiteBean site) {
                currentSiteBean = site;
                setSiteInfo();
            }
        });

        tvSearchAsn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    getAsn();
                }
                return false;
            }
        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (TextUtils.isEmpty(message))
                    tvSearchAsn.setText(message);
            }
        });

        getAsn();
    }


    private void getAsn() {
        GetAsnPresenter presenter = new GetAsnPresenter(this
                , new NetResultListener<List<AsnCodeBean>>() {
            @Override
            public void loadSuccess(List<AsnCodeBean> beans) {
                if (beans != null) {
                    mAsnCodeBeans.clear();
                    mAsnCodeBeans.addAll(beans);
                }
            }

            @Override
            public void loadFailure(SMException exception) {

            }
        }, null);
        presenter.getAsn();
    }

    private void showAsnSelectDialog() {
        if (asnSelectDialog == null || !asnSelectDialog.isShowing()) {
            asnSelectDialog = new AsnSelectDialog(this, mAsnCodeBeans);
            asnSelectDialog.setOrderSelectListener(new AsnSelectDialog.OnOrderSelectListener() {
                @Override
                public void onSelect(AsnCodeBean asnCodeBean) {
                    currentAsnCodeBean = asnCodeBean;
                    setOrderInfo();
                }
            });
            asnSelectDialog.show();
        }
    }

    private void setSiteInfo() {
        if (currentSiteBean != null) {
            tvSearchSite.setText(currentSiteBean.site_code);
        }
    }

    private void setOrderInfo() {
        if (currentAsnCodeBean != null) {
            tvSearchAsn.setText(currentAsnCodeBean.asn_code);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search_site:
                siteChooseHelper.selectSite();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_select:
                showAsnSelectDialog();
                break;
            case R.id.tv_next:
                intoScanning();
                break;
        }
    }

    private void intoScanning() {
        if (currentSiteBean == null) {
            ToastUtils.show("站点信息不能为空");
            return;
        }
        if (currentAsnCodeBean == null) {
            ToastUtils.show("单号信息不能为空");
            return;
        }
        Intent intent = new Intent(this, ScannerPutStockActivity.class);
        intent.putExtra("site_key", currentSiteBean.site_code);
        intent.putExtra("ans_key", currentAsnCodeBean.asn_code);
        startActivity(intent);
    }

}