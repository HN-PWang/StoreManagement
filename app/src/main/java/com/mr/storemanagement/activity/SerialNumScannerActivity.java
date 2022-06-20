package com.mr.storemanagement.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.SerialNumAdapter;
import com.mr.storemanagement.base.BaseScannerActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 序列号扫描界面
 */
public class SerialNumScannerActivity extends BaseScannerActivity implements View.OnClickListener {

    public static final String SERIAL_NUM_DATA_KEY = "serial_num_data_key";

    private TextView tvCount;

    private RecyclerView rvSerialNum;

    private SerialNumAdapter serialNumAdapter;

    private List<String> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_num_scanner);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);

        tvCount = findViewById(R.id.tv_count);
        rvSerialNum = findViewById(R.id.rv_serial_num);

        findViewById(R.id.tv_rdid_read).setOnClickListener(this);
        findViewById(R.id.tv_clear).setOnClickListener(this);

        serialNumAdapter = new SerialNumAdapter(this, mDataList);
        rvSerialNum.setLayoutManager(new LinearLayoutManager(this));
        rvSerialNum.setAdapter(serialNumAdapter);

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (!TextUtils.isEmpty(message)) {
                    mDataList.add(message);
                    serialNumAdapter.notifyDataSetChanged();
                    setCount();
                }
            }
        });

        setOnRfIdListener(new OnRfIdListener() {
            @Override
            public void onRFIdDataBack(String message) {
                if (!TextUtils.isEmpty(message)) {
                    mDataList.add(message);
                    serialNumAdapter.notifyDataSetChanged();
                    setCount();
                }
            }
        });
    }

    private void setCount() {
        String str = "已扫描序列号[" + mDataList.size() + "]";
        tvCount.setText(str);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_rdid_read:
                readMactchData();
                break;
            case R.id.tv_clear:
                Intent intent = new Intent();
                intent.putExtra(SERIAL_NUM_DATA_KEY, JSONObject.toJSONString(mDataList));
                setResult(Activity.RESULT_OK, intent);
                break;
        }
    }

    public static List<String> getSerialList(String backData) {
        List<String> list = JSONObject.parseArray(backData, String.class);
        return list;
    }

}