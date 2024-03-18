package com.example.motoengine.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.example.motoengine.MainActivity
import com.example.motoengine.utils.Constant
import com.example.motoengine.utils.ObdFactory.showEngineRPM
import java.util.Locale

class BluetoothService : Service() {

    private lateinit var mBluetoothManager: BluetoothManager

    private lateinit var mBluetoothEngine: BluetoothEngine

    private val mHandle: Handler = Handler(Looper.myLooper()!!)

    private val serviceBinder = ServiceBinder()

    private val mRunnableSend = object : Runnable {
        override fun run() {
            if (mBluetoothEngine.state != BluetoothEngine.STATE_CONNECTED) {
                return
            }

            mBluetoothEngine.write("010C".toByteArray())
            mHandle.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mBluetoothEngine = BluetoothEngine(this, mBluetoothHandler)
        mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
    }
    override fun onBind(intent: Intent): IBinder {
        return serviceBinder
    }


    inner class ServiceBinder : Binder() {
        val service: BluetoothService = this@BluetoothService
    }


    fun connectBluetooth(address: String, secure: Boolean) {
        val bluetoothDevice = mBluetoothManager.adapter.getRemoteDevice(address)
        mBluetoothEngine.connect(bluetoothDevice, secure)
    }

    private fun showToast(content: String) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        mHandle.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private val mBluetoothHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constant.MESSAGE_STATE_CHANGE -> {
                    when (msg.arg1) {
                        BluetoothEngine.STATE_CONNECTING -> {
                            Log.d(MainActivity.TAG, "STATE_CONNECTING")
                        }
                        BluetoothEngine.STATE_CONNECTED -> {
                            Log.d(MainActivity.TAG, "STATE_CONNECTED")
                            mHandle.post(mRunnableSend)
                        }
                        else -> {}
                    }
                }

                Constant.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    var readMessage = String(readBuf,0,msg.arg1)
                    readMessage = readMessage.trim()
                    readMessage = readMessage.uppercase(Locale.ROOT)
                    val lastChar = readMessage[readMessage.length - 1]
                    //lastChar need doing something
                    showEngineRPM(readMessage)
                }
            }
        }
    }
}