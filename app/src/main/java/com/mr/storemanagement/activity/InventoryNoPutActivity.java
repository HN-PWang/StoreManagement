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
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.InvCheckBackBean;
import com.mr.storemanagement.bean.InvCodeBean;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.dialog.InvSelectDialog;
import com.mr.storemanagement.eventbean.SaveAsnEvent;
import com.mr.storemanagement.eventbean.SaveInvEvent;
import com.mr.storemanagement.helper.SiteChooseHelper;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.GetInvCheckPresenter;
import com.mr.storemanagement.presenter.GetInvPresenter;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 盘点单号输入界面
 */
public class InventoryNoPutActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvSearchSite;

    private EditText tvSearchAsn;

    private List<InvCodeBean> mAsnCodeBeans = new ArrayList<>();

    private InvSelectDialog asnSelectDialog;

    private SiteBean currentSiteBean = null;

    private String mAsnCode;

    private SiteChooseHelper siteChooseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_no_put);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        tvSearchSite = findViewById(R.id.tv_search_site);
        tvSearchAsn = findViewById(R.id.tv_asn_code);

        tvSearchSite.setOnClickListener(this);
        tvSearchAsn.setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_select).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        siteChooseHelper = new SiteChooseHelper(this, 2);
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
        GetInvPresenter presenter = new GetInvPresenter(this
                , new NetResultListener<List<InvCodeBean>>() {
            @Override
            public void loadSuccess(List<InvCodeBean> beans) {
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

    private void checkInv() {
        GetInvCheckPresenter presenter = new GetInvCheckPresenter(this
                , new NetResultListener<InvCheckBackBean>() {
            @Override
            public void loadSuccess(InvCheckBackBean bean) {
                handlerInvCheckBack(bean);
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(InventoryNoPutActivity.this
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
        presenter.check(mAsnCode, AccountManger.getInstance().getUserCode());
    }

    @Subscribe
    public void onEventMainThread(SaveInvEvent event) {
        getAsn();
    }

    private void handlerInvCheckBack(InvCheckBackBean bean) {
        if (bean == null || !bean.HasAgv) {
            ShowMsgDialogUtil.show(InventoryNoPutActivity.this
                    , "没有盘点任务");
        } else {
            intoScanning(bean);
        }
    }

    private void showAsnSelectDialog() {
        if (asnSelectDialog == null || !asnSelectDialog.isShowing()) {
            asnSelectDialog = new InvSelectDialog(this, mAsnCodeBeans);
            asnSelectDialog.setOrderSelectListener(new InvSelectDialog.OnOrderSelectListener() {
                @Override
                public void onSelect(InvCodeBean asnCodeBean) {
                    mAsnCode = asnCodeBean.inventory_code;
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
        tvSearchAsn.setText(mAsnCode);
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
//                intoScanning();
                checkInv();
                break;
        }
    }

    private void intoScanning(InvCheckBackBean bean) {
        if (currentSiteBean == null) {
            ToastUtils.show("站点信息不能为空");
            return;
        }
        if (TextUtils.isEmpty(mAsnCode)) {
            ToastUtils.show("单号信息不能为空");
            return;
        }
        Intent intent = new Intent(this, InvSGVActivity.class);
        intent.putExtra(Constants.SITE_CODE_KEY, currentSiteBean.site_code);
        intent.putExtra(Constants.HAS_TASK_KEY, mAsnCode);
        intent.putExtra(Constants.ASN_DATA_KEY, bean.HasTask);
        intent.putExtra(Constants.HAS_NON_AGV_KEY, bean.HasNonAgv);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}