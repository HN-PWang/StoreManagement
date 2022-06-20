package com.mr.storemanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.BuildConfig;
import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.UserInfoBean;
import com.mr.storemanagement.manger.AccountManger;
import com.mr.storemanagement.presenter.LoginPresenter;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    TextView tVersionName;

    EditText tvName;

    EditText etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tVersionName = findViewById(R.id.tv_version_name);
        tvName = findViewById(R.id.tv_name);
        etPwd = findViewById(R.id.et_pwd);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_login).setOnClickListener(this);

        tVersionName.setText(BuildConfig.VERSION_NAME);

        if (AccountManger.getInstance().getAccount() != null) {
            toMainActivity();
        }

    }

    private void login() {
        LoginPresenter presenter = new LoginPresenter(this
                , new NetResultListener<UserInfoBean>() {
            @Override
            public void loadSuccess(UserInfoBean bean) {
                ToastUtils.show("登录成功");
                if (bean != null) {
                    AccountManger.getInstance().login(bean);
                    toMainActivity();
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, new NetLoadingListener() {
            @Override
            public void startLoading() {
                showLoadingDialog("正在登录", false);
            }

            @Override
            public void finishLoading() {
                dismissLoadingDialog();
            }
        });
        presenter.login(tvName.getText().toString(), etPwd.getText().toString());
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_login:
                login();
                break;
        }
    }

}