package com.example.motoengine.utils

import android.content.Context
import android.os.Bundle
import android.os.Handler
//import com.fr3ts0n.ecu.prot.obd.ElmProt
import java.util.logging.Level
import java.util.logging.Logger

abstract class CommService(private val mHandler: Handler) {

    enum class STATE {
        NONE,
        LISTEN,
        CONNECTING,
        CONNECTED,
        OFFLINE
    }

    private val TAG = "CommService"

    private lateinit var mContext: Context

    var mState: STATE = STATE.NONE

    val log: Logger = Logger.getLogger(TAG)

    //val elm = ElmProt()

    @Synchronized
    fun setState(state: STATE) {
        log.log(Level.FINE, "setState() $mState-> $state")
        mState = state
        mHandler.obtainMessage(Constant.MESSAGE_STATE_CHANGE, state).sendToTarget()
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a session
     * in listening (server) mode. Called by the Activity onResume()
     */
    protected abstract fun start()

    /**
     * Stop all threads
     */
    abstract fun stop()

    /**
     * Write to the output device in an un-synchronized manner
     *
     * @param out The bytes to write
     */
    abstract fun write(out: ByteArray)

    /**
     * Start connection to specified device
     *
     * @param device The device to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    abstract fun connect(device: Any, secure: Boolean)

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    fun connectionFailed() {
        stop()
        setState(STATE.OFFLINE)
        val msg = mHandler.obtainMessage(Constant.MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(Constant.TOAST, "Unable to connect device")
        msg.data = bundle
        mHandler.sendMessage(msg)
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    fun connectionLost() {
        stop()
        setState(STATE.OFFLINE)
        val msg = mHandler.obtainMessage(Constant.MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(Constant.TOAST, "Device connection was lost")
        msg.data = bundle
        mHandler.sendMessage(msg)
    }

    /**
     * Indicate that the connection was established and notify the UI Activity.
     */
    fun connectionEstablished(deviceName: String) {
        val msg = mHandler.obtainMessage(Constant.MESSAGE_DEVICE_NAME)
        val bundle = Bundle()
        bundle.putString(Constant.DEVICE_NAME, deviceName)
        msg.data = bundle
        mHandler.sendMessage(msg)

        setState(STATE.CONNECTED)
    }
}