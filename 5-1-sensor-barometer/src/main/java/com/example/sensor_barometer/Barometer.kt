package com.example.sensor_barometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.TextView
import org.greenrobot.eventbus.EventBus

class Barometer(private val mContext: Context, sensorLabel: TextView) : SensorEventListener {

    private var mSensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    private val primarySensor = Sensor.TYPE_PRESSURE

    init {
        enableSensor(sensorLabel)
    }

    fun enableSensor(sensorLabel: TextView) {
        Log.v("Sensor...", "Enabling sensor...")
        mSensorManager = mContext.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        Log.v("Sensor...", if (mSensorManager == null) "Sensors not supported" else "Sensors are supported")

        val manager = mSensorManager
        if (manager == null) {
            // Sensors are not available on this device
            sensorLabel.text = mContext.resources.getString(R.string.sensor_invalid)
            return
        }

        val foundSensor = manager.getDefaultSensor(primarySensor)
        sensor = foundSensor
        if (foundSensor == null) {
            // Failure! No pressure sensor is available
            Log.v("Sensor..", "Pressure sensor is not supported")
            sensorLabel.text = mContext.resources.getString(R.string.pressure_sensor_invalid)
        } else {
            // Success! There's a pressure sensor
            Log.v("Sensor..", "${foundSensor.name} is supported")
            manager.registerListener(this, foundSensor, SensorManager.SENSOR_DELAY_NORMAL)
            sensorLabel.text = mContext.resources.getString(R.string.pressure_label)
        }
    }

    fun disableSensor() {
        mSensorManager?.let {
            it.unregisterListener(this)
            mSensorManager = null
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor == null) {
            Log.v("Sensor...", "OnSensorChanged.sensor = null")
            return
        }

        if (sensorEvent.sensor.type == sensor?.type) {
            Log.v("Sensor...", "Posting value")
            EventBus.getDefault().post(BarometerMessage(sensorEvent.values[0]))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
    }
}
