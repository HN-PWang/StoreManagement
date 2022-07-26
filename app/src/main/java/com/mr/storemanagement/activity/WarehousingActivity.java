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

import com.mr.lib_base.AfterTextChangedListener;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.lib_base.widget.SMEditText;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.AsnCodeBean;
import com.mr.storemanagement.dialog.AsnSelectDialog;
import com.mr.storemanagement.eventbean.SaveAsnEvent;
import com.mr.storemanagement.presenter.GetAsnPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 入库界面
 */
public class WarehousingActivity extends BaseScannerActivity implements View.OnClickListener {

    private EditText tvSearchSite;

    private SMEditText tvSearchAsn;

    private List<AsnCodeBean> mAsnCodeBeans = new ArrayList<>();

    private AsnSelectDialog asnSelectDialog;

//    private SiteBean currentSiteBean = null;

    private String mAsnCode;

//    private SiteChooseHelper siteChooseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehousing);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        tvSearchSite = findViewById(R.id.tv_search_site);
        tvSearchAsn = findViewById(R.id.tv_asn_code);

//        tvSearchSite.setOnClickListener(this);
        tvSearchAsn.setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_select).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

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

        tvSearchAsn.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                mAsnCode = tvSearchAsn.getText().toString();
            }
        });

        tvSearchAsn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
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

    @Subscribe
    public void onEventMainThread(SaveAsnEvent event) {
        getAsn();
    }

    private void showAsnSelectDialog() {
        if (asnSelectDialog == null || !asnSelectDialog.isShowing()) {
            asnSelectDialog = new AsnSelectDialog(this, mAsnCodeBeans);
            asnSelectDialog.setOrderSelectListener(new AsnSelectDialog.OnOrderSelectListener() {
                @Override
                public void onSelect(AsnCodeBean asnCodeBean) {
                    mAsnCode = asnCodeBean.asn_code;
                    setOrderInfo();
                }
            });
            asnSelectDialog.show();
        }
    }

//    private void setSiteInfo() {
//        if (currentSiteBean != null) {
//            tvSearchSite.setText(currentSiteBean.site_code);
//        }
//    }

    private void setOrderInfo() {
        tvSearchAsn.setText(mAsnCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.tv_search_site:
//                siteChooseHelper.selectSite();
//                break;
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
        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ToastUtils.show("站点信息不能为空");
            return;
        }
        if (TextUtils.isEmpty(mAsnCode)) {
            ToastUtils.show("单号信息不能为空");
            return;
        }
        Intent intent = new Intent(this, ScannerPutStockActivity.class);
        intent.putExtra("site_key", tvSearchSite.getText().toString());
        intent.putExtra("ans_key", mAsnCode);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}