package com.example.watch

import android.app.Activity
import android.os.Bundle
import com.example.watch.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.statusLabel.setText(R.string.question_label)

        binding.button1.setOnClickListener {
            binding.statusLabel.setText(R.string.agree_label)
        }

        binding.button2.setOnClickListener {
            binding.statusLabel.setText(R.string.disagree_label)
        }
    }
}
