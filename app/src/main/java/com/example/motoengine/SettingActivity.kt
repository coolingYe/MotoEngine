package com.example.motoengine

import android.view.View
import android.widget.CheckBox
import com.example.motoengine.base.BaseActivity
import com.example.motoengine.databinding.ActivitySettingBinding
import com.example.motoengine.utils.Constant
import com.example.motoengine.utils.SPUtils
import java.util.Locale

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    override fun getLayout(): Int = R.layout.activity_setting

    companion object {
        const val TAG = "SettingActivity"
        val mPIDs = arrayOf(
            Constant.PIDS_ENGINE_LOAD,
            Constant.PIDS_ENGINE_RMP,
            Constant.PIDS_VEHICLE_SPEED,
            Constant.PIDS_COOLANT_TEMP,
            Constant.PIDS_DISTANCE_TRAVELED,
            Constant.PIDS_CONTROL_MODULE_VOLT,
            Constant.PIDS_FUEL_TANK_LEVEL
        )
    }

    override fun initViews() {
        initFlowLayout()
        val sendDelayed = SPUtils.getInstance().getInt("SP_BT_SEND_DELAYED", 500).toString()
        mBinding.tvSendDelayedInput.setText(sendDelayed)
        val sendCycle = SPUtils.getInstance().getBoolean("SP_BT_SEND_CYCLE", false)
        mBinding.switchCycle.isChecked = sendCycle
    }

    override fun initListeners() {
        mBinding.btnCancel.setOnClickListener {
            finish()
        }

        mBinding.btnApply.setOnClickListener {
            val delayed = mBinding.tvSendDelayedInput.text.toString()
            if (delayed.isNotEmpty()) SPUtils.getInstance().put("SP_BT_SEND_DELAYED", delayed.toInt())
            finish()
        }

        mBinding.switchCycle.setOnCheckedChangeListener { _, isChecked ->
            SPUtils.getInstance().put("SP_BT_SEND_CYCLE", isChecked)
        }
    }

    override fun initData() {

    }

    private fun initFlowLayout() {
        mBinding.flowlayout.removeAllViews()
        for (text in mPIDs) {
            val view = View.inflate(this, R.layout.layout_checkbox_view, null)
            val checkBox = view.findViewById<CheckBox>(R.id.checkbox)
            checkBox.tag = text
            checkBox.isChecked = SPUtils.getInstance().getBoolean(checkBox.tag as String)
            checkBox.text = capitalizeFirstLetter(text)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                SPUtils.getInstance().put(checkBox.tag as String, isChecked)
            }
            mBinding.flowlayout.addView(view)
        }
    }

    private fun capitalizeFirstLetter(input: String): String {
        if (input.isEmpty()) {
            return input
        }

        return input.substring(0, 1).uppercase(Locale.ROOT) + input.substring(1)
            .lowercase(Locale.ROOT)
    }
}