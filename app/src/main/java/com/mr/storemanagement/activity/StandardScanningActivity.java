package com.mr.storemanagement.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.pda.rfid.EPCModel;
import com.pda.scanner.ScanReader;
import com.pda.scanner.Scanner;

import java.nio.charset.Charset;

/**
 * 通用的扫码界面
 */
public class StandardScanningActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvCode;

    private static Class<?> mNextAct;

    private String mScanningCode;

    public static void actionIntent(Context context, Class<?> nextAct) {
        Intent intent = new Intent(context, StandardScanningActivity.class);
        context.startActivity(intent);

        mNextAct = nextAct;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_scanning);

        tvCode = findViewById(R.id.tv_code);

        findViewById(R.id.tv_back).setOnClickListener(this);

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
    }

    private void toNextActivity() {
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
        }
    }
}