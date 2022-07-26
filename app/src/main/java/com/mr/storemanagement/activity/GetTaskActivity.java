package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.GetTaskAdapter;
import com.mr.storemanagement.bean.GetTaskBean;
import com.mr.storemanagement.eventbean.OutStockEvent;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.GetPickWorkPresenter;
import com.mr.storemanagement.presenter.GetTaskPresenter;
import com.mr.storemanagement.util.NullUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 领取任务界面
 */
public class GetTaskActivity extends BaseActivity implements View.OnClickListener {

    private EditText tvSearchSite;
    private RecyclerView rvTask;

    private GetTaskAdapter taskAdapter;

    private List<GetTaskBean> mDataList = new ArrayList<>();

//    private SiteBean currentSiteBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_task);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        tvSearchSite = findViewById(R.id.tv_search_site);
        rvTask = findViewById(R.id.rv_task);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_get_task).setOnClickListener(this);
        findViewById(R.id.tv_choose).setOnClickListener(this);

        rvTask.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new GetTaskAdapter(this, mDataList);
        rvTask.setAdapter(taskAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_get_task:
                getTask();
                break;
            case R.id.tv_choose:
                chooseWork();
                break;
        }
    }

    private void chooseWork() {
        if (NullUtils.isEmpty(mDataList)) {
            ToastUtils.show("没有商品");
            return;
        }

        Intent intent = new Intent(this, PickingFeedBoxScannerActivity.class);
        intent.putExtra(Constants.SITE_CODE_KEY, tvSearchSite.getText().toString());
        intent.putExtra(Constants.TASK_DATA_KEY, JSONObject.toJSONString(mDataList));
        startActivity(intent);
    }

    private void getPickWork() {
        GetPickWorkPresenter presenter = new GetPickWorkPresenter(this
                , new NetResultListener<List<GetTaskBean>>() {
            @Override
            public void loadSuccess(List<GetTaskBean> taskBeans) {
                mDataList.clear();
                if (NullUtils.isNotEmpty(taskBeans)) {
                    mDataList.addAll(taskBeans);
                    taskAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.show("没有获取到任务信息");
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

        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ToastUtils.show("站点信息不能为空");
            return;
        }

        presenter.getPickWork(tvSearchSite.getText().toString());
    }

    private void getTask() {
        GetTaskPresenter presenter = new GetTaskPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                getPickWork();
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

        if (TextUtils.isEmpty(tvSearchSite.getText())) {
            ToastUtils.show("请输入站点");
            return;
        }

        presenter.getTask(tvSearchSite.getText().toString(), AccountManger.getInstance().getUserCode());
    }

    @Subscribe
    public void onEventMainThread(OutStockEvent event) {
        getTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}