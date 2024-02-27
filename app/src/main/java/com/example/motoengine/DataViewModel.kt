package com.example.motoengine

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel : ViewModel() {

    val speed: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
}