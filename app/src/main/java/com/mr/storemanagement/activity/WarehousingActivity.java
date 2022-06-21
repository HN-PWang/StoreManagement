package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.OrderBean;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.dialog.OrderNoSelectDialog;
import com.mr.storemanagement.dialog.SiteSearchDialog;
import com.mr.storemanagement.helper.SiteChooseHelper;
import com.mr.storemanagement.presenter.GetAsnPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 入库界面
 */
public class WarehousingActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvSearchSite;
    private TextView tvSearchOrder;

    private List<SiteBean> mSiteBeans = new ArrayList<>();

    private List<OrderBean> mOrderBeans = new ArrayList<>();

    private SiteSearchDialog siteSearchDialog;

    private OrderNoSelectDialog orderNoSelectDialog;

    private SiteBean currentSiteBean = null;

    private OrderBean currentOrderBean = null;

    private SiteChooseHelper siteChooseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehousing);

        tvSearchSite = findViewById(R.id.tv_search_site);
        tvSearchOrder = findViewById(R.id.et_order_no);

        tvSearchSite.setOnClickListener(this);
        tvSearchOrder.setOnClickListener(this);
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

        getAsn();
    }


    private void getAsn() {
        GetAsnPresenter presenter = new GetAsnPresenter(this
                , new NetResultListener<List<OrderBean>>() {
            @Override
            public void loadSuccess(List<OrderBean> beans) {
                if (beans != null) {
                    mOrderBeans.clear();
                    mOrderBeans.addAll(beans);
                }
            }

            @Override
            public void loadFailure(SMException exception) {

            }
        }, null);
        presenter.getAsn();
    }

    private void showOrderDialog() {
        if (orderNoSelectDialog == null || !orderNoSelectDialog.isShowing()) {
            orderNoSelectDialog = new OrderNoSelectDialog(this, mOrderBeans);
            orderNoSelectDialog.setOrderSelectListener(new OrderNoSelectDialog.OnOrderSelectListener() {
                @Override
                public void onSelect(OrderBean orderBean) {
                    currentOrderBean = orderBean;
                    setOrderInfo();
                }
            });
            orderNoSelectDialog.show();
        }
    }

    private void setSiteInfo() {
        if (currentSiteBean != null) {
            tvSearchSite.setText(currentSiteBean.site_code);
        }
    }

    private void setOrderInfo() {
        if (currentOrderBean != null) {
            tvSearchOrder.setText(currentOrderBean.asn_code);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search_site:
                siteChooseHelper.selectSite();
                break;
            case R.id.et_order_no:
                showOrderDialog();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_select:
                showOrderDialog();
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
        if (currentOrderBean == null) {
            ToastUtils.show("单号信息不能为空");
            return;
        }
        Intent intent = new Intent(this, ScannerPutStockActivity.class);
        intent.putExtra("site_key", currentSiteBean.site_code);
        intent.putExtra("ans_key", currentOrderBean.asn_code);
        startActivity(intent);
    }

}