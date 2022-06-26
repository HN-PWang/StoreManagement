package com.mr.storemanagement.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.AfterTextChangedListener;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.AsnDetailBean;
import com.mr.storemanagement.bean.FeedBoxBean;
import com.mr.storemanagement.bean.StoreInfoBean;
import com.mr.storemanagement.dialog.CheckSnDialog;
import com.mr.storemanagement.dialog.ConfirmDialog;
import com.mr.storemanagement.dialog.PutStorageDetailDialog;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.AsnCloseOrderPresenter;
import com.mr.storemanagement.presenter.AsnSaveDetailPresenter;
import com.mr.storemanagement.presenter.GetAsnCheckPresenter;
import com.mr.storemanagement.presenter.GetAsnDetailPresenter;
import com.mr.storemanagement.presenter.GetAsnDetailSnListPresenter;
import com.mr.storemanagement.presenter.GetFeedBoxPresenter;
import com.mr.storemanagement.util.DataUtil;
import com.mr.storemanagement.util.NullUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 入库扫描界面
 */
public class ScannerPutStockActivity extends BaseScannerActivity implements View.OnClickListener
        , View.OnFocusChangeListener {

    public static final int REQUEST_SERIAL_CODE = 101;

    private TextView tvSite;
    private TextView tvOrder;

    private EditText etCxNo;//册序号
    private TextView tvCalled;//已呼叫的料箱标签
    private EditText etFeedBoxNo;//料箱
    private EditText tvScanSerial;//扫描料箱标记
    private TextView tvScanSerialTag;//扫描料箱标记
    private TextView etCount;//数量
    private TextView tvCollectedCount;//待收数量

    private PutStorageDetailDialog mPutStorageDetailDialog;

    private CheckSnDialog mCheckSnDialog;

    private ConfirmDialog mConfirmDialog;

    private String site_code;
    private String asn_code;

    private StoreInfoBean currentStore;

    private String mContainerCode;

    private List<String> snCodeList = new ArrayList<>();

    private List<StoreInfoBean> storeInfoBeans = new ArrayList<>();

    private int mScannerInitiator = 1; //1:测序号 2:料箱号 3:序列号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_put_stock);

        site_code = getIntent().getStringExtra("site_key");
        asn_code = getIntent().getStringExtra("ans_key");

        tvSite = findViewById(R.id.tv_search_site);
        tvOrder = findViewById(R.id.et_order_no);
        etCxNo = findViewById(R.id.et_cx_no);
        tvCalled = findViewById(R.id.tv_called);
        etFeedBoxNo = findViewById(R.id.et_feed_box_no);
        tvScanSerial = findViewById(R.id.tv_scan_serial);
        tvScanSerialTag = findViewById(R.id.tv_scan_serial_tag);
        etCount = findViewById(R.id.et_count);
        tvCollectedCount = findViewById(R.id.tv_collected_count);

        etCxNo.setOnFocusChangeListener(this);
        etFeedBoxNo.setOnFocusChangeListener(this);
        tvScanSerial.setOnFocusChangeListener(this);

        findViewById(R.id.tv_scan_serial).setOnClickListener(this);
        findViewById(R.id.tv_detail_list).setOnClickListener(this);
        findViewById(R.id.tv_complete).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);

        etCxNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    String text = textView.getText().toString().trim();
                    checkScannerCode(text);
                }
                return false;
            }
        });

        etFeedBoxNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    String text = textView.getText().toString().trim();
                }
                return false;
            }
        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (TextUtils.isEmpty(message))
                    return;

                if (mScannerInitiator == 1) {
                    checkScannerCode(message);
                } else if (mScannerInitiator == 2) {
                    mScannerInitiator = 3;
                    setInputViewState();
                } else if (mScannerInitiator == 3) {
                    mScannerInitiator = 3;
                }
            }
        });

        setBaseDataToView();

        checkAsn();

        setInputViewState();
    }

    private void checkScannerCode(String itemCode) {
        currentStore = null;
        if (NullUtils.isNotEmpty(storeInfoBeans)) {
            for (StoreInfoBean bean : storeInfoBeans) {
                if (itemCode.equals(bean.item_Code)) {
                    currentStore = bean;
                }
            }
            if (currentStore != null) {
                setCurrentStoreInfo();
                setAwaitCount();

                getFeedBox();

                mScannerInitiator = 2;
                setInputViewState();
            } else {
                ToastUtils.show("无效商品");
            }
        } else {
            ToastUtils.show("无可操作商品");
        }
    }

    private void setBaseDataToView() {
        tvSite.setText(site_code);
        tvOrder.setText(asn_code);
    }

    private void setFeedBoxDataToView() {
        if (!TextUtils.isEmpty(mContainerCode)) {
            tvCalled.setText("已呼叫：" + mContainerCode);
            etFeedBoxNo.setText(mContainerCode);
        }
    }

    private void setCurrentStoreInfo() {
        if (currentStore != null) {
            etCxNo.setText(currentStore.item_Code);

            if ("1".equals(currentStore.is_SN)) {
                tvScanSerialTag.setSelected(true);

                toSnScanner();
            } else {
                tvScanSerialTag.setSelected(false);
            }
        }
    }

    private void setInputViewState() {
        etCxNo.setSelected(mScannerInitiator == 1);
        etFeedBoxNo.setSelected(mScannerInitiator == 2);
        tvScanSerial.setSelected(mScannerInitiator == 3);

        if (mScannerInitiator == 1) {
            if (!etCxNo.isFocused()) {
                etCxNo.requestFocus();
            }
        } else if (mScannerInitiator == 2) {
            if (!etFeedBoxNo.isFocused()) {
                etFeedBoxNo.requestFocus();
            }
        } else if (mScannerInitiator == 3) {
            if (!tvScanSerial.isFocused()) {
                tvScanSerial.requestFocus();
            }
        }
    }

    //设置待收数量
    private void setAwaitCount() {
        if (currentStore != null) {
            int quantity = DataUtil.getInt(currentStore.quantity);
            tvCollectedCount.setText(String.valueOf(quantity - snCodeList.size()));

            etCount.setText(String.valueOf(snCodeList.size()));
        }
    }

    private void checkAsn() {
        GetAsnCheckPresenter presenter = new GetAsnCheckPresenter(this
                , new NetResultListener<List<StoreInfoBean>>() {
            @Override
            public void loadSuccess(List<StoreInfoBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    storeInfoBeans.clear();
                    storeInfoBeans.addAll(beans);
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });
        presenter.check(asn_code);
    }

    private void getFeedBox() {
        GetFeedBoxPresenter presenter = new GetFeedBoxPresenter(this
                , new NetResultListener<FeedBoxBean>() {
            @Override
            public void loadSuccess(FeedBoxBean boxBean) {
                if (boxBean != null) {
                    mContainerCode = boxBean.ContainerCode;
                    if (currentStore != null) {
                        currentStore.container_code = mContainerCode;
                    }
                    setFeedBoxDataToView();
                    setAwaitCount();

                    mScannerInitiator = 3;
                    setInputViewState();
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });

        if (currentStore == null) {
            TextUtils.isEmpty("当前没有可操作商品");
            return;
        }

        String userCode = AccountManger.getInstance().getUserCode();
        presenter.getFeedBox(site_code, asn_code, currentStore.item_Code, userCode);
    }

    /**
     * 强制完成收货
     */
    private void forceCompleteDelivery() {
        AsnCloseOrderPresenter presenter = new AsnCloseOrderPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                ToastUtils.show("收货完成");
                finish();
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });
        presenter.close(asn_code, AccountManger.getInstance().getUserCode());
    }

    private void saveDeliveryState(boolean isForceComplete) {
        AsnSaveDetailPresenter presenter = new AsnSaveDetailPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                if (isForceComplete) {
                    forceCompleteDelivery();
                } else {
                    ToastUtils.show("保存成功");
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });

        if (currentStore == null) {
            TextUtils.isEmpty("当前没有可操作商品");
            return;
        }

        presenter.save(asn_code, currentStore.container_code, AccountManger.getInstance().getUserCode()
                , currentStore.keyid, String.valueOf(snCodeList.size()), snCodeList);
    }

    private void getPutStorageDetail() {
        GetAsnDetailPresenter presenter = new GetAsnDetailPresenter(this
                , new NetResultListener<List<AsnDetailBean>>() {
            @Override
            public void loadSuccess(List<AsnDetailBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    showPutStorageDetailDialog(beans);
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });
        presenter.getData(asn_code);
    }

    private void getSnData(String asnCode, String keyId) {
        GetAsnDetailSnListPresenter presenter = new GetAsnDetailSnListPresenter(null
                , new NetResultListener<List<String>>() {
            @Override
            public void loadSuccess(List<String> list) {
                if (NullUtils.isNotEmpty(list)) {
                    showCheckSnDialog(list, asnCode);
                } else {
                    ToastUtils.show("没有数据");
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("请稍后", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });
        presenter.getData(asnCode, keyId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SERIAL_CODE) {
                String snData = data.getStringExtra(Constants.SN_CODE_DATA_KEY);
                snData = TextUtils.isEmpty(snData) ? "" : snData;
                snCodeList = JSONObject.parseArray(snData, String.class);

                setAwaitCount();
            }
        }
    }

    private void showCheckSnDialog(List<String> dataList, String asnCode) {
        if (mCheckSnDialog == null || !mCheckSnDialog.isShowing()) {
            mCheckSnDialog = new CheckSnDialog(this, asnCode, dataList);
            mCheckSnDialog.show();
        }
    }

    private void showPutStorageDetailDialog(List<AsnDetailBean> beans) {
        if (mPutStorageDetailDialog == null || !mPutStorageDetailDialog.isShowing()) {
            mPutStorageDetailDialog = new PutStorageDetailDialog(this, beans);
            mPutStorageDetailDialog.setSnClickListener(new PutStorageDetailDialog.OnSnClickListener() {
                @Override
                public void OnSnClick(AsnDetailBean bean) {
                    getSnData(bean.asn_code, bean.keyid);
                }
            });
            mPutStorageDetailDialog.show();
        }
    }

    private void showConfirmDialog() {
        mConfirmDialog = new ConfirmDialog(this, "确认收货完成吗？", "取消"
                , "确认", new ConfirmDialog.OnConfirmClickListener() {
            @Override
            public void onClick(boolean confirm) {
                if (confirm) {
                    saveDeliveryState(true);
                }
            }
        });
        mConfirmDialog.show();
    }

    private void toSnScanner() {
        Intent intent = new Intent(this, SerialNumScannerActivity.class);
        intent.putExtra(Constants.SN_CODE_DATA_KEY, JSONObject.toJSONString(snCodeList));
        startActivityForResult(intent, REQUEST_SERIAL_CODE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_save:
                //保存
                saveDeliveryState(false);
                break;
            case R.id.tv_detail_list:
                //收货明细列表
                getPutStorageDetail();
                break;
            case R.id.tv_complete:
                //强制完成收货,先进行二次确认,然后调用保存接口
                showConfirmDialog();
                break;
//            case R.id.tv_scan_serial:
//                toSnScanner();
//                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b)
            switch (view.getId()) {
                case R.id.et_cx_no:
                    mScannerInitiator = 1;
                    setInputViewState();
                    Log.e("PWDebug","mScannerInitiator = 1");
                    break;
                case R.id.et_feed_box_no:
                    mScannerInitiator = 2;
                    setInputViewState();
                    Log.e("PWDebug","mScannerInitiator = 2");
                    break;
                case R.id.tv_scan_serial:
                    mScannerInitiator = 3;
                    setInputViewState();
                    Log.e("PWDebug","mScannerInitiator = 3");
                    break;

            }
    }
}