package com.example.motoengine.bluetooth

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.motoengine.R
import com.example.motoengine.adapter.BluetoothListAdapter
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.databinding.ActivityBluetoothListBinding
import com.example.motoengine.model.Bluetooth

class BluetoothListActivity : BaseActivity<ActivityBluetoothListBinding>() {

    private lateinit var mBluetoothManager: BluetoothManager
    private lateinit var mBluetoothListAdapter: BluetoothListAdapter

    companion object {
        val TAG = BluetoothListActivity::class.java.simpleName
    }

    override fun getLayout(): Int = R.layout.activity_bluetooth_list

    override fun initViews() {
        mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        if (mBluetoothManager.adapter.isEnabled.not()) {
            showToast("No devices")
            finish()
            return
        }

        mBluetoothListAdapter = BluetoothListAdapter(emptyList()) { bluetooth ->
            val intent = Intent()
            intent.putExtra("address", bluetooth.address)
            setResult(RESULT_OK, intent)
            finish()
        }
        mBinding.rvBluetoothList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.rvBluetoothList.adapter = mBluetoothListAdapter
    }

    override fun initListeners() {

    }

    override fun initData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val pairedDevice = mBluetoothManager.adapter.bondedDevices
        val bluetoothData = ArrayList<Bluetooth>()
        if (pairedDevice.size > 0) {
            pairedDevice.forEach { device ->
                bluetoothData.add(Bluetooth(device.name, device.address))
            }
        } else showToast("No Devices Paired")
        mBluetoothListAdapter.updateList(bluetoothData)
    }
}