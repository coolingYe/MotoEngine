package com.example.motoengine.base

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

}