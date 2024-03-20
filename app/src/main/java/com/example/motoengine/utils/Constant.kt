package com.example.motoengine.utils

object Constant {

    /**
     * Message types sent from the BluetoothChatService Handler
     */
    const val MESSAGE_STATE_CHANGE = 1
    const val MESSAGE_READ = 2
    const val MESSAGE_WRITE = 3
    const val MESSAGE_DEVICE_NAME = 4
    const val MESSAGE_TOAST = 5

    /**
     * Key names for preferences
     */
    const val DEVICE_NAME: String = "device_name"
    const val TOAST = "toast"
    const val PREF_AUTOHIDE = "autohide_toolbar"
    const val PREF_FULLSCREEN = "full_screen"
    const val PREF_AUTOHIDE_DELAY = "autohide_delay"

    val PIDS = listOf(
        "01", "02", "03", "04", "05", "06", "07", "08",
        "09", "0A", "0B", "0C", "0D", "0E", "0F", "10",
        "11", "12", "13", "14", "15", "16", "17", "18",
        "19", "1A", "1B", "1C", "1D", "1E", "1F", "20"
    )

    const val PIDS_ENGINE_LOAD = "PIDS_ENGINE_LOAD"
    const val PIDS_COOLANT_TEMP = "PIDS_COOLANT_TEMP"
    const val PIDS_ENGINE_RMP = "PIDS_ENGINE_RMP"
    const val PIDS_VEHICLE_SPEED = "PIDS_VEHICLE_SPEED"
    const val PIDS_TIME_SINCE_ENG_START = "PIDS_TIME_SINCE_ENG_START"
    const val PIDS_FUEL_TANK_LEVEL = "PIDS_FUEL_TANK_LEVEL"
    const val PIDS_DISTANCE_TRAVELED = "PIDS_DISTANCE_TRAVELED"
    const val PIDS_CONTROL_MODULE_VOLT = "PIDS_CONTROL_MODULE_VOLT"
    const val PIDS_TRANSMISSION_ACTUAL_GEAR = "PIDS_TRANSMISSION_ACTUAL_GEAR"

    val keyToValuePID = mapOf(
        PIDS_ENGINE_LOAD to "04",
        PIDS_COOLANT_TEMP to "05",
        PIDS_ENGINE_RMP to "0C",
        PIDS_VEHICLE_SPEED to "0D",
        PIDS_TIME_SINCE_ENG_START to "1F",
        PIDS_FUEL_TANK_LEVEL to "2F",
        PIDS_DISTANCE_TRAVELED to "31",
        PIDS_CONTROL_MODULE_VOLT to "42",
        PIDS_TRANSMISSION_ACTUAL_GEAR to "A4"
    )

    val valueToKeyPID = mapOf(
        "04" to PIDS_ENGINE_LOAD,
        "05" to PIDS_COOLANT_TEMP,
        "0C" to PIDS_ENGINE_RMP,
        "0D"  to PIDS_VEHICLE_SPEED,
        "1F" to PIDS_TIME_SINCE_ENG_START,
        "2F" to PIDS_FUEL_TANK_LEVEL,
        "31" to PIDS_DISTANCE_TRAVELED,
        "42" to PIDS_CONTROL_MODULE_VOLT,
        "A4" to PIDS_TRANSMISSION_ACTUAL_GEAR
    )
}