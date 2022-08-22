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
import com.mr.lib_base.widget.SMEditText;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.SerialNumAdapter;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.helper.CheckRfIdHelper;
import com.mr.storemanagement.util.NullUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 机件号扫描界面
 */
public class SerialNumScannerActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvCount;

    private SMEditText tvRdidRead;

    private RecyclerView rvSerialNum;

    private SerialNumAdapter serialNumAdapter;

    private List<String> mDataList = new ArrayList<>();

    private List<String> mCheckDataList;

    public boolean snCodeUnNeedCheck = false;

    private CheckRfIdHelper checkRfIdHelper;

    private List<String> scannerList = new ArrayList<>();

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

        checkRfIdHelper = new CheckRfIdHelper(this);

        snCodeUnNeedCheck = getIntent().getBooleanExtra(Constants.SN_CODE_UN_NEED_CHECK, false);

        String checkSns = getIntent().getStringExtra(Constants.SN_CODE_CHECK_DATA_KEY);
        checkSns = TextUtils.isEmpty(checkSns) ? "" : checkSns;
        mCheckDataList = JSONObject.parseArray(checkSns, String.class);
        if (mCheckDataList == null)
            mCheckDataList = new ArrayList<>();

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
                    tvRdidRead.setText("");
                }
                return false;
            }
        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                scannerList.clear();
                addCode(message);
                checkedRfId();
            }
        });

        setOnRfIdListener(new OnRfIdListener() {
            @Override
            public void onRFIdDataBack(String message) {
                scannerList.clear();
                addCode(message);
                checkedRfId();
            }
        });
    }

    private void addCode(String data) {
        if (!TextUtils.isEmpty(data)) {
            if (snCodeUnNeedCheck) {
                scannerList.add(data);
            } else {
                if (mCheckDataList.contains(data)) {
                    scannerList.add(data);
                }
            }
        }
    }

    @Override
    public void onRfIdReadComplete() {
        checkedRfId();
    }

    private void checkedRfId() {
        checkRfIdHelper.check(scannerList, new CheckRfIdHelper.OnCheckRfIdBackListener() {
            @Override
            public void onBack(List<String> backList) {
                if (snCodeUnNeedCheck) {
                    handlerAddAndShowWork(backList, false);
                } else {
                    handlerAddAndShowWork(backList, true);
//                    if (mCheckDataList.contains(backList)) {
//                        handlerAddAndShowWork(backList,true);
//                    }
                }
            }
        });
    }

    /**
     * @param backList
     * @param duplicateRemoval 去重
     */
    private void handlerAddAndShowWork(List<String> backList, boolean duplicateRemoval) {
        if (NullUtils.isEmpty(backList)) {
            for (String item : backList) {
                if (duplicateRemoval) {
                    if (!mDataList.contains(item))
                        mDataList.add(item);
                } else {
                    mDataList.add(item);
                }
            }
            setCount();
        }
    }

    private void setCount() {
        String str = "已扫描机件号[" + mDataList.size() + "]";
        tvCount.setText(str);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_rfid:
                scannerList.clear();
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