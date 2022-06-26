package com.mr.storemanagement.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.SerialNumAdapter;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.util.NullUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 序列号扫描界面
 */
public class SerialNumScannerActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvCount;

    private EditText tvRdidRead;

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
        tvRdidRead = findViewById(R.id.tv_rdid_read);

        findViewById(R.id.iv_rfid).setOnClickListener(this);
        findViewById(R.id.tv_clear).setOnClickListener(this);

        String sns = getIntent().getStringExtra(Constants.SN_CODE_DATA_KEY);
        sns = TextUtils.isEmpty(sns) ? "" : sns;
        List<String> list = JSONObject.parseArray(sns, String.class);
        if (NullUtils.isNotEmpty(list))
            mDataList.addAll(list);

        serialNumAdapter = new SerialNumAdapter(this, mDataList);
        rvSerialNum.setLayoutManager(new LinearLayoutManager(this));
        rvSerialNum.setAdapter(serialNumAdapter);

        tvRdidRead.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    addCode(tvRdidRead.getText().toString());
                }
                return false;
            }
        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                addCode(message);
            }
        });

        setOnRfIdListener(new OnRfIdListener() {
            @Override
            public void onRFIdDataBack(String message) {
                addCode(message);
            }
        });
    }

    private void addCode(String data) {
        if (!TextUtils.isEmpty(data) && !mDataList.contains(data)) {
            mDataList.add(data);
            serialNumAdapter.notifyDataSetChanged();
            setCount();
        }
    }

    private void setCount() {
        String str = "已扫描序列号[" + mDataList.size() + "]";
        tvCount.setText(str);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_rfid:
                readMactchData();
                break;
            case R.id.tv_clear:
                Intent intent = new Intent();
                intent.putExtra(Constants.SN_CODE_DATA_KEY, JSONObject.toJSONString(mDataList));
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }
    }

}