package com.example.motoengine.utils

import android.view.View
import androidx.core.view.ViewCompat


object CommonUtils {

    fun scaleView(view: View, scale: Float) {
        ViewCompat.animate(view)
            .setDuration(20)
            .scaleX(scale)
            .scaleY(scale)
            .start()
    }
}