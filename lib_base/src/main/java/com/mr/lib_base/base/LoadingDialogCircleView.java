package com.mr.lib_base.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.mr.lib_base.util.VVICUtils;


/**
 * 加载对话框，圆形视图
 */
public class LoadingDialogCircleView extends View {

    private Paint mPaint; // 画笔

    private int strokeWidth;

    private int mWidth, mHeight;

    private float mStartAngle = -40f;

    private RectF mRectF;

    private boolean mStopFlag;

    private RotateAnim mRotateAnim;

    public LoadingDialogCircleView(Context context) {
        super(context);
        init(context);
    }

    public LoadingDialogCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mRectF = new RectF();
        strokeWidth = VVICUtils.dip2px(context, 2.5f);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRectF.set(strokeWidth, strokeWidth, mWidth - strokeWidth, mHeight - strokeWidth);
        canvas.drawArc(mRectF, mStartAngle, 260, false, mPaint);
    }

    private class RotateAnim extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            mStartAngle = -40 + interpolatedTime * 360;
            postInvalidate();
        }
    }

    /**
     * 开启转动
     */
    public void startLoading(boolean delayStart) {
        mRotateAnim = new RotateAnim();
        mRotateAnim.setDuration(1000);
        mRotateAnim.setInterpolator(new LinearInterpolator());
        mRotateAnim.setFillAfter(true);
        if (delayStart) {
            mRotateAnim.setStartOffset(200);
        }
        mRotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!mStopFlag) {
                    startLoading(false);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(mRotateAnim);
    }

    /**
     * 停止转动
     */
    public void stopLoading() {
        mStopFlag = true;
        if (mRotateAnim != null) {
            mRotateAnim.cancel();
        }
        clearAnimation();
    }

}
