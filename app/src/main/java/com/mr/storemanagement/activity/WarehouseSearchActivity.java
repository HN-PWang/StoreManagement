package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.storemanagement.R;

public class WarehouseSearchActivity extends BaseActivity
        implements TextView.OnEditorActionListener {

    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_search);

        etSearch = findViewById(R.id.et_search);


        etSearch.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

        }
        return false;
    }
}