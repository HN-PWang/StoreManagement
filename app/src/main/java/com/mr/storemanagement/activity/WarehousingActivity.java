package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.OrderBean;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.dialog.OrderNoSelectDialog;
import com.mr.storemanagement.dialog.SiteSearchDialog;
import com.mr.storemanagement.presenter.GetAsnPresenter;
import com.mr.storemanagement.presenter.GetSitePresenter;

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

        getSite();
        getAsn();
    }

    private void getSite() {
        GetSitePresenter presenter = new GetSitePresenter(this
                , new NetResultListener<List<SiteBean>>() {
            @Override
            public void loadSuccess(List<SiteBean> beans) {
                if (beans != null && beans.size() > 0) {
                    mSiteBeans.clear();
                    mSiteBeans.addAll(beans);
                    currentSiteBean = mSiteBeans.get(0);
                    setSiteInfo();
                }
            }

            @Override
            public void loadFailure(SMException exception) {

            }
        }, null);
        presenter.getSite();
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

    private void showSiteDialog() {
        if (siteSearchDialog == null || !siteSearchDialog.isShowing()) {
            siteSearchDialog = new SiteSearchDialog(this, mSiteBeans);
            siteSearchDialog.setSiteSelectListener(new SiteSearchDialog.OnSiteSelectListener() {
                @Override
                public void onSelect(SiteBean siteBean) {
                    currentSiteBean = siteBean;
                    setSiteInfo();
                }
            });
            siteSearchDialog.show();
        }
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
                showSiteDialog();
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
        Intent intent = new Intent(this, GoodsScanningActivity.class);
        intent.putExtra("site_key", currentSiteBean.site_code);
        intent.putExtra("ans_key", currentOrderBean.asn_code);
        startActivity(intent);
    }

}