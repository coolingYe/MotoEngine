package com.example.motoengine

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.bluetooth.BluetoothEngine
import com.example.motoengine.bluetooth.BluetoothListActivity
import com.example.motoengine.bluetooth.BluetoothService
import com.example.motoengine.databinding.ActivityMainBinding
import com.example.motoengine.utils.Constant
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : BaseActivity<ActivityMainBinding>(), EasyPermissions.PermissionCallbacks {

    private lateinit var mBluetoothManager: BluetoothManager

    private lateinit var mBinder: BluetoothService

    private val mHandle = Handler(Looper.myLooper()!!)

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
        const val TAG = "MainActivity"
    }

    private var intentActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            run {
                if (it.resultCode == RESULT_OK) {
                    it.data?.getStringExtra("address")?.let { address ->
                        mBinder.connectBluetooth(address, true)
                    }
                }
            }
        }

    private val mRunnableSend = object : Runnable {
        override fun run() {
            if (mBinder.mBluetoothEngine.state != BluetoothEngine.STATE_CONNECTED) {
                return
            }
            val pId = INIT_COMMANDS[mCMDPointer]
            if (checkPID(pId).not()) return
            val cmd = "01$pId\r"
            mBinder.mBluetoothEngine.write(cmd.toByteArray())
            mCMDPointer++
        }
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected: ")
            mBinder = (service as BluetoothService.ServiceBinder).service
            mBinder.setOnEcuDataChangeListener { ecuData ->
                mBinding.tvEngineData.text = "Speed: ${ecuData.vehicleSpeed}\n" +
                        "Eng. RMP: ${ecuData.engineRmp}\n" +
                        "Temp: ${ecuData.coolantTemp}\n" +
                        "Fuel last: ${ecuData.fuelTankLevel}\n" +
                        "Travel distance: ${ecuData.distanceTraveled}\n" +
                        "Volt: ${ecuData.controlModuleVolt}\n" +
                        "Gear: ${ecuData.currentGear}\n"
                sendCMD()
            }

            mBinder.setOnConnectSuccessListener {
                sendCMD()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: ")
        }

    }

    private fun checkPID(pId: String): Boolean {
        return when(pId) {
            Constant.PIDS_COOLANT_TEMP -> {
                mBinding.checkboxTemp.isChecked
            }
            Constant.PIDS_CONTROL_MODULE_VOLT -> {
                mBinding.checkboxVolt.isChecked
            }
            Constant.PIDS_ENGINE_RMP -> {
                mBinding.checkboxRmp.isChecked
            }
            Constant.PIDS_VEHICLE_SPEED -> {
                mBinding.checkboxSpeed.isChecked
            }
            Constant.PIDS_ENGINE_LOAD -> {
                mBinding.checkboxEngineLoad.isChecked
            }
            Constant.PIDS_FUEL_TANK_LEVEL -> {
                mBinding.checkboxFuelTankLevel.isChecked
            }
            Constant.PIDS_DISTANCE_TRAVELED -> {
                mBinding.checkboxTraveled.isChecked
            }

            else -> {false}
        }
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

    override fun getLayout(): Int = R.layout.activity_main

    override fun initViews() {
        mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bindService(
            Intent(this, BluetoothService::class.java),
            mServiceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun initListeners() {
        mBinding.btnConnect.setOnClickListener {
            checkPermissions()
        }

        mBinding.btnEngine.setOnClickListener {
            if (mBinder.mBluetoothEngine.state == BluetoothEngine.STATE_CONNECTED)
                startActivity(Intent(this, MotoActivity::class.java))
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

    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
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
        if (mBinder.mBluetoothEngine.state == BluetoothEngine.STATE_NONE) {
            mBinder.mBluetoothEngine.start()
        }
        intentActivityResult.launch(Intent(this, BluetoothListActivity::class.java))
    }

    private fun cleanResponse(text: String): String {
        var text = text
        text = text.trim { it <= ' ' }
        text = text.replace("\t", "")
        text = text.replace(" ", "")
        text = text.replace(">", "")
        return text
    }

    override fun onDestroy() {
        unbindService(mServiceConnection)
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