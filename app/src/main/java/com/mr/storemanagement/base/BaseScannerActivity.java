package com.mr.storemanagement.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.pda.scanner.ScanReader;
import com.pda.scanner.Scanner;

import java.nio.charset.Charset;

/**
 * @auther: pengwang
 * @date: 2022/6/19
 * @description:
 */
public abstract class BaseScannerActivity extends BaseActivity {

    //扫描条码服务广播
    //Scanning barcode service broadcast.
    public static final String SCN_CUST_ACTION_SCODE = "com.android.server.scannerservice.broadcast";
    //条码扫描数据广播
    //Barcode scanning data broadcast.
    public static final String SCN_CUST_EX_SCODE = "scannerdata";

    //ScanReader object
    private Scanner scanReader = ScanReader.getScannerInstance();

    //ToneGenerator beep
    private ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
            ToneGenerator.MAX_VOLUME);

    public static final int MSG_LOAD_WHAT = 1;
    public static final int MSG_HINT_WHAT = 2;
    public static final int MSG_SCANNER_WHAT = 3;

    private boolean isBusy = false;

    private BroadcastReceiver mScanDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SCN_CUST_ACTION_SCODE.equals(intent.getAction())) {
                try {
                    String result = intent.getStringExtra(SCN_CUST_EX_SCODE);
                    sandMessage(MSG_SCANNER_WHAT, result);
//                    scannerDataDeCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_LOAD_WHAT:
                    showLoadingDialog("读取中", false);
                    break;
                case MSG_HINT_WHAT:
                    dismissLoadingDialog();
                    break;
                case MSG_SCANNER_WHAT:
                    String scanner = "";
                    if (msg.obj != null) {
                        scanner = (String) msg.obj;
                    }
                    onScannerDataBack(scanner);
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState
            , @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }


    //初始化扫描模块
    protected void initScanner() {
        if (!scanInit()) { // Failed to open the module power supply
            ToastUtils.show("扫描打开失败");
        }
    }

    private boolean scanInit() {
        if (null == scanReader) {
            return false;
        }
        return scanReader.open(this);
    }

    private void sandMessage(int what, String msg) {
        Message message = new Message();
        message.what = what;
        message.obj = msg;
        handler.sendMessage(message);
    }

    /**
     * 读取解码
     */
    protected void scannerDataDeCode() {
        if (isBusy) {
            return;
        }
        isBusy = true;
        new Thread() {
            @Override
            public void run() {
                sandMessage(MSG_LOAD_WHAT, getString(R.string.str_please_waitting));
                byte[] id = scanReader.decode();
                String idString;
                if (id != null) {
                    String utf8 = new String(id, Charset.forName("utf8"));
                    if (utf8.contains("\ufffd")) {
                        utf8 = new String(id, Charset.forName("gbk"));
                    }
                    idString = utf8 + "\n";
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                } else {
                    idString = null;
                }
                sandMessage(MSG_SCANNER_WHAT, idString);
                sandMessage(MSG_HINT_WHAT, null);
                isBusy = false;
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(SCN_CUST_ACTION_SCODE);

        registerReceiver(mScanDataReceiver, intentFilter);

        initScanner();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScanDataReceiver);

        scanReader.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        scanReader.close();
    }

    public abstract void onScannerDataBack(String codeInfo);

}
