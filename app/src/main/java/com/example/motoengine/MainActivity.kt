package com.example.motoengine

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.bluetooth.BluetoothEngine
import com.example.motoengine.bluetooth.BluetoothListActivity
import com.example.motoengine.bluetooth.BluetoothService
import com.example.motoengine.databinding.ActivityMainBinding
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : BaseActivity<ActivityMainBinding>(), EasyPermissions.PermissionCallbacks {

    private lateinit var mBluetoothManager: BluetoothManager

    private lateinit var mBinder: BluetoothService

    private val mHandle = Handler(Looper.myLooper()!!)

    private lateinit var translateAnimation1: AnimatorSet
    private lateinit var translateAnimation2: AnimatorSet
    private lateinit var translateAnimation3: AnimatorSet
    private lateinit var translateAnimation4: AnimatorSet

    private var isAdd = false

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

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        @SuppressLint("SetTextI18n")
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected: ")
            mBinder = (service as BluetoothService.ServiceBinder).service
            mBinder.setOnEcuDataChangeListener { ecuData ->
                addLog("Eng. RMP: ${ecuData.engineRmp}")
            }

            mBinder.setOnConnectSuccessListener {

            }

            mBinder.setOnLogcatListener {
                addLog(it)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: ")
        }

    }

    override fun getLayout(): Int = R.layout.activity_main

    @SuppressLint("ResourceType")
    override fun initViews() {
        mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bindService(
            Intent(this, BluetoothService::class.java),
            mServiceConnection,
            BIND_AUTO_CREATE
        )

        mBinding.tvEngineData.movementMethod = ScrollingMovementMethod()

        translateAnimation1 =
            AnimatorInflater.loadAnimator(this, R.anim.add_bill_anim) as AnimatorSet
        translateAnimation2 =
            AnimatorInflater.loadAnimator(this, R.anim.add_bill_anim) as AnimatorSet
        translateAnimation3 =
            AnimatorInflater.loadAnimator(this, R.anim.add_bill_anim) as AnimatorSet
        translateAnimation4 =
            AnimatorInflater.loadAnimator(this, R.anim.add_bill_anim) as AnimatorSet

    }

    override fun initListeners() {
        mBinding.floatingBtnConnect.setOnClickListener {
            checkPermissions()
        }

        mBinding.floatingBtnCar.setOnClickListener {
            startActivity(Intent(this, MotoActivity::class.java))
        }

        mBinding.floatingBtnSet.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        mBinding.floatingBtnMain.setOnClickListener {
            isAdd = isAdd.not()
            mBinding.floatingBtnMain.setImageResource(if (isAdd) R.drawable.icon_more_1 else R.drawable.icon_more)
            mBinding.floatingBtnSend.isVisible = isAdd
            mBinding.floatingBtnCar.isVisible = isAdd
            mBinding.floatingBtnConnect.isVisible = isAdd
            mBinding.floatingBtnSet.isVisible = isAdd
            if (isAdd) {
                translateAnimation1.setTarget(mBinding.floatingBtnSet)
                translateAnimation1.startDelay = 100
                translateAnimation1.start()
                translateAnimation2.setTarget(mBinding.floatingBtnConnect)
                translateAnimation2.startDelay = 150
                translateAnimation2.start()
                translateAnimation3.setTarget(mBinding.floatingBtnCar)
                translateAnimation3.startDelay = 200
                translateAnimation3.start()
                translateAnimation4.setTarget(mBinding.floatingBtnSend)
                translateAnimation4.startDelay = 250
                translateAnimation4.start()
            }
        }

        mBinding.floatingBtnSend.setOnClickListener {
            if (mBinder.mBluetoothEngine.state == BluetoothEngine.STATE_CONNECTED) {
                var strCMD = "010C"
                strCMD += '\r'
                mBinder.mBluetoothEngine.write(strCMD.toByteArray())
            }
        }

        mBinding.btnClear.setOnClickListener {
            mBinding.tvEngineData.text = ""
        }
    }

    override fun initData() {

    }

    fun addLog(log: String) {
        if (!TextUtils.isEmpty(log)){
            var msgInfo = log
            msgInfo += "\r\n"

            if (mBinding.tvEngineData.text.length > 1024 * 10){
                mBinding.tvEngineData.text = msgInfo
                mBinding.tvEngineData.scrollTo(0, 0)
            }
            else{
                mBinding.tvEngineData.append(msgInfo)
                val offset = getTextViewContentHeight(mBinding.tvEngineData)
                if (offset > mBinding.tvEngineData.height) {
                    mBinding.tvEngineData.scrollTo(0, offset - mBinding.tvEngineData.height)
                }
            }
        }
    }

    private fun getTextViewContentHeight(textView: TextView): Int {
        val layout = textView.layout
        val desired = layout.getLineTop(textView.lineCount)
        val padding = textView.compoundPaddingTop + textView.compoundPaddingBottom
        return desired + padding
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