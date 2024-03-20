package com.example.motoengine.utils

import android.util.Log
import com.example.motoengine.MainActivity
import com.example.motoengine.model.ECUData

object ObdFactory {

    fun getEcuData(ecuData: ECUData,readMessage: String): ECUData {
        if (readMessage.length < 4) return ecuData
        when (cleanResponse(readMessage).substring(2, 4)) {
            Constant.PIDS_ENGINE_RMP -> {
                ecuData.engineRmp = getEngineRPM(readMessage)
                return ecuData
            }

            Constant.PIDS_COOLANT_TEMP -> {
                ecuData.coolantTemp = getEngineCoolantTemperature(readMessage).toString()
                return ecuData
            }

            Constant.PIDS_ENGINE_LOAD -> {
                ecuData.engineLoad = getEngineLoad(readMessage).toString()
                return ecuData
            }

            Constant.PIDS_CONTROL_MODULE_VOLT -> {
                ecuData.controlModuleVolt = getControlModuleVolt(readMessage).toString()
                return ecuData
            }

            Constant.PIDS_VEHICLE_SPEED -> {
                ecuData.vehicleSpeed = getVehicleSpeed(readMessage).toString()
                return ecuData
            }

            Constant.PIDS_FUEL_TANK_LEVEL -> {
                ecuData.fuelTankLevel = getFuelTankLevel(readMessage).toString()
                return ecuData
            }

            Constant.PIDS_TIME_SINCE_ENG_START -> {
                ecuData.timeSinceEngStart = getTimeSinceEngStart(readMessage).toString()
                return ecuData
            }

            Constant.PIDS_TRANSMISSION_ACTUAL_GEAR -> {
                ecuData.currentGear = getTransmissionActualGear(readMessage).toString()
                return ecuData
            }

            Constant.PIDS_DISTANCE_TRAVELED -> {
                ecuData.distanceTraveled = getDistanceTraveled(readMessage).toString()
                return ecuData
            }

            else -> {
                return ecuData
            }
        }
    }

    private fun getDistanceTraveled(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("4131")) {
            try {
                buf = buf.substring(buf.indexOf("4131"))
                val MSB = buf.substring(4, 6)
                val LSB = buf.substring(6, 8)
                val A = Integer.valueOf(MSB, 16)
                val B = Integer.valueOf(LSB, 16)
                return 256 * A + B
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: java.lang.NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1
    }

    private fun getTransmissionActualGear(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("41A4")) {
            try {
                buf = buf.substring(buf.indexOf("41A4"))
                val MSB = buf.substring(4, 6)
                val LSB = buf.substring(6, 8)
                val A = Integer.valueOf(MSB, 16)
                val B = Integer.valueOf(LSB, 16)
                return (256 * A + B) / 1000
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: java.lang.NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1
    }

    private fun getTimeSinceEngStart(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("411F")) {
            try {
                buf = buf.substring(buf.indexOf("411F"))
                val MSB = buf.substring(4, 6)
                val LSB = buf.substring(6, 8)
                val A = Integer.valueOf(MSB, 16)
                val B = Integer.valueOf(LSB, 16)
                return 256 * A + B
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: java.lang.NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1
    }

    private fun getFuelTankLevel(buffer: String): Float {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("412F")) {
            try {
                buf = buf.substring(buf.indexOf("412F"))
                val MSB = buf.substring(4, 6)
                val A = Integer.valueOf(MSB, 16)
                return (A / 2.55).toFloat()
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: java.lang.NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1f
    }

    private fun getControlModuleVolt(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("4142")) {
            try {
                buf = buf.substring(buf.indexOf("4142"))
                val MSB = buf.substring(4, 6)
                val LSB = buf.substring(6, 8)
                val A = Integer.valueOf(MSB, 16)
                val B = Integer.valueOf(LSB, 16)
                return (256 * A + B) / 1000
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: java.lang.NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1
    }

    private fun getEngineLoad(buffer: String): Float {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("4104")) {
            try {
                buf = buf.substring(buf.indexOf("4104"))
                val MSB = buf.substring(4, 6)
                val A = Integer.valueOf(MSB, 16)
                return (A / 2.55).toFloat()
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: java.lang.NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1f
    }

    private fun getEngineCoolantTemperature(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("4105")) {
            try {
                buf = buf.substring(buf.indexOf("4105"))
                val temp = buf.substring(4, 6)
                var A = Integer.valueOf(temp, 16)
                A -= 40
                return A
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: java.lang.NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1
    }

    fun getEngineRPM(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("410C")) {
            try {
                buf = buf.substring(buf.indexOf("410C"))
                val MSB = buf.substring(4, 6)
                val LSB = buf.substring(6, 8)
                val A = Integer.valueOf(MSB, 16)
                val B = Integer.valueOf(LSB, 16)
                return (A * 256 + B) / 4
            } catch (e: IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1
    }

    private fun getVehicleSpeed(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("410D")) {
            try {
                buf = buf.substring(buf.indexOf("410D"))
                val temp = buf.substring(4, 6)
                return Integer.valueOf(temp, 16)
            } catch (e: java.lang.IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: java.lang.NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1
    }

    private fun showDistanceTraveled(buffer: String): Int {
        var buf = buffer
        buf = cleanResponse(buf)
        if (buf.contains("4131")) {
            try {
                buf = buf.substring(buf.indexOf("4131"))
                val MSB = buf.substring(4, 6)
                val LSB = buf.substring(6, 8)
                val A = Integer.valueOf(MSB, 16)
                val B = Integer.valueOf(LSB, 16)
                return A * 256 + B
            } catch (e: IndexOutOfBoundsException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            } catch (e: NumberFormatException) {
                Log.e(MainActivity.TAG, e.message ?: "")
            }
        }
        return -1
    }

    fun cleanResponse(text: String): String {
        var text = text
        text = text.trim { it <= ' ' }
        text = text.replace("\t", "")
        text = text.replace(" ", "")
        text = text.replace(">", "")
        return text
    }
}