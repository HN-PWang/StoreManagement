package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.SMLog;
import com.mr.storemanagement.presenter.LoginPresenter;
import com.mr.storemanagement.R;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_init_stack).setOnClickListener(this);
        findViewById(R.id.tv_back_stack).setOnClickListener(this);
        findViewById(R.id.tv_out_stack).setOnClickListener(this);
        findViewById(R.id.tv_inventory).setOnClickListener(this);
        findViewById(R.id.tv_bind_stack).setOnClickListener(this);
        findViewById(R.id.tv_search_stack).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_init_stack:
                startActivity(new Intent(this, WarehousingActivity.class));
                break;
            case R.id.tv_back_stack:
                StandardScanningActivity.actionIntent(this,WarehouseBackActivity.class);
//                startActivity(new Intent(this, WarehouseBackActivity.class));
                break;
            case R.id.tv_out_stack:

                break;
            case R.id.tv_inventory:

                break;
            case R.id.tv_bind_stack:

                break;
            case R.id.tv_search_stack:

                break;
            case R.id.tv_back:

                break;
        }
    }
}