package com.example.motoengine

import com.example.motoengine.model.ECUData
import com.example.motoengine.utils.Constant
import com.example.motoengine.utils.ObdFactory
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val INIT_COMMANDS = arrayOf(
        Constant.PIDS_COOLANT_TEMP,
        Constant.PIDS_CONTROL_MODULE_VOLT,
        Constant.PIDS_ENGINE_RMP,
        Constant.PIDS_VEHICLE_SPEED,
        Constant.PIDS_ENGINE_LOAD,
        Constant.PIDS_FUEL_TANK_LEVEL,
        Constant.PIDS_DISTANCE_TRAVELED
    )

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_showEngineRPM() {
        println(ObdFactory.getEngineRPM("010C41 0C 17 06"))
        val buf = ObdFactory.cleanResponse("010C41 0C 17 06")
        println(buf.substring(2,4))
    }

    @Test
    fun test_getEcuData() {

    }
}