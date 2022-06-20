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
import com.mr.storemanagement.adapter.CheckSnAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/20
 * @email: 1929774468@qq.com
 * @description:
 */
public class CheckSnDialog extends Dialog implements View.OnClickListener {

    private TextView tvSnCount;
    private RecyclerView rvSn;

    private CheckSnAdapter checkSnAdapter;

    private List<String> mDataList = new ArrayList<>();

    private String mNo;

    public CheckSnDialog(@NonNull Context context, String no, List<String> dataList) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.dialog_check_sn_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        tvSnCount = findViewById(R.id.tv_sn_count);
        findViewById(R.id.tv_clear).setOnClickListener(this);
        rvSn = findViewById(R.id.rv_sn);
        checkSnAdapter = new CheckSnAdapter(context, mDataList);
        rvSn.setLayoutManager(new LinearLayoutManager(context));
        rvSn.setAdapter(checkSnAdapter);

        mDataList.addAll(dataList);
        checkSnAdapter.notifyDataSetChanged();

        mNo = no;

        setSnCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear:
                dismiss();
                break;
        }
    }

    private void setSnCount() {
        tvSnCount.setText(mNo + "[" + mDataList.size() + "]");
    }
}
