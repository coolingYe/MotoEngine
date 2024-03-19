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

    val PIDS = listOf("01", "02", "03", "04", "05", "06", "07", "08",
        "09", "0A", "0B", "0C", "0D", "0E", "0F", "10",
        "11", "12", "13", "14", "15", "16", "17", "18",
        "19", "1A", "1B", "1C", "1D", "1E", "1F", "20")

    const val PIDS_ENGINE_LOAD = "04"
    const val PIDS_COOLANT_TEMP = "05"
    const val PIDS_ENGINE_RMP = "0C"
    const val PIDS_VEHICLE_SPEED = "0D"
    const val PIDS_TIME_SINCE_ENG_START = "1F"
    const val PIDS_FUEL_TANK_LEVEL = "2F"
    const val PIDS_DISTANCE_TRAVELED = "31"
    const val PIDS_CONTROL_MODULE_VOLT = "42"
    const val PIDS_TRANSMISSION_ACTUAL_GEAR = "A4"
}