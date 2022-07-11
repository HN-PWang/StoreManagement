package com.mr.storemanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.StackAdapter;
import com.mr.storemanagement.bean.InvDetailsBean;
import com.mr.storemanagement.bean.StackBean;
import com.mr.storemanagement.presenter.GetInventoryListPresenter;

import java.util.ArrayList;
import java.util.List;

public class SearchStockDetailDialog extends Dialog implements View.OnClickListener
        , TextView.OnEditorActionListener {

    private EditText etSearch;

    private RecyclerView rvStack;

    private TextView tvStackCount;

    private StackAdapter stackAdapter;

    private List<StackBean> mDataList = new ArrayList<>();

    private OnSnClickListener snClickListener;

    private BaseActivity mActivity;

    public void setSnClickListener(OnSnClickListener snClickListener) {
        this.snClickListener = snClickListener;
    }

    public SearchStockDetailDialog(@NonNull Context context, BaseActivity activity) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.dialog_search_stock_detail_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        mActivity = activity;

        etSearch = findViewById(R.id.et_search);
        tvStackCount = findViewById(R.id.tv_stack_count);
        rvStack = findViewById(R.id.rv_put_storage);
        findViewById(R.id.tv_clear).setOnClickListener(this);
        etSearch.setOnEditorActionListener(this);
//        findViewById(R.id.tv_search).setOnClickListener(this);

        stackAdapter = new StackAdapter(context, mDataList);
        rvStack.setLayoutManager(new LinearLayoutManager(context));
        rvStack.setAdapter(stackAdapter);

        setStackCount();
    }

    private void setStackCount() {
        String strStack = "库存明细[" + mDataList.size() + "]";
        tvStackCount.setText(strStack);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                getData();
                break;
            case R.id.tv_clear:
                dismiss();
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
        GetInventoryListPresenter presenter = new GetInventoryListPresenter(null
                , new NetResultListener<List<StackBean>>() {
            @Override
            public void loadSuccess(List<StackBean> beans) {
                if (beans != null) {
                    mDataList.clear();
                    mDataList.addAll(beans);

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
                mActivity.showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                mActivity.dismissLoadingDialog();
            }
        });
        String etContent = etSearch.getText().toString();
        presenter.getData(etContent);
    }

    public interface OnSnClickListener {
        void OnSnClick(InvDetailsBean bean);
    }
}
