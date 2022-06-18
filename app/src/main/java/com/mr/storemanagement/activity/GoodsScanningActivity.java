package com.mr.storemanagement.activity;

import android.os.Bundle;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.StoreInfoBean;
import com.mr.storemanagement.presenter.GetAsnCheckPresenter;

import java.util.List;

/**
 * 入库扫描界面
 */
public class GoodsScanningActivity extends BaseActivity {

    public String site_code;
    public String asn_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_scanning);

        site_code = getIntent().getStringExtra("site_key");
        asn_code = getIntent().getStringExtra("ans_key");

        checkAsn();
    }

    private void checkAsn() {
        GetAsnCheckPresenter presenter = new GetAsnCheckPresenter(this
                , new NetResultListener<List<StoreInfoBean>>() {
            @Override
            public void loadSuccess(List<StoreInfoBean> storeInfoBeans) {

            }

            @Override
            public void loadFailure(SMException exception) {

            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {

            }

            @Override
            public void finishLoading() {

            }
        });
        presenter.check(asn_code);
    }
}