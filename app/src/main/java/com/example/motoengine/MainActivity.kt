package com.example.motoengine

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.motoengine.databinding.ActivityMainBinding
import java.util.ArrayList
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY_PROGRESS = "Progress"
    }

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: DataViewModel
    private val mHandle: Handler = Handler(Looper.myLooper()!!)
    private val mMyHandle: MyHandler = MyHandler(Looper.myLooper()!!)
    private val mSpeedHandle: SpeedHandler = SpeedHandler(Looper.myLooper()!!)
    private val rotatingSpeedViewList = ArrayList<View>()


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

    private val mRunnableSpeed = object : Runnable {
        override fun run() {
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

        rotatingSpeedViewList.apply {
            add(mBinding.tvRotating0)
            add(mBinding.tvRotating1)
            add(mBinding.tvRotating2)
            add(mBinding.tvRotating3)
            add(mBinding.tvRotating4)
            add(mBinding.tvRotating5)
            add(mBinding.tvRotating6)
            add(mBinding.tvRotating7)
            add(mBinding.tvRotating8)
            add(mBinding.tvRotating9)
            add(mBinding.tvRotating10)
            add(mBinding.tvRotating11)
            add(mBinding.tvRotating12)
        }

        mBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.progressSpeed.progress = mBinding.seekBar.progress.toFloat()
                mBinding.tvSpeed.text = mBinding.progressSpeed.progress.toInt().toString()

                val message = Message.obtain(mHandle);
                val bundle = Bundle()
                bundle.putInt(KEY_PROGRESS, progress)
                message.data = bundle
                mSpeedHandle.sendMessage(message)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun setRotatingSpeed(progress: Int) {
        when {
            progress in 0..999 -> {
                CommonUtils.scaleView(mBinding.tvRotating0, 1.5f)
            }

            progress in 1000..1999 -> {
                CommonUtils.scaleView(mBinding.tvRotating1, 1.5f)
            }

            progress in 2000..2999 -> {
                CommonUtils.scaleView(mBinding.tvRotating2, 1.5f)
            }

            progress in 3000..3999 -> {
                CommonUtils.scaleView(mBinding.tvRotating3, 1.5f)
            }

            progress in 4000..4999 -> {
                CommonUtils.scaleView(mBinding.tvRotating4, 1.5f)
            }

            progress in 5000..5999 -> {
                CommonUtils.scaleView(mBinding.tvRotating5, 1.5f)
            }

            progress in 6000..6999 -> {
                CommonUtils.scaleView(mBinding.tvRotating6, 1.5f)
            }

            progress in 7000..7999 -> {
                CommonUtils.scaleView(mBinding.tvRotating7, 1.5f)
            }

            progress in 8000..8999 -> {
                CommonUtils.scaleView(mBinding.tvRotating8, 1.5f)
            }

            progress in 9000..9999 -> {
                CommonUtils.scaleView(mBinding.tvRotating9, 1.5f)
            }

            progress in 10000..10999 -> {
                CommonUtils.scaleView(mBinding.tvRotating10, 1.5f)
            }

            progress in 11000..11999 -> {
                CommonUtils.scaleView(mBinding.tvRotating11, 1.5f)
            }

            progress >= 12000 -> {
                CommonUtils.scaleView(mBinding.tvRotating12, 1.5f)
            }
        }
    }

    private fun initRotatingSpeed(view: View) {
        view.clearAnimation()
        CommonUtils.scaleView(view, 1f);
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

    inner class SpeedHandler(looper: Looper) : Handler(looper) {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            val speed = msg.data.getInt(KEY_PROGRESS)
            setRotatingSpeed(speed)
        }

    }

    inner class MyHandler(looper: Looper) : Handler(looper) {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            val angle = msg.data.getInt(KEY_PROGRESS)
            var angleNew = angle - 270
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