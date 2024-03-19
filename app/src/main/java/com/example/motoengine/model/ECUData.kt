package com.example.motoengine.model

data class ECUData(
    var engineRmp: Int? = null,
    var engineLoad: String? = null,
    var coolantTemp: String? = null,
    var vehicleSpeed: String? = null,
    var currentGear: String? = null,
    var fuelTankLevel: String? = null,
    var timeSinceEngStart: String ?= null,
    var controlModuleVolt: String ?= null,
    var distanceTraveled: String ?= null
)