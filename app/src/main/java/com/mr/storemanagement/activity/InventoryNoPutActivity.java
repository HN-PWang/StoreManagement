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

import com.alibaba.fastjson.JSONObject;
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
import com.mr.storemanagement.bean.InvDetailsBean;
import com.mr.storemanagement.dialog.InvSelectDialog;
import com.mr.storemanagement.eventbean.SaveInvEvent;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.GetInvCheckPresenter;
import com.mr.storemanagement.presenter.GetInvDetailsPresenter;
import com.mr.storemanagement.presenter.GetInvPresenter;
import com.mr.storemanagement.util.NullUtils;
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

    private List<InvCodeBean> mInvCodeBeans = new ArrayList<>();

    private InvSelectDialog asnSelectDialog;

//    private SiteBean currentSiteBean = null;

    private String mInvCode;

//    private SiteChooseHelper siteChooseHelper;

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

//        siteChooseHelper = new SiteChooseHelper(this, 2);
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
                mInvCode = tvSearchAsn.getText().toString();
            }
        });

        tvSearchAsn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    getAsn(false);
                }
                return false;
            }
        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (TextUtils.isEmpty(message)) {
                    if (tvSearchSite.isFocused()) {
                        tvSearchSite.setText(message);
                        tvSearchAsn.requestFocus();
                    } else {
                        tvSearchAsn.setText(message);
                    }
                }
            }
        });

        getAsn(false);
    }

    private void getAsn(boolean showSelectDialog) {
        GetInvPresenter presenter = new GetInvPresenter(this
                , new NetResultListener<List<InvCodeBean>>() {
            @Override
            public void loadSuccess(List<InvCodeBean> beans) {
                mInvCodeBeans.clear();
                if (beans != null) {
                    mInvCodeBeans.addAll(beans);
                }
                if (showSelectDialog) {
                    showAsnSelectDialog();
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

        if (TextUtils.isEmpty(mInvCode)) {
            ShowMsgDialogUtil.show(this, "请输入盘点单号");
            return;
        }

        presenter.check(mInvCode, AccountManger.getInstance().getUserCode());
    }

    @Subscribe
    public void onEventMainThread(SaveInvEvent event) {
        getAsn(false);
    }

    private void handlerInvCheckBack(InvCheckBackBean bean) {
        if (bean == null || !bean.HasAgv) {
            getInvDetails();
        } else {
            intoAGV(bean);
        }
    }

    private void showAsnSelectDialog() {
        if (asnSelectDialog == null || !asnSelectDialog.isShowing()) {
            asnSelectDialog = new InvSelectDialog(this, mInvCodeBeans);
            asnSelectDialog.setOrderSelectListener(new InvSelectDialog.OnOrderSelectListener() {
                @Override
                public void onSelect(InvCodeBean asnCodeBean) {
                    mInvCode = asnCodeBean.inventory_code;
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
        tvSearchAsn.setText(mInvCode);
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
                getAsn(true);
                break;
            case R.id.tv_next:
                checkInv();
                break;
        }
    }

    private void intoAGV(InvCheckBackBean bean) {
        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ToastUtils.show("站点信息不能为空");
            return;
        }
        if (TextUtils.isEmpty(mInvCode)) {
            ToastUtils.show("单号信息不能为空");
            return;
        }
        Intent intent = new Intent(this, InvSGVActivity.class);
        intent.putExtra(Constants.SITE_CODE_KEY, tvSearchSite.getText().toString());
        intent.putExtra(Constants.HAS_TASK_KEY, mInvCode);
        intent.putExtra(Constants.ASN_DATA_KEY, bean.HasTask);
        intent.putExtra(Constants.HAS_NON_AGV_KEY, bean.HasNonAgv);
        startActivity(intent);
    }

    private void getInvDetails() {
        GetInvDetailsPresenter presenter = new GetInvDetailsPresenter(this
                , new NetResultListener<List<InvDetailsBean>>() {
            @Override
            public void loadSuccess(List<InvDetailsBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    intoScanning(beans);
                } else {
                    ShowMsgDialogUtil.show(InventoryNoPutActivity.this
                            , "没有获取到盘点任务详情信息");
                }
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

        if (TextUtils.isEmpty(mInvCode)) {
            ShowMsgDialogUtil.show(this, "请输入盘点单号");
            return;
        }

        presenter.get(mInvCode);
    }

    private void intoScanning(List<InvDetailsBean> beans) {
        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ToastUtils.show("站点信息不能为空");
            return;
        }
        if (TextUtils.isEmpty(mInvCode)) {
            ToastUtils.show("单号信息不能为空");
            return;
        }
        Intent intent = new Intent(this, InventoryActivity.class);
        intent.putExtra(Constants.SITE_CODE_KEY, tvSearchSite.getText().toString());
        intent.putExtra(Constants.HAS_TASK_KEY, mInvCode);
        intent.putExtra(Constants.INV_DETAILS_DATA_KEY, JSONObject.toJSONString(beans));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}