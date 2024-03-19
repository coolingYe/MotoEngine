package com.example.motoengine

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.OrientationEventListener
import android.widget.SeekBar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.bluetooth.BluetoothService
import com.example.motoengine.databinding.ActivityMotoBinding
import kotlin.math.abs

class MotoActivity : BaseActivity<ActivityMotoBinding>() {

    companion object {
        private const val KEY_PROGRESS = "Progress"
    }

    private lateinit var mViewModel: DataViewModel
    private var mBinder: BluetoothService? = null
    private val mHandle: Handler = Handler(Looper.myLooper()!!)
    private val mMyHandle: MyHandler = MyHandler(Looper.myLooper()!!)
    private val mSpeedHandle: SpeedHandler = SpeedHandler(Looper.myLooper()!!)

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

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(MainActivity.TAG, "onServiceConnected: ")
            mBinder = (service as BluetoothService.ServiceBinder).service
            mBinder?.setOnEcuDataChangeListener { ecuData ->
                if (ecuData.engineRmp != null) {
                    if (ecuData.engineRmp == -1) return@setOnEcuDataChangeListener
                    mBinding.progressSpeed.progress = ecuData.engineRmp!!.toFloat()
                    mBinding.tvSpeed.text = ecuData.engineRmp.toString()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(MainActivity.TAG, "onServiceDisconnected: ")
        }

    }

    override fun getLayout(): Int = R.layout.activity_moto

    override fun initViews() {
        mViewModel = ViewModelProvider(this)[DataViewModel::class.java]
        val mOrientoinListener = MyOrientoinListener(this)
        mOrientoinListener.enable()

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
                mBinding.tvSpeed.text = mBinding.progressSpeed.progress.toInt().toString()

                val message = Message.obtain(mHandle)
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

    override fun initListeners() {

    }

    override fun initData() {

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
            rotatingSpeed += 150
        } else {
            if (rotatingSpeed <= 0) {
                rotatingSpeed = 0
                mHandle.removeCallbacksAndMessages(null)
                bindService(
                    Intent(this, BluetoothService::class.java),
                    mServiceConnection,
                    BIND_AUTO_CREATE
                )
                return
            }
            hasRotatingSpeed = true
            rotatingSpeed -= 150
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
            Log.d(this@MotoActivity.packageName.toString(), orientation.toString())
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
            initRotatingSpeed(speed)
        }

    }

    private fun initRotatingSpeed(speed: Int) {
        mBinding.tvRotating0.setCurrentValue(speed)
        mBinding.tvRotating1.setCurrentValue(speed)
        mBinding.tvRotating2.setCurrentValue(speed)
        mBinding.tvRotating3.setCurrentValue(speed)
        mBinding.tvRotating4.setCurrentValue(speed)
        mBinding.tvRotating5.setCurrentValue(speed)
        mBinding.tvRotating6.setCurrentValue(speed)
        mBinding.tvRotating7.setCurrentValue(speed)
        mBinding.tvRotating8.setCurrentValue(speed)
        mBinding.tvRotating9.setCurrentValue(speed)
        mBinding.tvRotating10.setCurrentValue(speed)
        mBinding.tvRotating11.setCurrentValue(speed)
        mBinding.tvRotating12.setCurrentValue(speed)
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