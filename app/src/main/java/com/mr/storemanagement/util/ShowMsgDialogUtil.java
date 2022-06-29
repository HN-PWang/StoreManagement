package com.mr.storemanagement.util;

import android.content.Context;

import com.mr.storemanagement.dialog.ShowMsgDialog;

/**
 * @auther: pengwang
 * @date: 2022/6/29
 * @email: 1929774468@qq.com
 * @description:
 */
public class ShowMsgDialogUtil {

    public static void show(Context context, String msg) {
        ShowMsgDialog dialog = new ShowMsgDialog(context, msg);
        dialog.show();
    }

}
