package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.MainMenuAdapter;
import com.mr.storemanagement.bean.MainMenuBean;
import com.mr.storemanagement.manger.AccountManger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvUserName;

    private RecyclerView rvMenu;

    private MainMenuAdapter menuAdapter;

    private List<MainMenuBean> menuBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUserName = findViewById(R.id.tv_user_name);
        rvMenu = findViewById(R.id.rv_menu);

        findViewById(R.id.tv_back).setOnClickListener(this);

        rvMenu.setLayoutManager(new GridLayoutManager(this, 2));
        menuAdapter = new MainMenuAdapter(this, menuBeanList);
        rvMenu.setAdapter(menuAdapter);

        menuAdapter.setOnMenuClickListener(new MainMenuAdapter.OnMenuClickListener() {
            @Override
            public void onClick(MainMenuBean item) {
                startActivity(new Intent(MainActivity.this, item.cls));
            }
        });

        buildMenu();
        menuAdapter.notifyDataSetChanged();

        tvUserName.setText("用户：" + AccountManger.getInstance().getUserCode());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                //退出
                AccountManger.getInstance().logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void buildMenu() {
        MainMenuBean bean1 = new MainMenuBean(WarehousingActivity.class, "入库扫描"
                ,R.drawable.ic_baseline_shopping_cart_24);

        MainMenuBean bean2 = new MainMenuBean(WarehouseBackActivity.class, "回库扫描"
                , R.drawable.ic_baseline_settings_backup_restore_24);

        MainMenuBean bean3 = new MainMenuBean(GetTaskActivity.class, "出库挑拣"
                , R.drawable.ic_baseline_add_shopping_cart_24);

        MainMenuBean bean4 = new MainMenuBean(InventoryNoPutActivity.class, "盘点"
                , R.drawable.ic_baseline_edit_24);

        MainMenuBean bean5 = new MainMenuBean(WarehouseBindActivity.class, "RfId绑定"
                , R.drawable.ic_baseline_loyalty_24);

        MainMenuBean bean6 = new MainMenuBean(WarehouseSearchActivity.class, "库存查询"
                , R.drawable.ic_baseline_search_24);

        MainMenuBean bean7 = new MainMenuBean(MergeContainerActivity.class, "下架扫描"
                , R.drawable.ic_baseline_input_24);

        menuBeanList.add(bean1);
        menuBeanList.add(bean2);
        menuBeanList.add(bean3);
        menuBeanList.add(bean4);
        menuBeanList.add(bean5);
        menuBeanList.add(bean6);
        menuBeanList.add(bean7);
    }
}