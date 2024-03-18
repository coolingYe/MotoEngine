package com.example.motoengine.utils

import android.util.Log
import com.example.motoengine.MainActivity

object ObdFactory {

    fun showEngineRPM(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("410C")) {
            try {
                buf = buf.substring(buf.indexOf("410C"))
                val MSB = buf.substring(4, 6)
                val LSB = buf.substring(6, 8)
                val A = Integer.valueOf(MSB, 16)
                val B = Integer.valueOf(LSB, 16)
                return (A * 256 + B) / 4
            } catch (e: IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message.toString())
            } catch (e: NumberFormatException) {
                Log.e(MainActivity.TAG, e.message.toString())
            }
        }
        return -1
    }

    private fun cleanResponse(text: String): String {
        var text = text
        text = text.trim { it <= ' ' }
        text = text.replace("\t", "")
        text = text.replace(" ", "")
        text = text.replace(">", "")
        return text
    }
}