package com.example.storage_database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.storage_database.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var dbHelper: MyDatabaseHelper

    private var deleteLessThan = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MyDatabaseHelper(this, "BookStore.db", null, 2)

        binding.modifyDeleteData.text = "<"

        binding.createDatabase.setOnClickListener {
            dbHelper.writableDatabase
        }

        binding.addData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values = ContentValues()

            // Compose the first record of database
            values.put("name", "The Da Vinci Code")
            values.put("author", "Dan Brown")
            values.put("pages", 454)
            values.put("price", 16.96)
            db.insert("Book", null, values)
            // Insert the first row of database

            values.clear()

            // Compose the second record of database
            values.put("name", "The Lost Symbol")
            values.put("author", "Dan Brown")
            values.put("pages", 510)
            values.put("price", 19.95)
            db.insert("Book", null, values)
            // Insert the second of database

            updateQueryCountLabel(queryAllDataCount().toString())
        }

        binding.updateData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values = ContentValues()
            values.put("price", 10.99)
            db.update("Book", values, "name = ?", arrayOf("The Da Vinci Code"))

            updateQueryCountLabel(queryAllDataCount().toString())
        }

        binding.deleteData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.delete("Book", "pages ${binding.modifyDeleteData.text}?", arrayOf("500"))

            updateQueryCountLabel(queryAllDataCount().toString())
        }

        binding.modifyDeleteData.setOnClickListener {
            deleteLessThan = !deleteLessThan
            binding.modifyDeleteData.text = if (deleteLessThan) "<" else ">"
        }

        binding.queryData.setOnClickListener {
            val db = dbHelper.writableDatabase

            // Query all data from "Book"
            val cursor = db.query("Book", null, null, null, null, null, null)

            updateQueryCountLabel(cursor.count.toString())

            if (cursor.moveToFirst()) {
                do {
                    // Get all the record from cursor
                    @SuppressLint("Range") val name = cursor.getString(cursor.getColumnIndex("name"))
                    @SuppressLint("Range") val author = cursor.getString(cursor.getColumnIndex("author"))
                    @SuppressLint("Range") val pages = cursor.getInt(cursor.getColumnIndex("pages"))
                    @SuppressLint("Range") val price = cursor.getDouble(cursor.getColumnIndex("price"))
                    Log.d("MainActivity", "------------------------")
                    Log.d("MainActivity", "book name is $name")
                    Log.d("MainActivity", "book author is $author")
                    Log.d("MainActivity", "book pages is $pages")
                    Log.d("MainActivity", "book price is $price")
                    Log.d("MainActivity", "------------------------")
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    private fun queryAllDataCount(): Int {
        val cursor = dbHelper.writableDatabase.query("Book", null, null, null, null, null, null)
        return cursor.count
    }

    private fun updateQueryCountLabel(value: String) {
        binding.queryCount.text = "N = $value"
    }
}
