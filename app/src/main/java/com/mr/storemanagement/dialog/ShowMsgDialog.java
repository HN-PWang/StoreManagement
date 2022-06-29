package com.mr.storemanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mr.lib_base.util.VVICUtils;
import com.mr.storemanagement.R;

/**
 * @auther: pengwang
 * @date: 2022/6/29
 * @email: 1929774468@qq.com
 * @description:
 */
public class ShowMsgDialog extends Dialog implements View.OnClickListener {

    private TextView tvMessage;

    public ShowMsgDialog(@NonNull Context context, String message) {
        super(context, R.style.lib_ui_confirm_dialog);
        setContentView(R.layout.dialog_show_msg_layout);

        setCanceledOnTouchOutside(false);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth() - VVICUtils.dip2px(context, 52) * 2;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        params.width = width;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        tvMessage = findViewById(R.id.tv_msg);

        findViewById(R.id.btn_cancel).setOnClickListener(this);

        tvMessage.setText(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}
