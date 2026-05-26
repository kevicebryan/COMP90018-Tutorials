package com.example.sensor_barometer

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.sensor_barometer.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sensorLabel: TextView
    private lateinit var pressure: TextView
    private lateinit var barometer: Barometer
    private lateinit var switchLayout: Button

    private var output = OutputVisibility.Text

    private val ROT_ZERO = -125f
    private val ROT_VALUE = 0.893f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorLabel = binding.sensorLabel
        pressure = binding.barometerValue
        switchLayout = binding.button

        // the image is scaled such as:
        // 0 = -125 rotations
        // 280 = +125 rotation
        // 1 value = 0.893f
        binding.point.rotation = ROT_ZERO

        switchLayout.setOnClickListener {
            output = OutputVisibility.values()[(output.ordinal + 1) % 3]

            when (output) {
                OutputVisibility.Image -> {
                    binding.panel.visibility = View.VISIBLE
                    binding.point.visibility = View.VISIBLE
                    binding.sensorLabel.visibility = View.INVISIBLE
                    binding.barometerValue.visibility = View.INVISIBLE
                }
                OutputVisibility.Text -> {
                    binding.panel.visibility = View.INVISIBLE
                    binding.point.visibility = View.INVISIBLE
                    binding.sensorLabel.visibility = View.VISIBLE
                    binding.barometerValue.visibility = View.VISIBLE
                }
                OutputVisibility.Both -> {
                    binding.panel.visibility = View.VISIBLE
                    binding.point.visibility = View.VISIBLE
                    binding.sensorLabel.visibility = View.VISIBLE
                    binding.barometerValue.visibility = View.VISIBLE
                }
            }
        }

        barometer = Barometer(this, sensorLabel)
        EventBus.getDefault().register(this)
    }

    fun setSensorLabel(s: String) {
        sensorLabel.text = s
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        barometer.disableSensor()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event: BarometerMessage) {
        pressure.text = event.pressure.toString()
        binding.point.rotation = ROT_ZERO + (event.pressure * ROT_VALUE)
    }

    private enum class OutputVisibility {
        Text,
        Image,
        Both
    }
}
