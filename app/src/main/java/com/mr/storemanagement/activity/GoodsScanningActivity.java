package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.StoreInfoBean;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.GetAsnCheckPresenter;
import com.mr.storemanagement.presenter.GetFeedBoxPresenter;
import com.mr.storemanagement.util.NullUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 入库扫描界面
 */
public class GoodsScanningActivity extends BaseScannerActivity {

    private TextView tvSite;
    private TextView tvOrder;

    private String site_code;
    private String asn_code;
    private String mItemCode;

    private List<StoreInfoBean> storeInfoBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_scanning);

        site_code = getIntent().getStringExtra("site_key");
        asn_code = getIntent().getStringExtra("ans_key");

        tvSite = findViewById(R.id.tv_search_site);
        tvOrder = findViewById(R.id.et_order_no);

        tvSite.setText(site_code);
        tvOrder.setText(asn_code);

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                mItemCode = message;
                getFeedBox();
            }
        });

        checkAsn();
    }

    private void checkAsn() {
        GetAsnCheckPresenter presenter = new GetAsnCheckPresenter(this
                , new NetResultListener<List<StoreInfoBean>>() {
            @Override
            public void loadSuccess(List<StoreInfoBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    storeInfoBeans.clear();
                    storeInfoBeans.addAll(beans);
                }
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
        presenter.check(asn_code);
    }

    private void getFeedBox() {
        GetFeedBoxPresenter presenter = new GetFeedBoxPresenter(this
                , new NetResultListener<String>() {
            @Override
            public void loadSuccess(String s) {

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


        presenter.getFeedBox(site_code, asn_code, mItemCode, AccountManger.getInstance().getUserCode());
    }
}