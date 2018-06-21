package com.iteast;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;


public class SectorProgress extends View {

    private static final int SP_BACKGROUND_COLOR = 0x66000000;
    private static final int SP_CORNER_RADIUS = 30;
    private static final int SP_BORDER_DISTANCE = 30;
    private static final int START_ANGLE = 270;

    private Paint mPaint;
    private int mCenterX;
    private int mCenterY;
    private int mSpBackgroundColor;
    private boolean mSpCenter;
    private int mSpCornerRadius;
    private int mSpBorderDistance;
    private int mSpCircleBackgroundColor;
    private int mSpOffsetCenterY;
    private int mSpCircleRadius;
    private int mCurrentAngle = -360;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCurrentAngle == 0) {
                startDisappearAnimation();
            } else {
                mCurrentAngle += 10;
                invalidate();
                mHandler.sendEmptyMessageDelayed(0, 100);
            }
        }
    };

    public SectorProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectorProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        //测试代码
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    public void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SectorProgress);
        mSpBackgroundColor = ta.getColor(R.styleable.SectorProgress_spBackgroundColor, SP_BACKGROUND_COLOR);
        mSpCenter = ta.getBoolean(R.styleable.SectorProgress_spCenter, true);
        mSpCornerRadius = ta.getDimensionPixelOffset(R.styleable.SectorProgress_spCornerRadius, SP_CORNER_RADIUS);
        mSpBorderDistance = ta.getDimensionPixelOffset(R.styleable.SectorProgress_spBorderDistance, SP_BORDER_DISTANCE);
        mSpCircleBackgroundColor = ta.getColor(R.styleable.SectorProgress_spCircleBackgroundColor, SP_BACKGROUND_COLOR);
        mSpOffsetCenterY = ta.getDimensionPixelOffset(R.styleable.SectorProgress_spOffsetCenterY, 0);
        mSpCircleRadius = ta.getDimensionPixelOffset(R.styleable.SectorProgress_spCircleRadius, 100);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mMeasuredWidth = getMeasuredWidth();
        int mMeasuredHeight = getMeasuredHeight();
        mCenterX = mMeasuredWidth / 2;
        mCenterY = mSpCenter ? mMeasuredHeight / 2 : mMeasuredHeight / 2 - mSpOffsetCenterY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int sc = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        //先绘制的是dst，后绘制的是src
        drawDst(canvas, mPaint);
        //设置xfermode
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        drawSrc(canvas, mPaint);
        //还原
        mPaint.setXfermode(null);
        canvas.restoreToCount(sc);

        drawCircleProgress(canvas, mPaint);
    }

    private void drawCircleProgress(Canvas canvas, Paint p) {
        //画中间扇形
        p.setColor(mSpBackgroundColor);
        RectF rectF = new RectF(
                mCenterX - mSpCircleRadius + mSpBorderDistance,
                mCenterY - mSpCircleRadius + mSpBorderDistance,
                mCenterX + mSpCircleRadius - mSpBorderDistance,
                mCenterY + mSpCircleRadius - mSpBorderDistance);
        canvas.drawArc(rectF, START_ANGLE, mCurrentAngle, true, p);
    }


    private void drawDst(Canvas canvas, Paint p) {
        //画圆角背景
        p.setColor(mSpCircleBackgroundColor);
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectF, mSpCornerRadius, mSpCornerRadius, p);
    }

    private void drawSrc(Canvas canvas, Paint p) {
        //画中心镂空
        p.setColor(0xFF66AAFF);
        canvas.drawCircle(mCenterX, mCenterY, mSpCircleRadius, p);
    }

    public void startDisappearAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mSpCircleRadius, getMeasuredWidth() > getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSpCircleRadius = (int) ((float) animation.getAnimatedValue());
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    /**
     * 设置当前进度
     *
     * @param progress
     */
    public void setCurrentPogress(int progress) {
        mCurrentAngle += progress;
        mCurrentAngle = mCurrentAngle > 0 ? 0 : mCurrentAngle;
        invalidate();
    }
}
