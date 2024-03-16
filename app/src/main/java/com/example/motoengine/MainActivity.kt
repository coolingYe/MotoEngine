package com.example.motoengine

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.base.Constant
import com.example.motoengine.bluetooth.BluetoothEngine
import com.example.motoengine.bluetooth.BluetoothListActivity
import com.example.motoengine.databinding.ActivityMainBinding
import pub.devrel.easypermissions.EasyPermissions
import java.util.Locale


class MainActivity : BaseActivity<ActivityMainBinding>(), EasyPermissions.PermissionCallbacks {

    private lateinit var mBluetoothManager: BluetoothManager

    private lateinit var mBluetoothEngine: BluetoothEngine

    private val mHandle: Handler = Handler(Looper.myLooper()!!)

    companion object {
        const val TAG = "MainActivity"
        const val RPM = "010C"
    }

    private val mRunnableSend = object : Runnable {
        override fun run() {
            if (mBluetoothEngine.state != BluetoothEngine.STATE_CONNECTED) {
                return
            }

            mBluetoothEngine.write(RPM.toByteArray())
            mHandle.postDelayed(this, 100)
        }
    }

    private var intentActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        run {
            if (it.resultCode == RESULT_OK) {
                it.data?.getStringExtra("address")?.let { address ->
                    connectBluetooth(address, true)
                }
            }
        }
    }
    override fun getLayout(): Int = R.layout.activity_main

    override fun initViews() {
        mBluetoothEngine = BluetoothEngine(this, mBluetoothHandler)
        mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager

    }

    override fun initListeners() {
        mBinding.btnConnect.setOnClickListener {
            checkPermissions()
        }
    }

    override fun initData() {

    }

    /**
     * Initiate a connect to the selected bluetooth device
     *
     * @param address bluetooth device address
     * @param secure  flag to indicate if the connection shall be secure, or not
     */
    private fun connectBluetooth(address: String, secure: Boolean) {
        val bluetoothDevice = mBluetoothManager.adapter.getRemoteDevice(address)
        mBluetoothEngine.connect(bluetoothDevice, secure)
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val permissions = arrayOf(Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN)
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            Log.d("checkBluetoothPermissions", "No Bluetooth Permissions")
            EasyPermissions.requestPermissions(this, "Please provide permissions", 1, *permissions)
        } else initBluetooth()
    }

    private fun initBluetooth() {
        if (mBluetoothManager.adapter.isEnabled.not()) {
            showToast("Please turn on Bluetooth")
            return
        }
        intentActivityResult?.launch(Intent(this, BluetoothListActivity::class.java))
    }

    private fun initObdService() {


    }

    private val mBluetoothHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constant.MESSAGE_STATE_CHANGE -> {
                    when (msg.arg1) {
                        BluetoothEngine.STATE_CONNECTING -> {
                            Log.d(TAG, "STATE_CONNECTING")
                        }
                        BluetoothEngine.STATE_CONNECTED -> {
                            Log.d(TAG, "STATE_CONNECTED")
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
                    mBinding.tvRpm.text = "RPM: ${showEngineRPM(readMessage)}"
                    Log.d(TAG, "RPM: ${showEngineRPM(readMessage)}")
                }
            }
        }
    }

    private fun cleanResponse(text: String): String {
        var text = text
        text = text.trim { it <= ' ' }
        text = text.replace("\t", "")
        text = text.replace(" ", "")
        text = text.replace(">", "")
        return text
    }

    private fun showEngineRPM(buffer: String): Int {
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
                Log.e(TAG, e.message.toString())
            } catch (e: NumberFormatException) {
                Log.e(TAG, e.message.toString())
            }
        }
        return -1
    }

    override fun onDestroy() {
        mHandle.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        initBluetooth()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

}