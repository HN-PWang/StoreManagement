package com.mr.lib_base.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * @auther: pengwang
 * @date: 2022/7/6
 * @email: 1929774468@qq.com
 * @description:
 */
public class SMEditText extends androidx.appcompat.widget.AppCompatEditText {

    public SMEditText(Context context) {
        super(context);
    }

    public SMEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SMEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);


        if (focused) {
//            setSelection(0, getText().length());
//            setSelectAllOnFocus(true);
            selectAll();
        }
    }

}
