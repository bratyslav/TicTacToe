package com.example.tictactoe

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class Accelerometer: SensorEventListener {

    private var listener: AccelerometerListener? = null

    fun subscribe(listener: AccelerometerListener) {
        this.listener = listener
    }

    override fun onSensorChanged(event: SensorEvent?) {
        listener?.onAccelerometerEvent(event)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

}

interface AccelerometerListener {

    fun onAccelerometerEvent(event: SensorEvent?)

}