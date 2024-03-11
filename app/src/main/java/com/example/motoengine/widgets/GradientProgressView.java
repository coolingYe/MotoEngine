package com.example.motoengine.widgets;

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

import com.example.motoengine.R;

/**
 * 1. 由于初始设置进度为0时代码控制实际绘制不为0，是一个圆形，因此高:宽不能大于一定的数值，否则看起来初始就是进度100%
 * 如果有别的方案解决初始进度0时的显示问题，可以优化代码
 * 2. 默认渐变色横向绘制，因为就一个进度条，左上右下什么的看不出来，也没见过从上到下绘制渐变色的横向进度条
 */
public class GradientProgressView extends View {
    private static final String TAG = "GradientProgressView";

    private final Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private @ColorInt
    final int mStartColor;
    private @ColorInt
    final int mEndColor;

    private final float topLeftRadius;
    private final float topRightRadius;
    private final float bottomLeftRadius;
    private final float bottomRightRadius;
    private final float mCornersRadius;

    private float skewX = 0; // X轴上的倾斜角度
    private float skewY = 0; // Y轴上的倾斜角度

    private float mProgress = -1;
    private int mWidth;
    private int mHeight;

    private int total = 6000;

    public GradientProgressView(Context context) {
        this(context, null);
    }

    public GradientProgressView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GradientProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientProgressView);

        mStartColor = a.getColor(R.styleable.GradientProgressView_xStartColor, Color.TRANSPARENT);
        mEndColor = a.getColor(R.styleable.GradientProgressView_xEndColor, Color.TRANSPARENT);
        mCornersRadius = a.getDimension(R.styleable.GradientProgressView_xCornersRadius, 0);
        topLeftRadius = a.getDimension(R.styleable.GradientProgressView_topLeftCornersRadius, 0);
        topRightRadius = a.getDimension(R.styleable.GradientProgressView_topRightCornersRadius, 0);
        bottomLeftRadius = a.getDimension(R.styleable.GradientProgressView_bottomLeftCornersRadius, 0);
        bottomRightRadius = a.getDimension(R.styleable.GradientProgressView_bottomRightCornersRadius, 0);
        skewX = a.getDimension(R.styleable.GradientProgressView_skewX, 0);
        skewY = a.getDimension(R.styleable.GradientProgressView_skewY, 0);
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
        mProgressPaint.setStyle(Paint.Style.FILL);
        PathEffect effect = new DashPathEffect(new float[]{5, 4}, 0); //设置虚线
        mProgressPaint.setPathEffect(effect);

        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStrokeWidth(10);

        if (skewX != 0) {
            Path path = new Path();
            path.addRoundRect(new RectF(0, 0, mWidth * ratio, mHeight),
                    new float[]{
                            topLeftRadius, topLeftRadius,
                            topRightRadius, topRightRadius,
                            bottomRightRadius, bottomRightRadius,
                            bottomLeftRadius, bottomLeftRadius
                    }, Path.Direction.CW); // 添加一个有弧度的矩形路径
            Matrix matrix = new Matrix();
            matrix.setSkew(skewX / 180 * (float) Math.PI, 0f, 0f, 500); //倾斜变换
            path.transform(matrix); // 将路径进行倾斜变换
            canvas.drawPath(path, mProgressPaint); // 使用画布绘制路径

            canvas.drawLine(mWidth * ratio + 185, 0, mWidth * ratio + 45, mHeight, mLinePaint);
        } else {
            canvas.drawRoundRect(0, 0, mWidth * ratio, mHeight, mCornersRadius, mCornersRadius, mProgressPaint);
        }
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