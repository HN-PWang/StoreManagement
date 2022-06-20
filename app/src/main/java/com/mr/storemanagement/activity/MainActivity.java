package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.UserInfoBean;
import com.mr.storemanagement.manger.AccountManger;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUserName = findViewById(R.id.tv_user_name);
        findViewById(R.id.tv_init_stack).setOnClickListener(this);
        findViewById(R.id.tv_back_stack).setOnClickListener(this);
        findViewById(R.id.tv_out_stack).setOnClickListener(this);
        findViewById(R.id.tv_inventory).setOnClickListener(this);
        findViewById(R.id.tv_bind_stack).setOnClickListener(this);
        findViewById(R.id.tv_search_stack).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);

        tvUserName.setText("用户：" + AccountManger.getInstance().getUserCode());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_init_stack:
                //入库
                startActivity(new Intent(this, WarehousingActivity.class));
                break;
            case R.id.tv_back_stack:
                //回库  去扫描界面扫描后跳转
                StandardScanningActivity.actionIntent(this, WarehouseBackActivity.class);
                break;
            case R.id.tv_out_stack:
                //出库挑拣

                break;
            case R.id.tv_inventory:
                //盘点
                //TODO 暂时不做
                break;
            case R.id.tv_bind_stack:
                //绑定RFID
                startActivity(new Intent(this, WarehouseBindActivity.class));
                break;
            case R.id.tv_search_stack:
                //库位查询
                startActivity(new Intent(this, WarehouseSearchActivity.class));
                break;
            case R.id.tv_back:
                //退出
                AccountManger.getInstance().logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}