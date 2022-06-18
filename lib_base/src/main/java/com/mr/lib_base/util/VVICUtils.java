package com.mr.lib_base.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 */
public class VVICUtils {
    /**
     * dip转px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕高度（包含状态栏，但是不包含底部虚拟键）
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 获取屏幕真正高度（包含状态栏，也包含底部虚拟键）
     */
    public static int getScreenRealHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            // 可能有虚拟按键的情况
            display.getRealSize(outPoint);
        } else {
            // 不可能有虚拟按键
            display.getSize(outPoint);
        }
        return outPoint.y;
    }

    /**
     * 显示软键盘
     */
    public static void showKeyBoard(Context context, View view) {
        // 显示软键盘
        try {
            InputMethodManager imm = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
            imm.showSoftInput(view, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Context context) {
        try {
            InputMethodManager imm = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
            if (((Activity) context).getCurrentFocus() != null) {
                View view = ((Activity) context).getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Context context, View editTextView) {
        try {
            InputMethodManager imm = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
            imm.hideSoftInputFromWindow(editTextView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * VVIC手机号码判断
     */
    public static boolean isMobileNumber(String phone) {
        String reg = "^1\\d{10}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(phone);
        boolean flag = matcher.matches();
        return flag;
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean theStringIsNumber(String text) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    /**
     * 获取TextView设置的text字体后的宽度
     */
    public static float getTextViewTextWidth(TextView tv, String text) {
        TextPaint textPaint = tv.getPaint();
        return textPaint.measureText(text);
    }

    /**
     * 获取TextView设置的text字体后的宽度
     */
    public static float getTextViewTextWidth(TextView tv, String text, float maxWidth) {
        TextPaint textPaint = tv.getPaint();
        // 如果字符串没有显示完整，则返回给用户能看到的内容，后面看不到的内容会被截取抛弃掉，如果显示完整的，则返回全部内容。
        String newStr = (String) TextUtils.ellipsize(text, textPaint, maxWidth, TextUtils.TruncateAt.END);
        if (!text.equals(newStr)) {
            return maxWidth;
        }
        return textPaint.measureText(newStr);
    }

    /**
     * 获取控件的高度
     */
    public static int[] getViewHeightAndWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int[] results = new int[2];
        results[0] = view.getMeasuredHeight();
        results[1] = view.getMeasuredWidth();
        return results;
    }

    /**
     * 交换两个元素的位置
     */
    public static <T> void swapPositionForList(List<T> list, int oldPosition, int newPosition) {
        T tempElement = list.get(oldPosition);
        // 向前移动，前面的元素需要向后移动
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                list.set(i, list.get(i + 1));
            }
            list.set(newPosition, tempElement);
        }
        // 向后移动，后面的元素需要向前移动
        if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                list.set(i, list.get(i - 1));
            }
            list.set(newPosition, tempElement);
        }
    }

    /**
     * 获取输入内容的长度，汉字占两个
     */
    public static int getInputStringLength(String inputText) {
        int length = 0;
        if (!TextUtils.isEmpty(inputText)) {
            char[] chars = inputText.toCharArray();
            for (char c : chars) {
                if (c < 128) {
                    length += 1;
                } else {
                    length += 2;
                }
            }
        }
        return length;
    }

    /**
     * 设置最大宽度，如果大于该宽度，去掉超出的字符串，然后添加...
     */
    public static String setMaxLengthTextWithEndPoint(String text, int maxLength) {
        String result;
        int length = getInputStringLength(text);
        if (length <= maxLength) {
            result = text;
        } else {
            int totalLength = 0;
            StringBuilder sb = new StringBuilder();
            char[] chars = text.toCharArray();
            for (char c : chars) {
                if (c < 128) {
                    totalLength += 1;
                } else {
                    totalLength += 2;
                }
                if (totalLength <= maxLength) {
                    sb.append(c);
                } else {
                    break;
                }
            }
            result = sb.toString() + "...";
        }
        return result;
    }

    /**
     * 根据ItemType的不同，获取完整的图片Url
     * 由于淘宝图片和VVIC对图片的切割方式不同，因此做的处理
     * <p>
     * 新增：针对非淘宝图片、非vvic图片做兼容
     */
    public static String getFullUrlWithItemType(int itemType, String url) {
//        if (TextUtils.isEmpty(url)) {
//            return "";
//        }
//
//        if (!url.contains(".alicdn.com") && !url.contains(".vvic.com")) {
//            return url;
//        }
//
//        if (itemType == 0) {
//            // 淘宝图片
//            return url + "_260x260.jpg";
//        } else {
//            // VVIC图片
//            return url + "?x-oss-process=image/resize,pad,h_260,w_260";
//        }
        return getCompressionImgUrl(itemType, url, String.valueOf(360));
    }

    /**
     * 获取完整的图片Url
     */
    public static String getFullUrlWithItemType(String url) {
        //默认采用淘宝图片
        return getFullUrlWithItemType(0, url);
    }

    /**
     * 获取压缩后的完整的图片Url
     * <p>
     * 根据ItemType的不同，获取完整的图片Url由于淘宝图片和VVIC对图片的切割方式不同，因此做的处理
     *
     * @param url       原地址
     * @param imageType 图片类型
     * @param size      图片大小
     * @return 压缩后的图片地址
     */
    public static String getCompressionImgUrl(int imageType, String url, String size) {
        String compressionImgUrl = "";

        if (TextUtils.isEmpty(url))
            return compressionImgUrl;

        if (!url.contains(".vvic.com") && !url.contains(".alicdn.com"))
            return url;

        if (url.contains(".vvic.com")) {
            compressionImgUrl = url + "?x-oss-process=image/resize,l_" + size + ",m_mfit";
        } else if (url.contains(".alicdn.com")) {
            compressionImgUrl = url + "_"+ size + "x" + size + ".jpg";
        }else {
            compressionImgUrl = url;
        }
        return compressionImgUrl;
    }

    /**
     * 获取压缩后的图片路径
     */
    public static String getCompressionImgUrl(int imageType, String url) {
        return getCompressionImgUrl(imageType, url, String.valueOf(400));
    }

    /**
     * 获取压缩后的图片路径
     */
    public static String getCompressionImgUrlDefNoAli(String url) {
        return getCompressionImgUrl(1, url);
    }


    /**
     * 判断是否是Email地址
     */
    public static boolean isEmailAddress(String address) {
        if (TextUtils.isEmpty(address))
            return false;
        String reg = "^\\w+([-+.]\\w*)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    /**
     * 判断字符串是否包含数字和字母
     */
    public static boolean isContainNumberAndLetter(String text) {
        boolean containNumber = false;
        boolean containLetter = false;
        char[] chars = text.toCharArray();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                containNumber = true;
            } else if (Character.isLetter(c)) {
                containLetter = true;
            }
        }
        return containNumber && containLetter;
    }

    /**
     * 计算srcString中包含多少个target
     */
    public static int strCount(String srcString, String target) {
        int count;
        int i = srcString.length() - srcString.replace(target, "").length();
        count = i / target.length();
        return count;
    }

    /**
     * 判断 在多个srcStrings中是否包含keyword，
     *
     * @param keyword    搜索关键字
     * @param srcStrings 源字符串数据
     * @return 返回是否含有的结果
     */
    public static boolean isContainKeyWord(String keyword, String... srcStrings) {
        boolean isContain = false;
        if (!TextUtils.isEmpty(keyword) && srcStrings.length > 0) {
            for (String src : srcStrings) {
                if (TextUtils.isEmpty(src)) {
                    continue;
                }
                if (src.contains(keyword)) {
                    isContain = true;
                    break;
                }
            }
        }
        return isContain;
    }

    public static boolean isContainKeyWordIgnoreCase(String keyword, String... srcStrings) {
        boolean isContain = false;
        if (!TextUtils.isEmpty(keyword) && srcStrings.length > 0) {
            for (String src : srcStrings) {
                if (TextUtils.isEmpty(src)) {
                    continue;
                }
                if (src.toLowerCase().contains(keyword.toLowerCase())) {
                    isContain = true;
                    break;
                }
            }
        }
        return isContain;
    }

    /**
     * 计算两个字符型的浮点数是否相等
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean equalFloatString(String v1, String v2) {
        try {
            float v1Float = Float.valueOf(v1);
            float v2Float = Float.valueOf(v2);

            return v1Float == v2Float;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 判断字符串是否全部为数据
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
