package com.mr.storemanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.InvDetailAdapter;
import com.mr.storemanagement.adapter.PutStorageDetailAdapter;
import com.mr.storemanagement.bean.AsnDetailBean;
import com.mr.storemanagement.bean.InvDetailsBean;
import com.mr.storemanagement.util.DataUtil;
import com.mr.storemanagement.util.NullUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/20
 * @email: 1929774468@qq.com
 * @description: 盘点详情查看弹窗
 */
public class InvDetailDialog extends Dialog implements View.OnClickListener {

    private TextView tvStackCount;

    private RecyclerView rvPutStorage;

    private InvDetailAdapter detailAdapter;

    private List<InvDetailsBean> mDataList = new ArrayList<>();

    private OnSnClickListener snClickListener;

    public void setSnClickListener(OnSnClickListener snClickListener) {
        this.snClickListener = snClickListener;
    }

    public InvDetailDialog(@NonNull Context context, List<InvDetailsBean> dataList) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.dialog_inv_detail_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        tvStackCount = findViewById(R.id.tv_stack_count);
        rvPutStorage = findViewById(R.id.rv_put_storage);
        findViewById(R.id.tv_clear).setOnClickListener(this);

        if (NullUtils.isNotEmpty(dataList))
            mDataList.addAll(dataList);

        detailAdapter = new InvDetailAdapter(context, mDataList);
        rvPutStorage.setLayoutManager(new LinearLayoutManager(context));
        rvPutStorage.setAdapter(detailAdapter);

        detailAdapter.setCodeClickListener(new InvDetailAdapter.OnSerialCodeClickListener() {
            @Override
            public void onClick(InvDetailsBean bean) {
                if (snClickListener != null) {
                    snClickListener.OnSnClick(bean);
                }
            }
        });

        String countStr = "入库明细：[" + getFinishQuantity() + "/" + mDataList.size() + "]";
        tvStackCount.setText(countStr);
    }

    private int getFinishQuantity() {
        int finishQuantity = 0;
        if (NullUtils.isNotEmpty(mDataList)) {
            for (InvDetailsBean bean : mDataList) {
                int q = DataUtil.getInt(bean.available_qty);
                int fq = DataUtil.getInt(bean.check_qty);
                if (q != 0 && q == fq) {
                    finishQuantity++;
                }
            }
        }
        return finishQuantity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clear:
                dismiss();
                break;
        }
    }

    public interface OnSnClickListener {
        void OnSnClick(InvDetailsBean bean);
    }
}
