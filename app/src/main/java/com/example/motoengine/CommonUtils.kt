package com.example.motoengine

import android.view.View
import androidx.core.view.ViewCompat


object CommonUtils {

    fun scaleView(view: View, scale: Float) {
        ViewCompat.animate(view)
            .setDuration(200)
            .scaleX(scale)
            .scaleY(scale)
            .start()
    }
}