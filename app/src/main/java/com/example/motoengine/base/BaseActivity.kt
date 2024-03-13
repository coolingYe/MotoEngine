package com.example.motoengine.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T: ViewDataBinding> : AppCompatActivity() {
    lateinit var mBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, getLayout())
        initViews()
        initListeners()
        initData()
    }

    abstract fun getLayout(): Int

    abstract fun initViews()

    abstract fun initListeners()

    abstract fun initData()

    fun showToast(res: String) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
    }
}