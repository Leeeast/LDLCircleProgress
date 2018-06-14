package com.iteast;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SectorProgress extends View {

    private int W;
    private int H;
    private Paint mPaint;
    private int R;

    public SectorProgress(Context context) {
        this(context, null);
    }

    public SectorProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectorProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        R = getWidth() / 2;
        W = getWidth();
        H = getHeight();
        //背景色
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
        //画黄色圆形
        p.setColor(0x50000000);
        RectF rectF = new RectF(R - R / 2 + 15, R - R / 2 + 15, R + R / 2 - 15, R + R / 2 - 15);
        canvas.drawArc(rectF, 270, -350, true, p);
    }


    private void drawDst(Canvas canvas, Paint p) {
        //画黄色圆形
        p.setColor(0x50000000);
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectF, 10, 10, p);
    }

    private void drawSrc(Canvas canvas, Paint p) {
        //画蓝色矩形
        p.setColor(0xFF66AAFF);
        canvas.drawCircle(R, R, R / 2, p);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
