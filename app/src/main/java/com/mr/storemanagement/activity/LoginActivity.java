package com.mr.storemanagement.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.storemanagement.BuildConfig;
import com.mr.storemanagement.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_version_name)
    TextView tVersionName;

    @BindView(R.id.et_pwd)
    EditText etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        tVersionName.setText(BuildConfig.VERSION_NAME);
    }
}