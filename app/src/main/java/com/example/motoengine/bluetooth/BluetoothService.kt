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
import com.example.motoengine.model.ECUData
import com.example.motoengine.utils.Constant
import com.example.motoengine.utils.ObdFactory
import com.example.motoengine.utils.SPUtils
import java.util.Locale

class BluetoothService : Service() {

    private lateinit var mBluetoothManager: BluetoothManager

    lateinit var mBluetoothEngine: BluetoothEngine

    private val mHandle: Handler = Handler(Looper.myLooper()!!)

    private val serviceBinder = ServiceBinder()

    lateinit var ecuDataCallback: (ECUData) -> Unit

    lateinit var logcatCallback: (String) -> Unit

    lateinit var successCallback: (BluetoothEngine)->Unit

    private var ecuData = ECUData()

    private var mCMDPointer = -1

    private val INIT_COMMANDS = arrayOf(
        Constant.PIDS_COOLANT_TEMP,
        Constant.PIDS_CONTROL_MODULE_VOLT,
        Constant.PIDS_ENGINE_RMP,
        Constant.PIDS_VEHICLE_SPEED,
        Constant.PIDS_ENGINE_LOAD,
        Constant.PIDS_FUEL_TANK_LEVEL,
        Constant.PIDS_DISTANCE_TRAVELED
    )

    companion object {
        const val TAG = "BluetoothService"
    }

    private val mRunnableSend = object : Runnable {
        override fun run() {
            if (mBluetoothEngine.state != BluetoothEngine.STATE_CONNECTED) {
                return
            }
            val pid = "01" + INIT_COMMANDS[mCMDPointer] + '\r'
            if (checkPID(pid).not()) return
            mBluetoothEngine.write(pid.toByteArray())
            mCMDPointer++
        }
    }

    private val mRunnableRMP = object : Runnable {
        override fun run() {
            if (SPUtils.getInstance().getBoolean("SP_BT_SEND_CYCLE", false).not()) {
                return
            }

            if (mBluetoothEngine.state != BluetoothEngine.STATE_CONNECTED) {
                return
            }
            mBluetoothEngine.write("010C\r".toByteArray())
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

    private fun sendCMD() {
        if (mCMDPointer >= INIT_COMMANDS.size) {
            mCMDPointer = -1
        }

        if (mCMDPointer < 0) {
            mCMDPointer = 0
        }
        mHandle.post(mRunnableSend)
    }

    fun checkPID(id: String): Boolean {
        Constant.valueToKeyPID[id]?.let {
           return SPUtils.getInstance().getBoolean(it, false)
        } ?: return false
    }

    private fun showToast(content: String) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }

    fun setOnEcuDataChangeListener(ecuDataCallback: (ECUData) -> Unit) {
        this.ecuDataCallback = ecuDataCallback
    }

    fun setOnConnectSuccessListener(callback: (BluetoothEngine) -> Unit) {
        this.successCallback = callback
    }

    fun setOnLogcatListener(callback: (String) -> Unit) {
        this.logcatCallback = callback
    }

    override fun onDestroy() {
        mHandle.removeCallbacksAndMessages(null)
        mBluetoothEngine.stop()
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
                            showToast("Connected")
                            successCallback.invoke(mBluetoothEngine)
                            mHandle.postDelayed(mRunnableRMP, 1000)
                        }

                        BluetoothEngine.STATE_LISTEN, BluetoothEngine.STATE_NONE -> {
                            Log.d(MainActivity.TAG, "Not Connected")
                        }

                        else -> {}
                    }
                }

                Constant.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    var readMessage = String(readBuf, 0, msg.arg1)
                    readMessage = readMessage.trim()
                    readMessage = readMessage.uppercase(Locale.ROOT)
                    val lastChar = readMessage[readMessage.length - 1]
                    //lastChar need doing something
                    Log.d(TAG, "result: $readMessage")
                    logcatCallback.invoke("result: $readMessage")

                    if (lastChar == '>') {
                        logcatCallback.invoke("After '>' result: $readMessage")
                        val ecuDataNew = ObdFactory.getEcuData(ecuData, readMessage)
                        ecuDataCallback.invoke(ecuDataNew)
                        val delayed = SPUtils.getInstance().getInt("SP_BT_SEND_DELAYED", 500).toLong()
                        mHandle.postDelayed(mRunnableRMP, delayed)
                    }
                }

                Constant.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray

                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    Log.d(TAG, "send: $writeMessage")
                    logcatCallback.invoke("send: $writeMessage")
                }
            }
        }
    }
}