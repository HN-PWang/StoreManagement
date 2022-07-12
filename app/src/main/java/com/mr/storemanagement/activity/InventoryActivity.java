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
import com.mr.lib_base.widget.SMEditText;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.AsnDetailBean;
import com.mr.storemanagement.bean.AsnSaveBackBean;
import com.mr.storemanagement.bean.FeedBoxBean;
import com.mr.storemanagement.bean.InvDetailsBean;
import com.mr.storemanagement.bean.InvSaveBackBean;
import com.mr.storemanagement.bean.StoreInfoBean;
import com.mr.storemanagement.dialog.CheckSnDialog;
import com.mr.storemanagement.dialog.ConfirmDialog;
import com.mr.storemanagement.dialog.InvDetailDialog;
import com.mr.storemanagement.dialog.PutStorageDetailDialog;
import com.mr.storemanagement.eventbean.SaveAsnEvent;
import com.mr.storemanagement.eventbean.SaveInvEvent;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.AsnCloseOrderPresenter;
import com.mr.storemanagement.presenter.AsnSaveDetailPresenter;
import com.mr.storemanagement.presenter.GetAsnCheckPresenter;
import com.mr.storemanagement.presenter.GetAsnDetailPresenter;
import com.mr.storemanagement.presenter.GetAsnDetailSnListPresenter;
import com.mr.storemanagement.presenter.GetFeedBoxPresenter;
import com.mr.storemanagement.presenter.GetInvDetailsPresenter;
import com.mr.storemanagement.presenter.InvSaveDetailPresenter;
import com.mr.storemanagement.presenter.SetInvCompletePresenter;
import com.mr.storemanagement.util.DataUtil;
import com.mr.storemanagement.util.NullUtils;
import com.mr.storemanagement.util.ShowMsgDialogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 盘点扫描界面
 */
public class InventoryActivity extends BaseScannerActivity implements View.OnClickListener
        , View.OnFocusChangeListener {

    public static final int REQUEST_SERIAL_CODE = 101;

    public static final String PS_COMPLETE = "AllComplete";   // 所有非agv明细完成
    public static final String PS_CLOSE = "ContainerComplete";   //当前容器完成

    public static final int IS_SN_STATE = 1;

    private ConstraintLayout mConstraintLayout;

    private TextView tvSite;
    private TextView tvOrder;

    private SMEditText etItemCode;//册序号
    private TextView tvCalled;//已呼叫的料箱标签
    private SMEditText etContainerCode;//料箱
    private TextView tvProductBatchTag;//序列号可扫描标记
    private SMEditText etCount;//数量
    private TextView tvToScanner;//数量

    private InvDetailDialog mInvDetailDialog;

    private CheckSnDialog mCheckSnDialog;

    private ConfirmDialog mConfirmDialog;

    private String mSiteCode;
    private String mInvCode;

    private InvDetailsBean currentInvDetails;

    private String mContainerCodeByScanner;

    //当前测序号
    private String mCurrentItemCode;

    private List<String> snCodeList = new ArrayList<>();

    private List<InvDetailsBean> mInvDetailsList = new ArrayList<>();

    private int mScannerInitiator = 1; //1:料箱号 2:测序号 3:数量

    private int IS_SN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        initView();
        setBaseDataToView();
        initListener();
        setInputViewState();
    }

    private void initView() {
        mSiteCode = getIntent().getStringExtra(Constants.SITE_CODE_KEY);
        mInvCode = getIntent().getStringExtra(Constants.HAS_TASK_KEY);
        String data = getIntent().getStringExtra(Constants.INV_DETAILS_DATA_KEY);
        if (!TextUtils.isEmpty(data)) {
            mInvDetailsList = JSONObject.parseArray(data, InvDetailsBean.class);
        }

        mConstraintLayout = findViewById(R.id.constraint_layout);
        tvSite = findViewById(R.id.tv_search_site);
        tvOrder = findViewById(R.id.et_order_no);
        etItemCode = findViewById(R.id.et_cx_no);
        tvCalled = findViewById(R.id.tv_called);
        etContainerCode = findViewById(R.id.et_feed_box_no);
        tvProductBatchTag = findViewById(R.id.tv_scan_serial_tag);
        etCount = findViewById(R.id.et_count);
        tvToScanner = findViewById(R.id.tv_to_scanner);

        etItemCode.setOnFocusChangeListener(this);
        etContainerCode.setOnFocusChangeListener(this);

        findViewById(R.id.tv_to_scanner).setOnClickListener(this);
        findViewById(R.id.tv_inv_detail).setOnClickListener(this);
        findViewById(R.id.tv_complete).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
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
                if (currentInvDetails == null) {
                    if (!TextUtils.isEmpty(countStr) && !"0".equals(countStr)) {
                        ShowMsgDialogUtil.show(InventoryActivity.this, "当前商品为空");
                        etCount.setText("0");
                    }
                } else {
                    if (!TextUtils.isEmpty(countStr)) {
                        int count = Integer.parseInt(countStr);
                        if (count > getWaitingDeliveryCount()) {
                            ShowMsgDialogUtil.show(InventoryActivity.this
                                    , "商品数量不能超出待收数量");
                            etCount.setText(String.valueOf(DataUtil.getInt(currentInvDetails.available_qty)));
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

                if (mScannerInitiator == 2) {
                    writeItemCode(message);
                } else if (mScannerInitiator == 1) {
                    writeContainerCode(message);
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
        }
    }

    /**
     * 写入料箱号
     */
    private void writeContainerCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            mContainerCodeByScanner = code;
            setContainerCodeToView();

            mScannerInitiator = 2;

            setInputViewState();
        }
    }

    /**
     * 根据测序码校验获取当前商品信息,不准确,只为了获取一个共有itemCode的信息而已
     */
    private void checkScannerCodeByItemCode() {
        if (TextUtils.isEmpty(mCurrentItemCode)) {
            ShowMsgDialogUtil.show(InventoryActivity.this
                    , "请填写测序号");
            return;
        }

        IS_SN = 0;
        currentInvDetails = null;
        if (NullUtils.isNotEmpty(mInvDetailsList)) {
            List<InvDetailsBean> sameItemCodeList = new ArrayList();
            for (InvDetailsBean bean : mInvDetailsList) {
                if (mCurrentItemCode.equals(bean.item_Code)) {
                    currentInvDetails = bean;
                }
            }

            //这里为了处理有的数据item code相同
            //先拿出相同的集合,在去寻找到第一个不为全部收货的数据,如果找不到就是全部收货
            if (NullUtils.isNotEmpty(sameItemCodeList)) {
                int indexByState = -1;
                for (int i = 0; i < sameItemCodeList.size(); i++) {
                    InvDetailsBean item = mInvDetailsList.get(i);
                    if (!"1".equals(item.status)) {
                        indexByState = i;
                        break;
                    }
                }
                if (indexByState < 0) {
                    currentInvDetails = sameItemCodeList.get(0);
                } else {
                    currentInvDetails = sameItemCodeList.get(indexByState);
                }
            }

            if (currentInvDetails != null) {
                setCurrentStoreInfo();

                if ("1".equals(currentInvDetails.status)) {
                    ShowMsgDialogUtil.show(InventoryActivity.this
                            , "该商品已经完成收货");
                    remake();
                    return;
                }

                if (IS_SN == 1) {
                    mScannerInitiator = -1;
                    toSnScanner();
                } else {
                    mScannerInitiator = 3;
                }
                setInputViewState();
            } else {
                ShowMsgDialogUtil.show(InventoryActivity.this
                        , "无效商品");
            }
        } else {
            ShowMsgDialogUtil.show(InventoryActivity.this
                    , "无可操作商品");
        }
    }

    private void setBaseDataToView() {
        tvSite.setText(mSiteCode);
        tvOrder.setText(mInvCode);
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
        if (currentInvDetails != null) {
            if (IS_SN_STATE == DataUtil.getInt(currentInvDetails.is_SN)) {
                tvProductBatchTag.setSelected(true);
                etCount.setEnabled(false);
                IS_SN = 1;
            } else {
                tvProductBatchTag.setSelected(false);
                etCount.setEnabled(true);
                IS_SN = 0;
            }

            calculateAwaitCount();
        }
    }

    private void setInputViewState() {
        etContainerCode.setSelected(mScannerInitiator == 1);
        etItemCode.setSelected(mScannerInitiator == 2);
        etCount.setSelected(mScannerInitiator == 3);

        if (mScannerInitiator == 1) {
            if (!etContainerCode.isFocused()) {
                etContainerCode.requestFocus();
            }
        } else if (mScannerInitiator == 2) {
            if (!etItemCode.isFocused()) {
                etItemCode.requestFocus();
            }
        } else if (mScannerInitiator == 3) {
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
//        tvCollectedCount.setText(String.valueOf(getWaitingDeliveryCount()));
    }

    /**
     * 强制完成收货
     */
    private void forceCompleteDelivery(String state) {
        SetInvCompletePresenter presenter = new SetInvCompletePresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                ToastUtils.show("盘点完成");
                finish();
                if (PS_COMPLETE.equals(state)) {
                    finish();
                } else {
                    remake();
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(InventoryActivity.this
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
        presenter.close(mInvCode, AccountManger.getInstance().getUserCode());
    }

    private void saveDeliveryState(boolean isForceComplete) {
        InvSaveDetailPresenter presenter = new InvSaveDetailPresenter(this
                , new NetResultListener<InvSaveBackBean>() {
            @Override
            public void loadSuccess(InvSaveBackBean bean) {
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
                EventBus.getDefault().post(new SaveInvEvent());
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(InventoryActivity.this
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

        if (currentInvDetails == null) {
            ShowMsgDialogUtil.show(InventoryActivity.this
                    , "当前没有可操作商品");
            return;
        }

        if (getCount() == 0) {
            ShowMsgDialogUtil.show(InventoryActivity.this
                    , "商品数量为0，不能保存");
            return;
        }

        if (TextUtils.isEmpty(mContainerCodeByScanner)) {
            ShowMsgDialogUtil.show(InventoryActivity.this
                    , "还没有呼叫容器号");
            return;
        }
        presenter.save(mInvCode, mContainerCodeByScanner, AccountManger.getInstance().getUserCode()
                , String.valueOf(getCount()), currentInvDetails.uid, snCodeList);
    }


    private void getInvDetailsShowDialog() {
        GetInvDetailsPresenter presenter = new GetInvDetailsPresenter(this
                , new NetResultListener<List<InvDetailsBean>>() {
            @Override
            public void loadSuccess(List<InvDetailsBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    showPutStorageDetailDialog(beans);
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ShowMsgDialogUtil.show(InventoryActivity.this
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
        presenter.get(mInvCode);
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

    private void showPutStorageDetailDialog(List<InvDetailsBean> beans) {
        if (mInvDetailDialog == null || !mInvDetailDialog.isShowing()) {
            mInvDetailDialog = new InvDetailDialog(this, beans);
            mInvDetailDialog.setSnClickListener(new InvDetailDialog.OnSnClickListener() {
                @Override
                public void OnSnClick(InvDetailsBean bean) {
                    showCheckSnDialog(bean.sn_list.SnList, mInvCode);
                }
            });
            mInvDetailDialog.show();
        }
    }

    private void showConfirmDialog() {
        mConfirmDialog = new ConfirmDialog(this, "确认盘点完成吗？", "取消"
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
        intent.putExtra(Constants.SN_CODE_CHECK_DATA_KEY, checkData());
        startActivityForResult(intent, REQUEST_SERIAL_CODE);
    }

    private String checkData() {
        List<String> checkList = new ArrayList<>();
        if (currentInvDetails != null && currentInvDetails.sn_list != null
                && NullUtils.isNotEmpty(currentInvDetails.sn_list.SnList)) {
            checkList.addAll(currentInvDetails.sn_list.SnList);
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
            case R.id.tv_inv_detail:
                //收货明细列表
                getInvDetailsShowDialog();
                break;
            case R.id.tv_complete:
                //强制完成收货,先进行二次确认,然后调用保存接口
                showConfirmDialog();
                break;
            case R.id.tv_to_scanner:
                toSnScanner();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean isFocus) {
        if (isFocus && !view.isFocused()) {
            switch (view.getId()) {
                case R.id.et_feed_box_no:
                    mScannerInitiator = 1;
                    setInputViewState();
                    break;
                case R.id.et_cx_no:
                    mScannerInitiator = 2;
                    setInputViewState();
                    break;
                case R.id.et_count:
                    mScannerInitiator = 3;
                    setInputViewState();
                    break;
            }
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
        if (currentInvDetails != null)
            return DataUtil.getInt(currentInvDetails.available_qty)
                    - DataUtil.getInt(currentInvDetails.check_qty);
        return 0;
    }

    private void remake() {
        currentInvDetails = null;
        mContainerCodeByScanner = "";
        mCurrentItemCode = "";
        snCodeList.clear();
        IS_SN = 0;

        etItemCode.setText("");
        etContainerCode.setText("");
        tvProductBatchTag.setSelected(false);
        etCount.setText("");
        etCount.setEnabled(false);
        tvToScanner.setEnabled(false);
        mScannerInitiator = 1;
        setInputViewState();
    }

}