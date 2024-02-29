package com.example.motoengine

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.OrientationEventListener
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.motoengine.databinding.ActivityMainBinding
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY_PROGRESS = "Progress"
    }

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: DataViewModel
    private val mHandle: Handler = Handler(Looper.myLooper()!!)
    private val mMyHandle: MyHandler = MyHandler(Looper.myLooper()!!)


    private val mRunnableSelfTest1 = object : Runnable {
        override fun run() {
            selfTestSpeed()
            mHandle.postDelayed(this, 150)
        }
    }

    private val mRunnableSelfTest2 = object : Runnable {
        override fun run() {
            selfTestRotatingSpeed()
            mHandle.postDelayed(this, 10)
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
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        mHandle.post(mRunnableSelfTest1)
        mHandle.post(mRunnableSelfTest2)

        mBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.progressSpeed.progress = mBinding.seekBar.progress.toFloat()
                mBinding.tvSpeed.text = mBinding.progressSpeed.progress.toString()
                //mBinding.tvSpeed.text = (mBinding.progressSpeedLine.progress / 40).toInt().toString()
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
        if (rotatingSpeed <= 10850 && !hasRotatingSpeed) {
            rotatingSpeed += 50
        } else {
            if (rotatingSpeed <= 0) {
                rotatingSpeed = 0
                mHandle.removeCallbacksAndMessages(null)
                return
            }
            hasRotatingSpeed = true
            rotatingSpeed -= 50
        }
        mBinding.progressSpeed.progress = rotatingSpeed.toFloat()
        mBinding.tvSpeed.text = rotatingSpeed.toString()
        if (rotatingSpeed == 10900) {
            Thread.sleep(1000)
        }
    }

    private fun updateData(speed: Int) {

    }

    inner class MyOrientoinListener(context: Context) : OrientationEventListener(context) {

        @SuppressLint("SetTextI18n")
        override fun onOrientationChanged(orientation: Int) {
            Log.d(this@MainActivity.packageName.toString(), orientation.toString())
            val message = Message.obtain(mHandle);
            val bundle = Bundle()
            bundle.putInt(KEY_PROGRESS, orientation)
            message.data = bundle
            mMyHandle.sendMessage(message)
        }
    }

    inner class MyHandler(looper: Looper) : Handler(looper) {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            val angle = msg.data.getInt(KEY_PROGRESS)
            var angleNew =  angle - 270
            Log.d("angle----->", angle.toString())
            Log.d("angleNew----->", angleNew.toString())
            when {
                angle == -1 || angle == 271 -> {
                    angleNew = 0
                }
                angle == 0 || angleNew == -270 -> {
                    angleNew = 90
                }
            }
            if (angleNew !in -90..90) {
                if (angle < 90) {
                    angleNew = 90
                }
                if (angle in 91..179) {
                    angleNew = -90
                }
            }
            mBinding.halfCircleProgressBar.progress = angleNew
            mBinding.tvCarAngle.text = abs(angleNew).toString() + "°"
        }

    }
}