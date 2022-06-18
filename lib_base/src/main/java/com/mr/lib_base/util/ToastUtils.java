package com.mr.lib_base.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mr.lib_base.BaseApplication;
import com.mr.lib_base.R;

/**
 * 吐司工具类
 */
public class ToastUtils {

    private static long oneTime = 0;

    private static long twoTime = 0;

    public static void show(String text) {
        showCustomToast(BaseApplication.getInstance().getApplicationContext(), text, Toast.LENGTH_SHORT);
    }

    public static void show(int res) {
        Context context = BaseApplication.getInstance().getApplicationContext();
        showCustomToast(context, context.getResources().getString(res), Toast.LENGTH_SHORT);
    }

    public static void show(String text, int duration) {
        showCustomToast(BaseApplication.getInstance().getApplicationContext(), text, duration);
    }

    public static void show(String text, int icon, int duration) {
        showCustomToastWithIcon(BaseApplication.getInstance().getApplicationContext(), text, icon, duration);
    }

    private static void showCustomToast(Context context, String text, int duration) {
        //不用吝惜toast的view内存占有,会很快被清理的,每次进来都新建,避免has already been added to the window manager.
        Toast mToast = new Toast(context);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastRootView = inflate.inflate(R.layout.lib_ui_custom_toast_layout, null);
        mToast.setView(toastRootView);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        //第一次调用时间
        oneTime = System.currentTimeMillis();
        mToast.setDuration(duration);
//        if (mToast == null) {
//            mToast = new Toast(context);
//            View view = View.inflate(context, R.layout.lib_ui_custom_toast_layout, null);
//            mToast.setView(view);
//            mToast.setGravity(Gravity.CENTER, 0, 0);
//            //第一次调用时间
//            oneTime = System.currentTimeMillis();
//        } else {
//            //第二次调用时间
//            twoTime = System.currentTimeMillis();
//            if (twoTime - oneTime < 4000) {
//                //如果两次时间相差小于4秒（Toast.Length_Short的时间）则重新创建，这样就可以有个关掉上次toast的效果，
//                mToast = new Toast(context);
//                View view = View.inflate(context, R.layout.lib_ui_custom_toast_layout, null);
//                mToast.setView(view);
//                mToast.setGravity(Gravity.CENTER, 0, 0);
//            }
//        }
        if (toastRootView != null) {
            TextView textView = toastRootView.findViewById(R.id.tv_text);
            textView.setText(text);
        }
        mToast.show();
        oneTime = twoTime;
    }

    /**
     * 显示带Icon的自定义吐司
     */
    private static void showCustomToastWithIcon(Context context, String text, int icon, int duration) {
        Toast mIconToast = new Toast(context.getApplicationContext());
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflate.inflate(R.layout.lib_ui_custom_toast_with_icon_layout, null);
        mIconToast.setView(view);
        mIconToast.setGravity(Gravity.CENTER, 0, 0);
        mIconToast.setDuration(duration);
        if (view != null) {
            TextView textView = view.findViewById(R.id.tv_text);
            textView.setText(text);
            ImageView iconIv = view.findViewById(R.id.iv_icon);
            iconIv.setImageResource(icon);
        }
        mIconToast.show();
    }

}
