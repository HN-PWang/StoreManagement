package com.mr.storemanagement.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.hopeland.ui.BaseActivity;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.R;
import com.pda.scanner.ScanReader;
import com.pda.scanner.Scanner;

import java.nio.charset.Charset;

public class StandardScanningActivity extends BaseActivity {

    private TextView tvCode;

    static final int MSG_UPDATE_ID = MSG_USER_BEG + 1;

    boolean busy = false;

    //ScanReader object
    private Scanner scanReader = ScanReader.getScannerInstance();

    //ToneGenerator beep
    private ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
            ToneGenerator.MAX_VOLUME);

    private static Class<?> mNextAct;

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
    }


    @Override
    public void msgProcess(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_ID:
                String idString = (String) msg.obj;
                tvCode.setText(idString);
                break;
            default:
                super.msgProcess(msg);
                break;
        }
    }

    protected void DeCode() {
        if (busy) {
            showTip(getString(R.string.str_busy));
            return;
        }
        busy = true;
        new Thread() {
            @Override
            public void run() {
                sendMessage(MSG_SHOW_WAIT, getString(R.string.str_please_waitting));
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
                    showTip(getString(R.string.str_faild));
                }
                sendMessage(MSG_UPDATE_ID, idString);
                sendMessage(MSG_HIDE_WAIT, null);
                busy = false;
            }
        }.start();
    }

    //back
    public void back(View v) {
        scanReader.close();
        finish();
    }

    //Init
    protected void initScanner() {
        if (!scanInit()) { // Failed to open the module power supply
            showMsg("打开错误",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
        }
    }

    //Turn on the module power
    private boolean scanInit() {
        if (null == scanReader) {
            return false;
        }

        return scanReader.open(getApplicationContext());
    }

    //Turn off module power
    private void scanDispose() {
        scanReader.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanDispose();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanDispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initScanner();
        DeCode();
    }
}