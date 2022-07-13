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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.AfterTextChangedListener;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.lib_base.widget.SMEditText;
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

    private SMEditText etItemCode;//册序号
    private TextView tvCalled;//已呼叫的料箱标签
    private SMEditText etContainerCode;//料箱
    private TextView tvProductBatchTag;//序列号可扫描标记
    private TextView etCount;//数量
    private TextView tvCollectedCount;//待收数量
    private TextView tvRfidScan;//扫描RFID
    private EditText etGetFocus;//仅仅用来抢占焦点

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

    private List<String> snCodeList = new ArrayList<>();

    private List<StoreInfoBean> storeInfoBeans = new ArrayList<>();

    private int mScannerInitiator = 1; //1:测序号 2:料箱号 3:数量

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
        tvProductBatchTag = findViewById(R.id.tv_scan_serial_tag);
        etCount = findViewById(R.id.et_count);
        tvCollectedCount = findViewById(R.id.tv_collected_count);
        tvRfidScan = findViewById(R.id.tv_scan_rfid);
        etGetFocus = findViewById(R.id.et_get_focus);

        etItemCode.setOnFocusChangeListener(this);
        etContainerCode.setOnFocusChangeListener(this);

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

        etCount.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                String countStr = editable.toString();
                if (currentStore == null) {
                    if (!TextUtils.isEmpty(countStr) && !"0".equals(countStr)) {
                        ShowMsgDialogUtil.show(ScannerPutStockActivity.this, "当前商品为空");
                        etCount.setText("0");
                    }
                } else {
                    if (!TextUtils.isEmpty(countStr)) {
                        int count = Integer.parseInt(countStr);
                        if (count > getWaitingDeliveryCount()) {
                            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                                    , "商品数量不能超出待收数量");
                            etCount.setText(String.valueOf(DataUtil.getInt(currentStore.quantity)));
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
//                    writeProductBatch(message);
                }
            }
        });

    }

    /**
     * 写入测序号
     */
    private void writeItemCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            mCurrentItemCode = code;
            checkScannerCodeByItemCode();

//            mScannerInitiator = 2;
//            setInputViewState();
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
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "请填写测序号");
            return;
        }

        IS_SN = 0;
        currentStore = null;
        if (NullUtils.isNotEmpty(storeInfoBeans)) {
            List<StoreInfoBean> sameItemCodeList = new ArrayList();
            for (StoreInfoBean bean : storeInfoBeans) {
                if (mCurrentItemCode.equals(bean.item_Code)) {
                    sameItemCodeList.add(bean);
                }
            }

            //这里为了处理有的数据item code相同
            //先拿出相同的集合,在去寻找到第一个不为全部收货的数据,如果找不到就是全部收货
            if (NullUtils.isNotEmpty(sameItemCodeList)) {
                int indexByState = -1;
                for (int i = 0; i < sameItemCodeList.size(); i++) {
                    StoreInfoBean item = storeInfoBeans.get(i);
                    if (!"1".equals(item.status)) {
                        indexByState = i;
                        break;
                    }
                }
                if (indexByState < 0) {
                    currentStore = sameItemCodeList.get(0);
                } else {
                    currentStore = sameItemCodeList.get(indexByState);
                }
            }

            if (currentStore != null) {
                setCurrentStoreInfo();
                setAwaitCount(0);

                getContainerCode();

                if ("1".equals(currentStore.status)) {
                    ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                            , "该商品已经完成收货");
                    remake();
                    return;
                }

                mScannerInitiator = 2;
                setInputViewState();
            } else {
                ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                        , "无效商品");
            }
        } else {
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
                tvRfidScan.setEnabled(true);
                IS_SN = 1;
            } else {
                tvProductBatchTag.setSelected(false);
                etCount.setEnabled(true);
                etCount.setEnabled(false);
                IS_SN = 0;
            }

            calculateAwaitCount();
        }
    }

    private void setInputViewState() {
        etItemCode.setSelected(mScannerInitiator == 1);
        etContainerCode.setSelected(mScannerInitiator == 2);
        etCount.setSelected(mScannerInitiator == 3);

        if (mScannerInitiator == 1) {
            if (!etItemCode.isFocused()) {
                etItemCode.requestFocus();
            }
        } else if (mScannerInitiator == 2) {
            if (!etContainerCode.isFocused()) {
                etContainerCode.requestFocus();
            }
        } else if (mScannerInitiator == 3) {
            if (!etCount.isFocused()) {
                etCount.requestFocus();
            }
        } else {
            etGetFocus.requestFocus();
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
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "当前没有可操作商品");
            return;
        }

        if (getCount() == 0) {
            ShowMsgDialogUtil.show(ScannerPutStockActivity.this
                    , "商品数量为0，不能保存");
            return;
        }

        if (TextUtils.isEmpty(mContainerCodeByScanner)) {
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
                    if (currentStore != null) {
                        saveDeliveryState(true);
                    } else {
                        forceCompleteDelivery(PS_COMPLETE);
                    }
                }
            }
        });
        mConfirmDialog.show();
    }

    private void toSnScanner() {
        Intent intent = new Intent(this, SerialNumScannerActivity.class);
        intent.putExtra(Constants.SN_CODE_DATA_KEY, JSONObject.toJSONString(snCodeList));
        intent.putExtra(Constants.SN_CODE_CHECK_DATA_KEY, checkData());
        startActivityForResult(intent, REQUEST_SERIAL_CODE);
    }

    private String checkData() {
        List<String> checkList = new ArrayList<>();
        if (currentStore != null && NullUtils.isNotEmpty(currentStore.sns)) {
            for (StoreInfoBean.SenNum senNum : currentStore.sns) {
                checkList.add(senNum.SN);
            }
        }
        return JSONObject.toJSONString(checkList);
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
            case R.id.tv_scan_rfid:
                toSnScanner();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean isFocus) {
        if (isFocus && !view.isFocused())
            switch (view.getId()) {
                case R.id.et_cx_no:
                    mScannerInitiator = 1;
                    setInputViewState();
                    break;
                case R.id.et_feed_box_no:
                    mScannerInitiator = 2;
                    setInputViewState();
                    break;
                case R.id.et_count:
                    mScannerInitiator = 3;
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
        snCodeList.clear();
        IS_SN = 0;

        etItemCode.setText("");
        etContainerCode.setText("");
        tvProductBatchTag.setSelected(false);
        etCount.setText("");
        tvCollectedCount.setText("");
        etCount.setEnabled(false);
        tvRfidScan.setEnabled(false);
        mScannerInitiator = 1;
        setInputViewState();
    }

}