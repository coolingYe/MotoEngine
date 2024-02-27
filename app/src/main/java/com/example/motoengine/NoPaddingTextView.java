package com.example.motoengine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;

public class NoPaddingTextView extends androidx.appcompat.widget.AppCompatTextView {
    public NoPaddingTextView(Context context) {
        super(context, null);
    }

    public NoPaddingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public NoPaddingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER_VERTICAL);
        setIncludeFontPadding(false);
        int height = getViewHeight(this);
        int textSize = (int) getTextSize() + 1;
        int padding = (int) (height - getTextSize());
        if (height / 2 == 0 && textSize % 2 == 0) {
            setPadding(0, -padding, 0, -padding);
        } else {
            padding -= 1;
            setPadding(0, -padding, 0, -padding);
        }
    }

    private static int getViewHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredHeight();
    }

}
