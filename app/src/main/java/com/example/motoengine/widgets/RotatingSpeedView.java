package com.example.motoengine.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.motoengine.R;

public class RotatingSpeedView extends ConstraintLayout {

    private static final String TAG = "RotatingSpeedView";

    private float mProgress = -1;
    private int mWidth;
    private int mHeight;

    public RotatingSpeedView(@NonNull Context context) {
        this(context, null);
    }

    public RotatingSpeedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatingSpeedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_rotating_speed_view, this);

    }


}
