package com.example.firstdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firstdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /// Simple enums to handle different examples
    private enum class Step {
        One,
        Two,
        Three
    }

    private enum class IntentStyle {
        Implicit,
        Explicit
    }

    private val step = Step.Three
    private val intent = IntentStyle.Implicit

    ///////////////////////////

    private val TAG = "First Demo"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.receivedMessage.text = "ButterKnife has been replaced!"

        if (step == Step.One) {
            //Step One: Show how to output Log from Logcat;
            Log.d(TAG, "onCreate: Step One: This is the first LOG")
        }

        if (step == Step.Two) {
            //Step Two: Show how to add listener to a button
            val button = findViewById<android.widget.Button>(R.id.button)
            button.setOnClickListener {
                binding.receivedMessage.text = "Button has been clicked!"
                Log.d(TAG, "onCreate: Step Two: Click Button!")
            }
        }

        if (step == Step.Three) {
            //Step Three: Show how to use ViewBinding to add listener to a button
            binding.button.setOnClickListener {
                binding.receivedMessage.text = "Button has been clicked from ViewBinding!"
                Log.d(TAG, "onCreate: Step Three: Click View Binding Button!")
                triggerButtonPressWithIntent()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun triggerButtonPressWithIntent() {
        Log.d(TAG, "outputLog: Click Button Successful!")

        /*
        Explicit intents specify which component of which application
        will satisfy the intent, by specifying a full ComponentName.
         */
        if (intent == IntentStyle.Explicit) {
            //Explicit Intents
            Log.d(TAG, "outputLog: Step one: Explicit Intent")
            val explicit = Intent(this, Main2Activity::class.java)
            explicit.putExtra(MESSAGE, "Hello from the first activity")
            startActivityIntent.launch(explicit)
        }

        /*
        Implicit intents do not name a specific component,
        but instead declare a general action to perform,
        which allows a component from another app to handle it.
         */
        if (intent == IntentStyle.Implicit) {
            //Implicit Intents
            val implicit = Intent()
            implicit.action = "SecondActivity"
            implicit.putExtra(MESSAGE, "Hello from the first activity")
            startActivityIntent.launch(implicit)
        }
    }

    /// Register a callback from the activity result
    private val startActivityIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.receivedMessage.text =
                result.data?.getStringExtra(Main2Activity.RECEIVED_MESSAGE)
        }
    }

    companion object {
        const val MESSAGE = "Message"
        const val MESSAGE_RECEIVED = 1
    }
}
