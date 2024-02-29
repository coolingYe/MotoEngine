package com.example.motoengine;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * 1. 由于初始设置进度为0时代码控制实际绘制不为0，是一个圆形，因此高:宽不能大于一定的数值，否则看起来初始就是进度100%
 * 如果有别的方案解决初始进度0时的显示问题，可以优化代码
 * 2. 默认渐变色横向绘制，因为就一个进度条，左上右下什么的看不出来，也没见过从上到下绘制渐变色的横向进度条
 */
public class LineProgressView extends View {
    private static final String TAG = "GradientProgressView";

    private final Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint mProgressPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private @ColorInt
    final int mStartColor;
    private @ColorInt
    final int mEndColor;

    private float skewX = 0; // X轴上的倾斜角度
    private float skewY = 0; // Y轴上的倾斜角度

    private float mProgress = -1;
    private int mWidth;
    private int mHeight;

    private int total = 12000;

    public LineProgressView(Context context) {
        this(context, null);
    }

    public LineProgressView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LineProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientProgressView);

        mStartColor = a.getColor(R.styleable.GradientProgressView_xStartColor, Color.TRANSPARENT);
        mEndColor = a.getColor(R.styleable.GradientProgressView_xEndColor, Color.TRANSPARENT);
        mProgress = a.getInt(R.styleable.GradientProgressView_progress, 0);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mProgress >= 1)
            drawProgress(canvas);
    }

    /**
     * 绘制填充色
     *
     * @param canvas 画布
     */
    private void drawProgress(Canvas canvas) {

        float ratio = (mProgress / total);

        Shader shader = new LinearGradient(0, 0, mWidth * ratio, 0, mStartColor,
                mStartColor, Shader.TileMode.REPEAT);
        mProgressPaint.setShader(shader);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(0xFF007cc4);
        mProgressPaint.setStrokeWidth(4);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        mProgressPaint1.setShader(shader);
        mProgressPaint1.setAntiAlias(true);
        mProgressPaint1.setColor(0xFF007cc4);
        mProgressPaint1.setStrokeWidth(4);
        mProgressPaint1.setStrokeCap(Paint.Cap.ROUND);

        mLinePaint.setColor(0xFFFFFFFF);
        mLinePaint.setStrokeWidth(18);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);

        for (int i = 0; i <= mWidth * ratio; i += 12) {
            canvas.drawLine(i + 155, 0, i + 15, mHeight, mProgressPaint1);
        }

        canvas.drawLine(mWidth * ratio + 155, 0, mWidth * ratio + 15, mHeight, mLinePaint);

    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        if (progress < 0) {
            mProgress = 0;
        } else if (progress > total) {
            mProgress = total;
        } else {
            mProgress = progress;
        }
        invalidate();
    }
}