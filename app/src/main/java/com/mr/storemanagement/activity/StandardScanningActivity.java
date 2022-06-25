package com.mr.storemanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mr.lib_base.AfterTextChangedListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;

/**
 * 通用的扫码界面
 */
public class StandardScanningActivity extends BaseScannerActivity implements View.OnClickListener {

    private EditText tvCode;

    private static Class<?> mNextAct;

    private static String mHint;

    private String mScanningCode;

    public static void actionIntent(Context context, Class<?> nextAct, String hint) {
        Intent intent = new Intent(context, StandardScanningActivity.class);
        context.startActivity(intent);

        mNextAct = nextAct;
        mHint = hint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_scanning);

        tvCode = findViewById(R.id.tv_code);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_station).setOnClickListener(this);

        tvCode.setHint(mHint);

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                mScanningCode = message;

                tvCode.setText(mScanningCode);

                if (!TextUtils.isEmpty(mScanningCode)) {
                    toNextActivity();
                }
            }
        });

        tvCode.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                mScanningCode = tvCode.getText().toString();
            }
        });

        tvCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    toNextActivity();
                }
                return false;
            }
        });
    }

    private void toNextActivity() {
        if (TextUtils.isEmpty(mScanningCode)) {
            ToastUtils.show("请输入条码");
            return;
        }

        if (mNextAct != null) {
            Intent intent = new Intent(this, mNextAct);
            intent.putExtra(Constants.SCANNER_DATA_KEY, mScanningCode);

            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_station:
                toNextActivity();
                break;
        }
    }
}