package com.example.activitylifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * An example dialog activity using the Android Theme.AppCompat.Dialog
 */
class DialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_layout)
    }
}
