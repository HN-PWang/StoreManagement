package com.mr.lib_base.base;

import android.app.Dialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private boolean isAutoHiddenSoftInput; // 页面有输入框，当点击到非输入框时，自动隐藏输入法
    protected Dialog mLoadingDialog;

    /**
     * 显示加载中的Loading
     */
    public void showLoadingDialog(String msg, boolean backKeyFinishDialog) {
        if ((mLoadingDialog == null || !mLoadingDialog.isShowing()) && !isFinishing()) {
            mLoadingDialog = new LoadingDialog().showDialog(this, msg, backKeyFinishDialog);
        }
    }

    /**
     * 显示加载中的Loading
     */
    public void showLoadingDialog(String msg, boolean backKeyFinishDialog
            , DialogInterface.OnDismissListener dismissListener) {
        showLoadingDialog(msg, backKeyFinishDialog);
        if (dismissListener != null && mLoadingDialog != null)
            mLoadingDialog.setOnDismissListener(dismissListener);
    }

    /**
     * 关闭加载中的Loading
     */
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing() && !isFinishing() && !isDestroyed()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}
