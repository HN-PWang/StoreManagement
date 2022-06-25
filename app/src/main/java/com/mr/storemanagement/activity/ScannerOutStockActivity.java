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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mr.lib_base.AfterTextChangedListener;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.Constants;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.OutStockGoodsAdapter;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.AsnDetailBean;
import com.mr.storemanagement.bean.ContainerGoodsBean;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.OutStockConfirmPresenter;
import com.mr.storemanagement.util.NullUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 出库扫描商品界面
 */
public class ScannerOutStockActivity extends BaseScannerActivity implements View.OnClickListener {

    public static final int REQUEST_SERIAL_CODE = 101;

    private TextView tvContainerNo;

    private TextView tvCxNo;

    private EditText etOutCount;

    private TextView tvScanSerialTag;

    private RecyclerView rvContent;

    private TextView tvScanner;

    private OutStockGoodsAdapter goodsAdapter;

    private String mSiteCode;

    private String mContainerCode;

    private List<ContainerGoodsBean> mContainerGoodsList;

    private List<ContainerGoodsBean> mDataList = new ArrayList<>();

    private ContainerGoodsBean currentGoodsBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_out_stock);

        mSiteCode = getIntent().getStringExtra(Constants.SITE_CODE_KEY);
        mContainerCode = getIntent().getStringExtra(Constants.CONTAINER_CODE_KEY);
        String cgd = getIntent().getStringExtra(Constants.CONTAINER_GOODS_DATA_KEY);
        if (!TextUtils.isEmpty(cgd)) {
            mContainerGoodsList = JSONObject.parseArray(cgd, ContainerGoodsBean.class);
        }

        mDataList.addAll(mContainerGoodsList);

        tvContainerNo = findViewById(R.id.tv_container_no);
        tvCxNo = findViewById(R.id.tv_cx_no);
        etOutCount = findViewById(R.id.et_out_count);
        tvScanSerialTag = findViewById(R.id.tv_scan_serial_tag);
        rvContent = findViewById(R.id.rv_content);
        tvScanner = findViewById(R.id.tv_scanner);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_scanner).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        rvContent.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter = new OutStockGoodsAdapter(this, mDataList);
        rvContent.setAdapter(goodsAdapter);

        tvCxNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (!TextUtils.isEmpty(tvCxNo.getText().toString())) {
                        currentGoodsBean = findGoodsByCode(tvCxNo.getText().toString());
                        if (currentGoodsBean != null) {
                            setGoodsInfoToView(currentGoodsBean);
                        } else {
                            ToastUtils.show("容器中没有找到该商品");
                        }
                    }
                }
                return false;
            }
        });

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {
                if (!TextUtils.isEmpty(message)) {
                    currentGoodsBean = findGoodsByCode(message);
                    if (currentGoodsBean != null) {
                        setGoodsInfoToView(currentGoodsBean);
                    } else {
                        ToastUtils.show("容器中没有找到该商品");
                    }
                }
            }
        });

        setBaseDataToView();

        setOutCount();
    }

    private ContainerGoodsBean findGoodsByCode(String code) {
        if (NullUtils.isNotEmpty(mDataList)) {
            for (ContainerGoodsBean bean : mDataList) {
                if (code.equals(bean.item_Code))
                    return bean;
            }
        }
        return null;
    }

    private void setBaseDataToView() {
        tvContainerNo.setText(mContainerCode);

        goodsAdapter.notifyDataSetChanged();
    }

    private void setGoodsInfoToView(ContainerGoodsBean goodsBean) {
        tvCxNo.setText(goodsBean.item_Code);

        if (1 == goodsBean.is_SN) {
            etOutCount.setEnabled(false);
            tvScanner.setEnabled(true);
            tvScanSerialTag.setSelected(true);
            toSnScanner();
        } else {
            etOutCount.setEnabled(true);
            tvScanner.setEnabled(false);
            tvScanSerialTag.setSelected(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SERIAL_CODE) {
                String snData = data.getStringExtra(Constants.SN_CODE_DATA_KEY);
                snData = TextUtils.isEmpty(snData) ? "" : snData;
                List<String> snList = JSONObject.parseArray(snData, String.class);
                if (currentGoodsBean != null)
                    currentGoodsBean.snList = snList;

                setOutCount();
            }
        }
    }

    private void setOutCount() {
        String count = "0";
        if (currentGoodsBean != null && NullUtils.isNotEmpty(currentGoodsBean.snList)) {
            count = String.valueOf(currentGoodsBean.snList.size());
        }
        etOutCount.setText(count);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_scanner:
                toSnScanner();
                break;
            case R.id.tv_save:
                //确认拣货
                confirm();
                break;
        }
    }

    private void toSnScanner() {
        Intent intent = new Intent(this, SerialNumScannerActivity.class);
        intent.putExtra(Constants.SN_CODE_DATA_KEY, JSONObject.toJSONString(currentGoodsBean.snList));
        startActivityForResult(intent, REQUEST_SERIAL_CODE);
    }

    private void confirm() {
        OutStockConfirmPresenter presenter = new OutStockConfirmPresenter(this
                , new NetResultListener() {
            @Override
            public void loadSuccess(Object o) {
                ToastUtils.show("拣货成功");
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
        presenter.save(mSiteCode, AccountManger.getInstance().getUserCode(), buildData());
    }

    private JSONObject buildData() {
        JSONObject object = null;
        if (currentGoodsBean != null) {
            object = JSONObject.parseObject(JSONObject.toJSONString(currentGoodsBean));

            if (NullUtils.isNotEmpty(currentGoodsBean.snList)) {
                JSONArray snA = new JSONArray();
                for (String sn : currentGoodsBean.snList) {
                    snA.add(sn);
                }
                object.put("SNList", snA);
            }
        }
        return object;
    }

}