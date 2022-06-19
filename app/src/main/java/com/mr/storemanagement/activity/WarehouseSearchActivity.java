package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.StackAdapter;
import com.mr.storemanagement.bean.StackBean;
import com.mr.storemanagement.presenter.GetInventoryListPresenter;

import java.util.ArrayList;
import java.util.List;

public class WarehouseSearchActivity extends BaseActivity
        implements TextView.OnEditorActionListener, View.OnClickListener {

    private EditText etSearch;

    private RecyclerView rvStack;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView tvStackCount;

    private StackAdapter stackAdapter;

    private List<StackBean> stackBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_search);

        etSearch = findViewById(R.id.et_search);
        rvStack = findViewById(R.id.rv_stack);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvStackCount = findViewById(R.id.tv_stack_count);

        stackAdapter = new StackAdapter(this, stackBeans);
        rvStack.setLayoutManager(new LinearLayoutManager(this));
        rvStack.setAdapter(stackAdapter);

        etSearch.setOnEditorActionListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        setStackCount();
    }

    private void setStackCount() {
        String strStack = "库存明细[" + stackBeans.size() + "]";
        tvStackCount.setText(strStack);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                getData();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            getData();
        }
        return false;
    }

    private void getData() {
        GetInventoryListPresenter presenter = new GetInventoryListPresenter(this
                , new NetResultListener<List<StackBean>>() {
            @Override
            public void loadSuccess(List<StackBean> beans) {
                if (beans != null) {
                    stackBeans.clear();
                    stackBeans.addAll(beans);

                    stackAdapter.notifyDataSetChanged();
                    setStackCount();
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void finishLoading() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        String etContent = etSearch.getText().toString();
        presenter.getData(etContent);
    }

}