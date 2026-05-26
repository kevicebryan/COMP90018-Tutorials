package com.example.firstdemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.firstdemo.databinding.ActivityMain2Binding

class Main2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousMessage.text = intent.getStringExtra(MainActivity.MESSAGE)

        // Handle the back press in API > Tiramisu
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val returnIntent = Intent()
                returnIntent.putExtra(RECEIVED_MESSAGE, "Hello from the second activity.")
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {
        const val RECEIVED_MESSAGE = "Received message"
    }
}
