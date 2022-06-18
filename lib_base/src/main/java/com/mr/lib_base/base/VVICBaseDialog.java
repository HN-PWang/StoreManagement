package com.mr.lib_base.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

/**
 * 所有自定义对话框的基类
 */
public class VVICBaseDialog {

    protected Dialog generalDialog(Context context, int style, int layoutId, int gravity, int width, int height, boolean backKeyFinishDialog,
                                   boolean touchOutsideFinish){
        Activity activity = (Activity) context;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        Dialog dialog = new Dialog(activity, style);
        dialog.setContentView(layoutId);

        Window window = dialog.getWindow();

        if (window == null)
            return null;

        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(gravity);

        params.width = width;
        params.height = height;
        window.setAttributes(params);

        dialog.setCancelable(backKeyFinishDialog);
        dialog.setCanceledOnTouchOutside(touchOutsideFinish);

        dialog.show();
        return dialog;
    }

}
