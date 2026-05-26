package com.example.storage_internalstorage

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.storage_internalstorage.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val FILE_NAME = "myFile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // button events
        binding.saveButton.setOnClickListener { save(binding.editText.text.toString()) }
        binding.loadButton.setOnClickListener { binding.editText.setText(load()) }
        binding.clearButton.setOnClickListener { binding.editText.setText("") }
    }

    private fun save(input: String) {
        try {
            val out = openFileOutput(FILE_NAME, MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(out))
            writer.write(input)
            writer.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun load(): String {
        val myString = StringBuilder()

        try {
            val input = openFileInput(FILE_NAME)
            val reader = BufferedReader(InputStreamReader(input))
            var line = reader.readLine()
            while (line != null) {
                myString.append(line)
                line = reader.readLine()
            }
            reader.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return myString.toString()
    }
}
