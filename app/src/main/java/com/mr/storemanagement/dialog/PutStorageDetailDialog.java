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
import com.mr.storemanagement.adapter.PutStorageDetailAdapter;
import com.mr.storemanagement.bean.PutStorageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/20
 * @email: 1929774468@qq.com
 * @description: 入库明细查看弹窗
 */
public class PutStorageDetailDialog extends Dialog implements View.OnClickListener {

    private TextView tvStackCount;

    private RecyclerView rvPutStorage;

    private PutStorageDetailAdapter storageDetailAdapter;

    private List<PutStorageBean> mDataList = new ArrayList<>();

    public PutStorageDetailDialog(@NonNull Context context) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.dialog_put_storage_detail_layout);
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

        storageDetailAdapter = new PutStorageDetailAdapter(context, mDataList);
        rvPutStorage.setLayoutManager(new LinearLayoutManager(context));
        rvPutStorage.setAdapter(storageDetailAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clear:
                dismiss();
                break;
        }
    }
}
