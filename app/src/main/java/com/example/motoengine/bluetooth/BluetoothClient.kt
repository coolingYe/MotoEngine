package com.example.motoengine.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.motoengine.utils.CommService
//import com.fr3ts0n.prot.StreamHandler
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.logging.Level


class BluetoothClient(mContext: Context, mHandler: Handler) : CommService(mHandler) {
    //private val ser = StreamHandler()
    private var mBtConnectThread: BtConnectThread? = null
    private var mBtWorkerThread: BtWorkerThread? = null

    init {
        val mBluetoothManager = mContext.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
        mBluetoothManager.adapter.cancelDiscovery()
        //elm.addTelegramWriter(ser)
        //ser.messageHandler = elm
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a session
     * in listening (server) mode. Called by the Activity onResume()
     */
    override fun start() {
        log.log(Level.FINE, "start")

        // Cancel any thread attempting to make a connection

        // Cancel any thread attempting to make a connection
        if (mBtConnectThread != null) {
            mBtConnectThread!!.cancel()
            mBtConnectThread = null
        }

        // Cancel any thread currently running a connection

        // Cancel any thread currently running a connection
        if (mBtWorkerThread != null) {
            mBtWorkerThread!!.cancel()
            mBtWorkerThread = null
        }

        setState(STATE.LISTEN)
    }

    /**
     * Stop all threads
     */
    override fun stop() {
        log.log(Level.FINE, "stop")
        //elm.removeTelegramWriter(ser)

        if (mBtConnectThread != null) {
            mBtConnectThread!!.cancel()
            mBtConnectThread = null
        }

        if (mBtWorkerThread != null) {
            mBtWorkerThread!!.cancel()
            mBtWorkerThread = null
        }

        setState(STATE.OFFLINE)
    }

    /**
     * Write to the BtWorkerThread in an un-synchronized manner
     *
     * @param out The bytes to write
     * @see BtWorkerThread#write(byte[])
     */
    override fun write(out: ByteArray) {

        // Perform the write un-synchronized
        mBtWorkerThread!!.write(out)
    }

    /**
     * start connection to specified device
     *
     * @param device The device to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    override fun connect(device: Any, secure: Boolean) {
        log.log(Level.FINE, "connect to: $device")

        // Cancel any thread attempting to make a connection

        // Cancel any thread attempting to make a connection
        if (mState === STATE.CONNECTING) {
            if (mBtConnectThread != null) {
                mBtConnectThread!!.cancel()
                mBtConnectThread = null
            }
        }

        // Cancel any thread currently running a connection

        // Cancel any thread currently running a connection
        if (mBtWorkerThread != null) {
            mBtWorkerThread!!.cancel()
            mBtWorkerThread = null
        }

        setState(STATE.CONNECTING)

        // Start the thread to connect with the given device

        // Start the thread to connect with the given device
        mBtConnectThread = BtConnectThread((device as BluetoothDevice), secure)
        mBtConnectThread!!.start()
    }

    /**
     * Start the BtWorkerThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    @Synchronized
    private fun connected(socket: BluetoothSocket, device: BluetoothDevice, socketType: String) {
        log.log(Level.FINE, "connected, Socket Type:$socketType")

        // Cancel the thread that completed the connection
        if (mBtConnectThread != null) {
            mBtConnectThread!!.cancel()
            mBtConnectThread = null
        }

        // Cancel any thread currently running a connection
        if (mBtWorkerThread != null) {
            mBtWorkerThread!!.cancel()
            mBtWorkerThread = null
        }

        // Delay connection for 500ms (Fix issue AndrOBD/#233)
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Start the thread to manage the connection and perform transmissions
        mBtWorkerThread = BtWorkerThread(socket, socketType)
        mBtWorkerThread!!.start()

        // we are connected -> signal connection established
        connectionEstablished(device.name)
    }

    inner class BtConnectThread(private val device: BluetoothDevice, secure: Boolean) : Thread() {
        private lateinit var mSocket: BluetoothSocket
        private var mSocketType: String? = null
        init {
            mSocketType = if (secure) "Secure" else "Insecure"
            var tmp: BluetoothSocket? = null
            val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(SPP_UUID)
                } else tmp = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
            } catch (e: IOException) {
                log.log(Level.SEVERE, "Socket Type: " + mSocketType + "create() failed", e)
            }
            if (tmp != null) {
                mSocket = tmp
            }

            logSocketUuids(mSocket, "BT socket")
        }

        private fun logSocketUuids(socket: BluetoothSocket, msg: String) {
            if (log.isLoggable(Level.INFO)) {
                val message = StringBuilder(msg)
                // dump supported UUID's
                message.append(" - UUIDs:")
                val uuids = socket.remoteDevice.uuids
                if (uuids != null) {
                    for (uuid in uuids) {
                        message.append(uuid.uuid.toString()).append(",")
                    }
                } else {
                    message.append("NONE (Invalid BT implementation)")
                }
                log.log(Level.INFO, message.toString())
            }
        }

        override fun run() {
            log.log(Level.INFO, "BEGIN mBtConnectThread SocketType:$mSocketType")

            // Make a connection to the BluetoothSocket
            try {
                log.log(Level.FINE, "Connect BT socket")

                // This is a blocking call and will only return on a
                // successful connection or an exception
                mSocket.connect()
            } catch (e: IOException) {
                log.log(Level.FINE, e.message)
                cancel()
                log.log(Level.INFO, "Fallback attempt to create RfComm socket")
                val sockFallback: BluetoothSocket
                val clazz: Class<*> = mSocket.getRemoteDevice().javaClass
                val paramTypes = arrayOf<Class<*>>(Integer.TYPE)
                try {
                    val m = clazz.getMethod("createRfcommSocket", *paramTypes)
                    val params = arrayOf<Any>(1)
                    sockFallback = m.invoke(mSocket.getRemoteDevice(), *params) as BluetoothSocket
                    mSocket = sockFallback
                    logSocketUuids(mSocket, "Fallback socket")

                    // connect fallback socket
                    mSocket.connect()
                } catch (e2: Exception) {
                    log.log(Level.SEVERE, e2.message)
                    connectionFailed()
                    return
                }
            }

            // Reset the BtConnectThread because we're done
            synchronized(this@BluetoothClient) { mBtConnectThread = null }

            // Start the connected thread
            connected(mSocket, device, mSocketType!!)
        }

        @Synchronized
        fun cancel() {
            try {
                log.log(Level.INFO, "Closing BT socket")
                mSocket.close()
            } catch (e: IOException) {
                log.log(Level.SEVERE, e.message)
            }
        }
    }

    inner class BtWorkerThread(socket: BluetoothSocket, socketType: String) : Thread() {
        private var mmSocket: BluetoothSocket? = null
        private var mmInStream: InputStream? = null
        private var mmOutStream: OutputStream? = null

        init {
            log.log(Level.FINE, "create BtWorkerThread: $socketType")
            mmSocket = socket
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the BluetoothSocket input and output streams

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
                log.log(Level.SEVERE, "temp sockets not created", e)
            }

            mmInStream = tmpIn
            mmOutStream = tmpOut
            // set streams
            //ser.setStreams(mmInStream, mmOutStream)
        }

        @Synchronized
        fun write(buffer: ByteArray?) {
            //ser.writeTelegram(String(buffer!!).toCharArray())
        }

        override fun run() {
            log.log(Level.INFO, "BEGIN mBtWorkerThread")
            try {
                // run the communication thread
                //ser.run()
            } catch (ex: java.lang.Exception) {
                // Intentionally ignore
                log.log(Level.SEVERE, "Comm thread aborted", ex)
            }
            connectionLost()
        }

        @Synchronized
        fun cancel() {
            try {
                log.log(Level.INFO, "Closing BT socket")
                mmSocket!!.close()
            } catch (e: IOException) {
                log.log(Level.SEVERE, e.message)
            }
        }
    }

}