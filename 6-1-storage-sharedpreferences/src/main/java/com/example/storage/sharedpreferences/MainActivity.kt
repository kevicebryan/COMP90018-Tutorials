package com.example.storage.sharedpreferences

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.storage.sharedpreferences.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val PREFERENCE_NAME = "data"
    private val OBJECT_NAME = "name"
    private val OBJECT_AGE = "age"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveData.setOnClickListener {
            val editor = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit()
            editor.putString(OBJECT_NAME, binding.name.text.toString())
            editor.putInt(OBJECT_AGE, binding.age.text.toString().toInt())
            editor.apply()
        }

        binding.restoreData.setOnClickListener {
            val preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
            binding.name.setText(preferences.getString(OBJECT_NAME, ""))
            binding.age.setText(preferences.getInt(OBJECT_AGE, 0).toString())
        }
    }
}
