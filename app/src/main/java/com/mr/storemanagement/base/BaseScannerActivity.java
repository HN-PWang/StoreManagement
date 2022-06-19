package com.mr.storemanagement.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hopeland.pda.rfid.uhf.UHFReader;
import com.hopeland.port.Adapt;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.pda.rfid.EPCModel;
import com.pda.rfid.IAsynchronousMessage;
import com.pda.rfid.uhf.UHF;
import com.pda.scanner.ScanReader;
import com.pda.scanner.Scanner;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @auther: pengwang
 * @date: 2022/6/19
 * @description:
 */
public abstract class BaseScannerActivity extends BaseActivity implements IAsynchronousMessage {

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

    public static UHF CLReader = UHFReader.getUHFInstance();

    public static final int MSG_LOAD_WHAT = 1;
    public static final int MSG_HINT_WHAT = 2;
    public static final int MSG_SCANNER_WHAT = 3;
    public static final int MSG_RF_ID_WHAT = 4;

    private boolean isScannerBusy = false;

    private boolean isRfIdReading = false;

    //Rf模块开启
    private boolean isFfModelOpen = false;
    public static int low_power_soc = 10;
    static int _NowAntennaNo = 1; // 读写器天线编号
    static int _UpDataTime = 0; // 重复标签上传时间，控制标签上传速度不要太快
    static int _Max_Power = 30; // 读写器最大发射功率
    static int _Min_Power = 0; // 读写器最小发射功率

    //备用电源开启
    private boolean usingBackBattery = false;

    private IAsynchronousMessage log = null;

    private OnScannerListener mOnScannerListener;

    private OnRfIdListener mOnRfIdListener;

    protected void setOnScannerListener(OnScannerListener scannerListener) {
        this.mOnScannerListener = scannerListener;
    }

    protected void setOnRfIdListener(OnRfIdListener rfIdListener) {
        this.mOnRfIdListener = rfIdListener;
    }

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
                    if (mOnScannerListener != null) {
                        mOnScannerListener.onScannerDataBack(scanner);
                    }
                    break;
                case MSG_RF_ID_WHAT:
                    String data = "";
                    if (msg.obj != null) {
                        data = (String) msg.obj;
                    }
                    if (mOnRfIdListener != null) {
                        mOnRfIdListener.onRFIdDataBack(data);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState
            , @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    /**
     * 初始化扫描模块
     */
    protected void initScanner() {
        if (mOnScannerListener != null) {
            boolean opened = false;
            if (null != scanReader) {
                opened = scanReader.open(this);
            }
            if (!opened) {
                ToastUtils.show("扫描打开失败");
            }
        }
    }

    /**
     * 清空扫码模块
     */
    protected void closeScanner() {
        if (mOnScannerListener != null) {
            scanReader.close();
        }
    }

    /**
     * 初始化Rf模块
     */
    protected void initRf() {
        if (mOnRfIdListener != null) {
            usingBackBattery = canUsingBackBattery();
            log = this;
            if (!UHF_Init(usingBackBattery, log)) { // 打开模块电源失败
                ToastUtils.show("打开电源模块失败");
            } else {
                try {
                    UHF_GetReaderProperty(); // 获得读写器的能力
//                    _NowReadParam = _NowAntennaNo + "|1";
                    Thread.sleep(20);
                    CLReader.Stop(); // 停止指令
                    Thread.sleep(20);
//                    super.UHF_SetTagUpdateParam(); // 设置标签重复上传时间为20ms
                } catch (Exception ee) {
                }
            }
        }
    }

    private boolean UHF_Init(boolean usingBackupPower, IAsynchronousMessage log) {
        boolean rt = false;
        try {
            if (!isFfModelOpen) {
                int ret = UHFReader._Config.OpenConnect(usingBackupPower, log);
                if (ret == 0) {
                    rt = true;
                    isFfModelOpen = true;
                }
                Thread.sleep(500);
            } else {
                rt = true;
            }
        } catch (Exception ex) {
            Log.d("debug", "On the UHF electric abnormal:" + ex.getMessage());
        }
        return rt;
    }

    //Determine the backup power
    protected boolean canUsingBackBattery() {
        if (Adapt.getPowermanagerInstance().getBackupPowerSOC() < low_power_soc) {
            return false;
        }
        return true;
    }

    protected void UHF_GetReaderProperty() {
        //String propertyStr = CLReader.GetReaderProperty();
        String propertyStr = UHFReader._Config.GetReaderProperty();
        //Log.d("Debug", "Get Reader Property:" + propertyStr);
        String[] propertyArr = propertyStr.split("\\|");
        HashMap<Integer, Integer> hm_Power = new HashMap<Integer, Integer>() {
            {
                put(1, 1);
                put(2, 3);
                put(3, 7);
                put(4, 15);
            }
        };
        if (propertyArr.length > 3) {
            try {
                _Min_Power = Integer.parseInt(propertyArr[0]);
                _Max_Power = Integer.parseInt(propertyArr[1]);
                int powerIndex = Integer.parseInt(propertyArr[2]);
                _NowAntennaNo = hm_Power.get(powerIndex);
            } catch (Exception ex) {
                Log.d("Debug", "Get Reader Property failure and conversion failed!");
            }
        } else {
            Log.d("Debug", "Get Reader Property failure");
        }
    }

    protected boolean UHF_CheckReadResult(int retval) {
        if (99 == retval) {
            ToastUtils.show(R.string.uhf_read_power_over);
            finish();
            return false;
        }
        return true;
    }

    /**
     * 清理Rf模块
     */
    protected void closeRf() {
        if (mOnRfIdListener != null) {
            if (isFfModelOpen) {
                UHFReader._Config.CloseConnect();
                isFfModelOpen = false;
            }
        }
    }

    @Override
    public void OutPutEPC(EPCModel epcModel) {
        sandMessage(MSG_RF_ID_WHAT, epcModel._TID);
    }

    private void sandMessage(int what, String msg) {
        Message message = new Message();
        message.what = what;
        message.obj = msg;
        handler.sendMessage(message);
    }

    /**
     * 读取扫码信息解码
     */
    protected void scannerDataDeCode() {
        if (isScannerBusy) {
            return;
        }
        isScannerBusy = true;
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
                isScannerBusy = false;
            }
        }.start();
    }

    /**
     * 读取rfid信息
     */
    protected void readMactchData() {
        sandMessage(MSG_LOAD_WHAT, getString(R.string.str_please_waitting));
        isRfIdReading = true;
        String rt = "";
        int ret = -1;
        if (Constants._IsCommand6Cor6B.equals("6C")) {// 6C
            ret = UHFReader._Tag6C.GetEPC_TID(_NowAntennaNo, 1);
        } else { // 6B
            rt = CLReader.Get6B(_NowAntennaNo + "|1" + "|0");
        }

        int retval = CLReader.GetReturnData(rt);
        if (!UHF_CheckReadResult(retval)) {
            CLReader.Stop();
            isRfIdReading = false;
            return;
        }
        //After reading the tag for 2 seconds, stop
        new Thread() {
            public void run() {
                int wait = 50;
                int timeout = 2000;
                sandMessage(MSG_LOAD_WHAT, getString(R.string.str_please_waitting));
                try {
                    for (int i = 0; i < timeout; i += wait) {
                        Thread.sleep(wait);
                        if (!isRfIdReading) {
                            break;
                        }
                    }
                    CLReader.Stop(); //You must stop reading or the write tag will fail
                } catch (Exception e) {
                }
                sandMessage(MSG_HINT_WHAT, null);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SCN_CUST_ACTION_SCODE);
        intentFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        intentFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        intentFilter.addAction("com.rscja.scanner.action.scanner.RFID");

        registerReceiver(mScanDataReceiver, intentFilter);

        initScanner();

        initRf();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScanDataReceiver);

        closeScanner();

        closeRf();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        closeScanner();

        closeRf();
    }

    /**
     * 扫码信息回调
     */
    public interface OnScannerListener {
        void onScannerDataBack(String message);
    }

    /**
     * rfid信息回调
     */
    public interface OnRfIdListener {
        void onRFIdDataBack(String message);
    }

}
