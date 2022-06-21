package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.GetTaskAdapter;
import com.mr.storemanagement.bean.GetTaskBean;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.helper.SiteChooseHelper;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.GetTaskPresenter;
import com.mr.storemanagement.util.NullUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 领取任务界面
 */
public class GetTaskActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvSearchSite;
    private RecyclerView rvTask;

    private GetTaskAdapter taskAdapter;

    private List<GetTaskBean> mDataList = new ArrayList<>();

    private SiteBean currentSiteBean = null;

    private SiteChooseHelper siteChooseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_task);

        tvSearchSite = findViewById(R.id.tv_search_site);
        rvTask = findViewById(R.id.rv_task);

        findViewById(R.id.tv_search_site).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_get_task).setOnClickListener(this);
        findViewById(R.id.tv_choose).setOnClickListener(this);

        rvTask.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new GetTaskAdapter(this, mDataList);
        rvTask.setAdapter(taskAdapter);

        siteChooseHelper = new SiteChooseHelper(this);
        siteChooseHelper.setSiteClickListener(new SiteChooseHelper.OnSiteEventListener() {
            @Override
            public void onClick(SiteBean site) {
                currentSiteBean = site;
                setSiteInfo();
                getTask();
            }

            @Override
            public void onFirst(SiteBean site) {
                currentSiteBean = site;
                setSiteInfo();
                getTask();
            }
        });
    }

    private void setSiteInfo() {
        if (currentSiteBean != null) {
            tvSearchSite.setText(currentSiteBean.site_code);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_site:
                siteChooseHelper.selectSite();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_get_task:
                getTask();
                break;
            case R.id.tv_choose:

                break;
        }
    }

    private void getTask() {
        GetTaskPresenter presenter = new GetTaskPresenter(this
                , new NetResultListener<List<GetTaskBean>>() {
            @Override
            public void loadSuccess(List<GetTaskBean> taskBeans) {
                if (NullUtils.isNotEmpty(taskBeans)) {
                    mDataList.clear();
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

        if (currentSiteBean == null) {
            ToastUtils.show("请选择站点");
            return;
        }

        presenter.getTask(currentSiteBean.site_code, AccountManger.getInstance().getUserCode());
    }
}