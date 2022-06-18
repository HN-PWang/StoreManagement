package com.mr.lib_base.base;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.mr.lib_base.R;


/**
 * 加载对话框
 */
public class LoadingDialog extends VVICBaseDialog {

    public Dialog showDialog(Context context, String msg, boolean backKeyFinishDialog) {
        Dialog dialog = generalDialog(context, R.style.lib_ui_loading_dialog, R.layout.lib_ui_loading_dialog_layout, Gravity.CENTER,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, backKeyFinishDialog, false);

        TextView msgTv = dialog.findViewById(R.id.tv_loading_msg);
        msgTv.setText(msg);

        final LoadingDialogCircleView loadingView = dialog.findViewById(R.id.view_loading);
        loadingView.startLoading(true);

        return dialog;
    }

}
