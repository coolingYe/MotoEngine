package com.example.motoengine

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.motoengine.R
import com.example.motoengine.databinding.ActivityMainBinding
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: DataViewModel
    private val mHandle: Handler = Handler(Looper.myLooper()!!)

    private val mRunnableSelfTest1: Runnable = object : Runnable {
        override fun run() {
            selfTestSpeed()
            mHandle.postDelayed(this, 150)
        }
    }

    private val mRunnableSelfTest2: Runnable = object : Runnable {
        override fun run() {
            selfTestRotatingSpeed()
            mHandle.postDelayed(this, 80)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mViewModel = ViewModelProvider(this)[DataViewModel::class.java]
        initViews()
        val myOrientoinListener = MyOrientoinListener(this)
        myOrientoinListener.enable()
    }

    private fun initViews() {
        // 隐藏系统UI
        // 隐藏系统UI
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        decorView.systemUiVisibility = uiOptions

        // 设置全屏显示
        supportActionBar?.hide()
        mHandle.post(mRunnableSelfTest1)
        mHandle.post(mRunnableSelfTest2)

        mBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.progressSpeed.setProgress(mBinding.seekBar.progress)
                mBinding.speed = mBinding.seekBar.progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private var speed = 100
    private var rotatingSpeed = 0
    private var hasSpeed = false
    private var hasRotatingSpeed = false

    private fun selfTestSpeed() {
        if (speed < 199 && !hasSpeed) {
            speed += 11
        } else {
            if (speed <= 100) {
                speed = 0
                mBinding.speed = this.speed
                return
            }
            hasSpeed = true
            speed -= 11
        }
        mBinding.speed = this.speed
    }

    private fun selfTestRotatingSpeed() {
        if (rotatingSpeed < 12000 && !hasRotatingSpeed) {
            rotatingSpeed += 700
        } else {
            if (rotatingSpeed <= 0) {
                rotatingSpeed = 0
                return
            }
            hasRotatingSpeed = true
            rotatingSpeed -= 700
        }
        mBinding.progressSpeed.setProgress(rotatingSpeed)
    }

    private fun updateData(speed: Int) {

    }

    inner class MyOrientoinListener(context: Context) : OrientationEventListener(context) {

        @SuppressLint("SetTextI18n")
        override fun onOrientationChanged(orientation: Int) {
            Log.d(this@MainActivity.packageName.toString(), orientation.toString())
            val angle = abs(orientation - 270)
            if (angle == 271) {
                mBinding.tvCarAngle.text = "00°"
                mBinding.halfCircleProgressBar.setProgress(0)
            } else {
                mBinding.tvCarAngle.text = "$angle°"
                var progress = orientation - 270
                if (progress <= -90 && orientation >= 180) {
                    progress = -90
                } else if (orientation <= 1) {
                    progress = 90
                }
                Log.d("progress", progress.toString())
                mBinding.halfCircleProgressBar.setProgress(progress)
            }
        }
    }
}