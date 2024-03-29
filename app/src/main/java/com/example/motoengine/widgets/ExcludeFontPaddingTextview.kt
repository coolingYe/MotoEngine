package com.example.motoengine.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.LineHeightSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.motoengine.utils.CommonUtils
import java.lang.Math.abs
import java.util.Currency
import kotlin.math.max
import kotlin.math.min

class ExcludeFontPaddingTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        includeFontPadding = false
    }

    private var minValue = 0
    private var currentValue = 0
    private var maxValue = 0

    fun setCurrentValue(currentValue: Int) {
        this.currentValue = currentValue
        if (text.toString() != "" && !text.contains("°")) {
            minValue = Integer.parseInt(text.toString()) * 1000
            maxValue = minValue + 1000
            if (currentValue in minValue..<maxValue) {
                CommonUtils.scaleView(this, 1.3f)
            } else {
                clearAnimation()
                CommonUtils.scaleView(this, 1f)
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(getCustomText(text), type)
    }

    private fun getCustomText(text: CharSequence?): SpannableStringBuilder? {
        if (text == null) {
            return null
        }

        return SpannableStringBuilder(text).apply {
            setSpan(
                object : LineHeightSpan {
                    override fun chooseHeight(
                        text: CharSequence,
                        start: Int,
                        end: Int,
                        spanstartv: Int,
                        lineHeight: Int,
                        fm: Paint.FontMetricsInt
                    ) {
                        val rect = Rect()
                        paint.getTextBounds(text.toString(), 0, text.length, rect)

                        val viewHeight = fm.descent - fm.ascent
                        val textHeight = max(textSize.toInt(), rect.bottom - rect.top)

                        val paddingTop = abs(fm.ascent - rect.top)
                        val paddingBottom = fm.descent - rect.bottom

                        val minPadding = min(paddingTop, paddingBottom)
                        val avgPadding = (viewHeight - textHeight) / 2

                        when {
                            avgPadding < minPadding -> {
                                fm.ascent += avgPadding
                                fm.descent -= avgPadding
                            }

                            paddingTop < paddingBottom -> {
                                fm.ascent = rect.top
                                fm.descent = textHeight + fm.ascent
                            }

                            else -> {
                                fm.descent = rect.bottom
                                fm.ascent = fm.descent - textHeight
                            }
                        }
                    }
                },
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

}