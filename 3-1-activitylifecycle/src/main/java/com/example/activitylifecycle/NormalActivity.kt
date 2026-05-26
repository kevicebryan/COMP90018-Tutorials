package com.example.activitylifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * An example activity using a standard design
 */
class NormalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.normal_layout)
    }
}
