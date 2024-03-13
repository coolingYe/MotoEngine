package com.example.motoengine

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.databinding.ActivityMotoBinding

class BluetoothListActivity : BaseActivity<ActivityMotoBinding>() {

    private lateinit var mBluetoothManager: BluetoothManager
    companion object {
        val TAG = BluetoothListActivity::class.java.simpleName
    }
    override fun getLayout(): Int = R.layout.activity_bluetooth_list

    override fun initViews() {
        mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager

        if (mBluetoothManager.adapter.isEnabled.not()) {
            showToast("not devices")
            finish()
            return
        }

        //TODO

    }

    override fun initListeners() {

    }

    override fun initData() {

    }
}