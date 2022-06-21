package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.OutStockGoodsAdapter;
import com.mr.storemanagement.base.BaseScannerActivity;
import com.mr.storemanagement.bean.AsnDetailBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 出库扫描商品界面
 */
public class ScannerOutStockActivity extends BaseScannerActivity implements View.OnClickListener {

    private TextView tvFeedBoxNo;

    private TextView tvCxNo;

    private TextView tvScanSerialTag;

    private RecyclerView rvContent;

    private OutStockGoodsAdapter goodsAdapter;

    private List<AsnDetailBean> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_out_stock);

        tvFeedBoxNo = findViewById(R.id.tv_feed_box_no);
        tvCxNo = findViewById(R.id.tv_cx_no);
        tvScanSerialTag = findViewById(R.id.tv_scan_serial_tag);
        rvContent = findViewById(R.id.rv_content);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_scanner).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        rvContent.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter = new OutStockGoodsAdapter(this, mDataList);
        rvContent.setAdapter(goodsAdapter);

        setOnScannerListener(new OnScannerListener() {
            @Override
            public void onScannerDataBack(String message) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_scanner:
                Intent intent = new Intent(this, SerialNumScannerActivity.class);
                startActivityForResult(intent, RESULT_FIRST_USER);
                break;
            case R.id.tv_save:
                //确认拣货

                break;
        }
    }
}