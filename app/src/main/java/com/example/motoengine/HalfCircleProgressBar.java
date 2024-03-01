package com.example.motoengine;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.motoengine.R;

public class HalfCircleProgressBar extends View {

    private int progress = 0;
    private int max = 180;

    private Paint backgroundPaint;
    private Paint progressPaint;

    public HalfCircleProgressBar(Context context) {
        this(context, null);
    }

    public HalfCircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HalfCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HalfCircleProgressBar);
        progress = a.getInt(R.styleable.HalfCircleProgressBar_progress, 0);
        a.recycle();
        init();
    }

    private void init() {
        // 初始化背景画笔
//        backgroundPaint = new Paint();
//        backgroundPaint.setColor(Color.GRAY);
//        backgroundPaint.setStyle(Paint.Style.STROKE);
//        backgroundPaint.setStrokeWidth(10);
//        backgroundPaint.setAntiAlias(true);

        // 初始化进度画笔
        progressPaint = new Paint();
        progressPaint.setColor(Color.WHITE);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(40);
        progressPaint.setAntiAlias(true);
    }

    private static int padding = 20;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth();
        int centerY = getHeight();
        int radius = Math.min(getWidth(), getHeight()) / 2;

        // 绘制背景半圆环
        RectF rectF = new RectF(padding, padding, centerX - padding, centerY * 2);
//        canvas.drawArc(rectF, 180, 180, false, backgroundPaint);

        // 绘制进度半圆环
        float sweepAngle = ((float) progress / max) * 180;
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgress() {
        return this.progress;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
