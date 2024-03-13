package com.example.motoengine

import android.bluetooth.BluetoothManager
import android.content.Intent
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.databinding.ActivityMainBinding


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var mBluetoothManager: BluetoothManager
    override fun getLayout(): Int = R.layout.activity_main

    override fun initViews() {
        mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
    }

    override fun initListeners() {
        mBinding.btnConnect.setOnClickListener {
            initBluetooth()
        }
    }

    override fun initData() {

    }

    private fun initBluetooth() {
        if (mBluetoothManager.adapter.isEnabled.not()) {
            showToast("Please turn on Bluetooth")
            return
        }
        startActivity(Intent(this, BluetoothListActivity::class.java))
    }

}