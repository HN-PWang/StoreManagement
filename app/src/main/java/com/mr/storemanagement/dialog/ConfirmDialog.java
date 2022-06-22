package com.mr.storemanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mr.lib_base.util.VVICUtils;
import com.mr.storemanagement.R;


public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private TextView tvMessage;
    private Button btnCancel, btnConfirm;
    private OnConfirmClickListener confirmClickListener;

    public ConfirmDialog(@NonNull Context context, String message, String cancel, String confirm,
                         OnConfirmClickListener confirmClickListener) {
        super(context, R.style.lib_ui_confirm_dialog);
        setContentView(R.layout.dialog_confirm_layout);

        this.confirmClickListener = confirmClickListener;

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
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        tvMessage.setText(message);
        btnCancel.setText(cancel);
        btnConfirm.setText(confirm);
    }

    @Override
    public void onClick(View view) {
        if (confirmClickListener != null) {
            if (view.getId() == R.id.btn_cancel) {
                confirmClickListener.onClick(false);
            } else if (view.getId() == R.id.btn_confirm) {
                confirmClickListener.onClick(true);
            }
        }

        dismiss();
    }

    public interface OnConfirmClickListener {
        void onClick(boolean confirm);
    }
}
