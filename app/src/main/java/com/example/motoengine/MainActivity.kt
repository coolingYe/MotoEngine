package com.example.motoengine

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.bluetooth.BluetoothListActivity
import com.example.motoengine.bluetooth.BluetoothService
import com.example.motoengine.databinding.ActivityMainBinding
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : BaseActivity<ActivityMainBinding>(), EasyPermissions.PermissionCallbacks {

    private lateinit var mBluetoothManager: BluetoothManager

    private var binder: BluetoothService ?= null

    companion object {
        const val TAG = "MainActivity"
    }

    private var intentActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        run {
            if (it.resultCode == RESULT_OK) {
                it.data?.getStringExtra("address")?.let { address ->
                    binder?.connectBluetooth(address, true)
                }
            }
        }
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = (service as BluetoothService.ServiceBinder).service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: ")
        }

    }

    override fun getLayout(): Int = R.layout.activity_main

    override fun initViews() {
        mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bindService(Intent(this, BluetoothService::class.java), mServiceConnection, BIND_AUTO_CREATE)
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
        intentActivityResult.launch(Intent(this, BluetoothListActivity::class.java))
    }

    private fun initObdService() {


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