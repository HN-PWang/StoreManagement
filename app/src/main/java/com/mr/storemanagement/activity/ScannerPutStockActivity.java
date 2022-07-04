package com.mr.storemanagement.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.mr.storemanagement.bean.AsnSaveBackBean;
import com.mr.storemanagement.bean.FeedBoxBean;
import com.mr.storemanagement.bean.StoreInfoBean;
import com.mr.storemanagement.dialog.CheckSnDialog;
import com.mr.storemanagement.dialog.ConfirmDialog;
import com.mr.storemanagement.dialog.PutStorageDetailDialog;
import com.mr.storemanagement.eventbean.SaveAsnEvent;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.AsnCloseOrderPresenter;
import com.mr.storemanagement.presenter.AsnSaveDetailPresenter;
import com.mr.storemanagement.presenter.GetAsnCheckPresenter;
import com.mr.storemanagement.presenter.GetAsnDetailPresenter;
import com.mr.storemanagement.presenter.GetAsnDetailSnListPresenter;
import com.mr.storemanagement.presenter.GetFeedBoxPresenter;
import com.mr.storemanagement.util.DataUtil;
import com.mr.storemanagement.util.NullUtils;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 入库扫描界面
 */
public class ScannerPutStockActivity extends BaseScannerActivity implements View.OnClickListener
        , View.OnFocusChangeListener {

    public static final int REQUEST_SERIAL_CODE = 101;

    public static final String PS_COMPLETE = "CompleteAsn";   //CompleteAsn 入库单完成
    public static final String PS_CLOSE = "ContainerClose";   //ContainerClose 当前容器关闭

    public static final int IS_SN_STATE = 1;

    private ConstraintLayout mConstraintLayout;

    private TextView tvSite;
    private TextView tvOrder;

    private EditText etItemCode;//册序号
    private TextView tvCalled;//已呼叫的料箱标签
    private EditText etContainerCode;//料箱
    private EditText tvProductBatch;//序列号点击图标
    private ImageView ivProductBatch;//序列号输入框
    private TextView tvProductBatchTag;//序列号可扫描标记
    private TextView etCount;//数量
    private TextView tvCollectedCount;//待收数量
    private TextView tvRfidScan;//扫描RFID

    private PutStorageDetailDialog mPutStorageDetailDialog;

    private CheckSnDialog mCheckSnDialog;

    private ConfirmDialog mConfirmDialog;

    private String site_code;
    private String asn_code;

    private StoreInfoBean currentStore;

    private String mContainerCodeByGet;

    private String mContainerCodeByScanner;

    //当前测序号
    private String mCurrentItemCode;

    //当前序列号
    private String mCurrentProductBatch;

    private List<String> snCodeList = new ArrayList<>();

    private List<StoreInfoBean> storeInfoBeans = new ArrayList<>();

    private int mScannerInitiator = 1; //1:测序号 2:料箱号 3:序列号 4:数量

    private boolean isCompleteDelivery = false;

    private int IS_SN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_put_stock);
        initView();
        setBaseDataToView();
        initListener();
        checkAsn();
        setInputViewState();
    }

    private void initView() {
        site_code = getIntent().getStringExtra("site_key");
        asn_code = getIntent().getStringExtra("ans_key");

        mConstraintLayout = findViewById(R.id.constraint_layout);
        tvSite = findViewById(R.id.tv_search_site);
        tvOrder = findViewById(R.id.et_order_no);
        etItemCode = findViewById(R.id.et_cx_no);
        tvCalled = findViewById(R.id.tv_called);
        etContainerCode = findViewById(R.id.et_feed_box_no);
        tvProductBatch = findViewById(R.id.tv_scan_serial);
        ivProductBatch = findViewById(R.id.iv_scan_rfid);
        tvProductBatchTag = findViewById(R.id.tv_scan_serial_tag);
        etCount = findViewById(R.id.et_count);
        tvCollectedCount = findViewById(R.id.tv_collected_count);
        tvRfidScan = findViewById(R.id.tv_scan_rfid);

        ivProductBatch.setEnabled(false);
        tvProductBatch.setEnabled(false);

        etItemCode.setOnFocusChangeListener(this);
        etContainerCode.setOnFocusChangeListener(this);
        tvProductBatch.setOnFocusChangeListener(this);

        findViewById(R.id.iv_scan_rfid).setOnClickListener(this);
        findViewById(R.id.tv_detail_list).setOnClickListener(this);
        findViewById(R.id.tv_complete).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_scan_rfid).setOnClickListener(this);
    }

    private void initListener() {
        //测序号输入完毕
        etItemCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    writeItemCode(textView.getText().toString().trim());
                }
                return false;
            }
        });

        //料箱号输入完毕
        etContainerCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    writeContainerCode(textView.getText().toString().trim());
                }
                return false;
            }
        });

        /**
         * 序列号输入完毕
         */
        tvProductBatch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    writeProductBatch(textView.getText().toString().trim());
                }
                return false;
            }
        });

        etCount.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                String countStr = editable.toString();
                if (currentStore == null) {
                    if (!TextUtils.isEmpty(countStr) && !"0".equals(countStr)) {
//                        ToastUtils.show("当前商品为空");
                        ShowMsgDialogUtil.show(ScannerPutStockActivity.this, "当前商品为空");
                        etCount.setText("0");
                    }
                } else {
                    if (!TextUtils.isEmpty(countStr)) {
                        int count = Integer.parseInt(countStr);
                        if (count > getWaitingDeliveryCount()) {
//                            ToastUtils.show("商品数量不能超出待收数量");
                            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                                    , "商品数量不能超出待收数量");
                            etCount.setText(String.valueOf(currentStore.quantity));
                        }
                    }
                }
            }
        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (TextUtils.isEmpty(message))
                    return;

                if (mScannerInitiator == 1) {
                    writeItemCode(message);
                } else if (mScannerInitiator == 2) {
                    writeContainerCode(message);
                } else if (mScannerInitiator == 3) {
                    writeProductBatch(message);
                }
            }
        });

//        setOnRfIdListener(new OnRfIdListener() {
//            @Override
//            public void onRFIdDataBack(String message) {
//                if (!TextUtils.isEmpty(message)) {
//                    tvProductBatch.setText(message);
//
//                    snCodeList.clear();
//                    snCodeList.add(message);
//
//                    setAwaitCount(snCodeList.size());
//
//                    if (IS_SN == 0) {
//                        mScannerInitiator = 4;
//                    } else {
//                        mScannerInitiator = -1;
//                    }
//                    setInputViewState();
//                }
//            }
//        });


    }

    /**
     * 写入测序号
     */
    private void writeItemCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            mCurrentItemCode = code;
            checkScannerCodeByItemCode();

            mScannerInitiator = 2;
            setInputViewState();
        }
    }

    /**
     * 写入料箱号
     */
    private void writeContainerCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            mContainerCodeByScanner = code;
            setContainerCodeToView();

            if (IS_SN == 1) {
                mScannerInitiator = 3;
            } else {
                mScannerInitiator = 4;
            }
            setInputViewState();
        }
    }

    private void writeProductBatch(String code) {
        if (!TextUtils.isEmpty(code)) {
            mCurrentProductBatch = code;
//            snCodeList.clear();
            if (!snCodeList.contains(code)) {
                snCodeList.add(code);
            }

            checkScannerCodeByItemCodeAndPB();

            setAwaitCount(getCount());
            if (IS_SN == 0) {
                mScannerInitiator = 4;
            } else {
                mScannerInitiator = -1;
            }
            setInputViewState();
        }
    }

    /**
     * 根据测序码校验获取当前商品信息,不准确,只为了获取一个共有itemCode的信息而已
     */
    private void checkScannerCodeByItemCode() {
        if (TextUtils.isEmpty(mCurrentItemCode)) {
//            ToastUtils.show("请填写测序号");
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "请填写测序号");
            return;
        }

        IS_SN = 0;
        currentStore = null;
        isCompleteDelivery = false;
        if (NullUtils.isNotEmpty(storeInfoBeans)) {
            for (StoreInfoBean bean : storeInfoBeans) {
                if (mCurrentItemCode.equals(bean.item_Code)) {
                    currentStore = bean;
                }
            }
            if (currentStore != null) {
                setCurrentStoreInfo();
                setAwaitCount(0);

                getContainerCode();

                if ("1".equals(currentStore.status)) {
                    isCompleteDelivery = true;
                    ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                            , "该商品已经完成收货");
//                    ToastUtils.show("该商品已经完成收货");
                    remake();
                    return;
                }

                mScannerInitiator = 2;
                setInputViewState();
            } else {
                ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                        , "无效商品");
//                ToastUtils.show("无效商品");
            }
        } else {
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "无可操作商品");
//            ToastUtils.show("无可操作商品");
        }
    }

    private void checkScannerCodeByItemCodeAndPB() {
        if (TextUtils.isEmpty(mCurrentItemCode)) {
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "请填写测序号");
//            ToastUtils.show("请填写测序号");
            return;
        }
        if (TextUtils.isEmpty(mCurrentProductBatch)) {
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "请填写序列号");
//            ToastUtils.show("请填写序列号");
            return;
        }

        currentStore = null;
        isCompleteDelivery = false;

        if (NullUtils.isNotEmpty(storeInfoBeans)) {
            for (StoreInfoBean bean : storeInfoBeans) {
                if (mCurrentItemCode.equals(bean.item_Code)) {
                    //&& mCurrentProductBatch.equals(bean.product_batch)) {
                    currentStore = bean;
                }
            }
            if (currentStore != null) {
                setCurrentStoreInfo();
                setAwaitCount(0);

                if ("1".equals(currentStore.status)) {
                    isCompleteDelivery = true;
//                    ToastUtils.show("该商品已经完成收货");
                    ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                            , "该商品已经完成收货");
                    remake();
                    return;
                }
            } else {
//                ToastUtils.show("无效商品");
                ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                        , "无效商品");
            }
        } else {
//            ToastUtils.show("无可操作商品");
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "无可操作商品");
        }
    }

    private void setBaseDataToView() {
        tvSite.setText(site_code);
        tvOrder.setText(asn_code);
    }

    private void setContainerCodeToView() {
        if (!TextUtils.isEmpty(mContainerCodeByScanner)) {
            tvCalled.setText("已呼叫：" + mContainerCodeByScanner);
            etContainerCode.setText(mContainerCodeByScanner);
        }
    }

    /**
     * 设置当前入库单信息
     */
    private void setCurrentStoreInfo() {
        if (currentStore != null) {
            etItemCode.setText(currentStore.item_Code);

            if (IS_SN_STATE == DataUtil.getInt(currentStore.is_SN)) {
                tvProductBatchTag.setSelected(true);
                etCount.setEnabled(false);
                ivProductBatch.setEnabled(true);
                tvProductBatch.setEnabled(true);
                IS_SN = 1;
            } else {
                tvProductBatchTag.setSelected(false);
                etCount.setEnabled(true);
                ivProductBatch.setEnabled(false);
                tvProductBatch.setEnabled(false);
                IS_SN = 0;
            }

            calculateAwaitCount();
        }
    }

    private void setInputViewState() {
        etItemCode.setSelected(mScannerInitiator == 1);
        etContainerCode.setSelected(mScannerInitiator == 2);
        tvProductBatch.setSelected(mScannerInitiator == 3);
        etCount.setSelected(mScannerInitiator == 4);

        if (mScannerInitiator == 1) {
            if (!etItemCode.isFocused()) {
                etItemCode.requestFocus();
            }
        } else if (mScannerInitiator == 2) {
            if (!etContainerCode.isFocused()) {
                etContainerCode.requestFocus();
            }
        } else if (mScannerInitiator == 3) {
            if (!tvProductBatch.isFocused()) {
                tvProductBatch.requestFocus();
            }
        } else if (mScannerInitiator == 4) {
            if (!etCount.isFocused()) {
                etCount.requestFocus();
            }
        } else {
            mConstraintLayout.requestFocus();
        }
    }

    //设置待收数量
    private void setAwaitCount(int count) {
        etCount.setText(String.valueOf(count));
    }

    private void calculateAwaitCount() {
        tvCollectedCount.setText(String.valueOf(getWaitingDeliveryCount()));
    }

    private void checkAsn() {
        GetAsnCheckPresenter presenter = new GetAsnCheckPresenter(this
                , new NetResultListener<List<StoreInfoBean>>() {
            @Override
            public void loadSuccess(List<StoreInfoBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    storeInfoBeans.clear();
                    storeInfoBeans.addAll(beans);
                } else {
//                    ToastUtils.show("没有获取到校验数据");
                    ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                            , "没有获取到校验数据");
                    finish();
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
                finish();
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

    private void getContainerCode() {
        GetFeedBoxPresenter presenter = new GetFeedBoxPresenter(this
                , new NetResultListener<FeedBoxBean>() {
            @Override
            public void loadSuccess(FeedBoxBean boxBean) {
                if (boxBean != null) {
                    mContainerCodeByGet = boxBean.ContainerCode;
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                //                ToastUtils.show(exception.getErrorMsg());
                ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                        , exception.getErrorMsg());
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
//            TextUtils.isEmpty("当前没有可操作商品");
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "当前没有可操作商品");
            return;
        }

        String userCode = AccountManger.getInstance().getUserCode();
        presenter.getFeedBox(site_code, asn_code, currentStore.item_Code, userCode);
    }

    /**
     * 强制完成收货
     */
    private void forceCompleteDelivery(String state) {
        AsnCloseOrderPresenter presenter = new AsnCloseOrderPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                ToastUtils.show("收货完成");
                if (PS_COMPLETE.equals(state)) {
                    finish();
                } else {
                    remake();
                }
            }

            @Override
            public void loadFailure(SMException exception) {
//                ToastUtils.show(exception.getErrorMsg());
                ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                        , exception.getErrorMsg());
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
                , new NetResultListener<AsnSaveBackBean>() {
            @Override
            public void loadSuccess(AsnSaveBackBean bean) {
                if (isForceComplete) {
                    forceCompleteDelivery(bean.ProcessStatus);
                } else {
                    ToastUtils.show("保存成功");
                    if (bean != null && PS_COMPLETE.equals(bean.ProcessStatus)) {
                        finish();
                    } else {
                        remake();
                    }
                }
                EventBus.getDefault().post(new SaveAsnEvent());
            }

            @Override
            public void loadFailure(SMException exception) {
                //                ToastUtils.show(exception.getErrorMsg());
                ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                        , exception.getErrorMsg());
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
//            TextUtils.isEmpty("当前没有可操作商品");
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "当前没有可操作商品");
            return;
        }

        if (getCount() == 0) {
//            TextUtils.isEmpty("商品数量为0");
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "商品数量为0，不能保存");
            return;
        }

        if (TextUtils.isEmpty(mContainerCodeByScanner)) {
//            TextUtils.isEmpty("还没有呼叫容器号");
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "还没有呼叫容器号");
            return;
        }
        presenter.save(asn_code, mContainerCodeByScanner, AccountManger.getInstance().getUserCode()
                , String.valueOf(getCount()), buildSenList());
    }

    private List<StoreInfoBean.SenNum> buildSenList() {
        List<StoreInfoBean.SenNum> senNumList = new ArrayList<>();
        if (currentStore != null && NullUtils.isNotEmpty(currentStore.sns)
                && NullUtils.isNotEmpty(snCodeList)) {
            for (int i = 0; i < currentStore.sns.size(); i++) {
                StoreInfoBean.SenNum sn = currentStore.sns.get(i);
                for (String code : snCodeList) {
                    if (code.equals(sn.SN)) {
                        senNumList.add(sn);
                    }
                }
            }
        }
        return senNumList;
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
                //                ToastUtils.show(exception.getErrorMsg());
                ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                        , exception.getErrorMsg());
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
                //                ToastUtils.show(exception.getErrorMsg());
                ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                        , exception.getErrorMsg());
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

                setAwaitCount(getCount());
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
            case R.id.iv_scan_rfid:
            case R.id.tv_scan_rfid:
//                readMactchData();
                toSnScanner();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b)
            switch (view.getId()) {
                case R.id.et_cx_no:
                    mScannerInitiator = 1;
                    setInputViewState();
                    break;
                case R.id.et_feed_box_no:
                    mScannerInitiator = 2;
                    setInputViewState();
                    break;
                case R.id.tv_scan_serial:
                    mScannerInitiator = 3;
                    setInputViewState();
                    break;
                case R.id.et_count:
                    mScannerInitiator = 4;
                    setInputViewState();
                    break;

            }
    }

    private int getCount() {
        int count = 0;
        if (IS_SN == 1) {
            count = snCodeList.size();
        } else {
            String str = etCount.getText().toString();
            count = DataUtil.getInt(str);
        }

        return count;
    }

    private int getWaitingDeliveryCount() {
        if (currentStore != null)
            return DataUtil.getInt(currentStore.quantity)
                    - DataUtil.getInt(currentStore.finish_qty);
        return 0;
    }

    private void remake() {
        currentStore = null;
        mContainerCodeByGet = "";
        mContainerCodeByScanner = "";
        mCurrentItemCode = "";
        mCurrentProductBatch = "";
        snCodeList.clear();
        IS_SN = 0;

        etItemCode.setText("");
        etContainerCode.setText("");
        tvProductBatch.setText("");
        tvProductBatchTag.setSelected(false);
        etCount.setText("");
        tvCollectedCount.setText("");
        etCount.setEnabled(false);
        ivProductBatch.setEnabled(false);
        tvProductBatch.setEnabled(false);
        mScannerInitiator = 1;
        setInputViewState();
    }

}