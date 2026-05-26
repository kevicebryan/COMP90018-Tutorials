package com.example.storage_contentprovider

import android.annotation.SuppressLint
import android.content.ContentProviderClient
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.storage_contentprovider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var newId: String? = null
    private var provider: ContentProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addData.setOnClickListener {
            // Insert Data
            try {
                val uri = Uri.parse("content://com.example.storage_database.provider/book")
                val client = contentResolver.acquireContentProviderClient(uri)
                provider = client
                val values = ContentValues()
                values.put("name", "A Clash of Kings")
                values.put("author", "George Martin")
                values.put("pages", 1040)
                values.put("price", 55.55)
                val newUri: Uri? = client?.insert(uri, values)
                newId = newUri?.pathSegments?.get(1)

                client?.close()
            } catch (e: RemoteException) {
                Log.e("Content Provider Insert Data", "URI is not reachable")
            }
        }

        binding.queryData.setOnClickListener {
            // Query Data
            try {
                val uri = Uri.parse("content://com.example.storage_database.provider/book")
                val client = contentResolver.acquireContentProviderClient(uri)
                provider = client
                val cursor = client?.query(uri, null, null, null, null)

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range") val name = cursor.getString(cursor.getColumnIndex("name"))
                        @SuppressLint("Range") val author = cursor.getString(cursor.getColumnIndex("author"))
                        @SuppressLint("Range") val pages = cursor.getInt(cursor.getColumnIndex("pages"))
                        @SuppressLint("Range") val price = cursor.getDouble(cursor.getColumnIndex("price"))
                        Log.d("MainActivity", "book name is $name")
                        Log.d("MainActivity", "book author is $author")
                        Log.d("MainActivity", "book pages is $pages")
                        Log.d("MainActivity", "book price is $price")
                    }
                    cursor.close()
                }

                client?.close()
            } catch (e: Exception) {
                Log.e("Content Provider Query", "URI is not reachable")
            }
        }

        binding.updateData.setOnClickListener {
            // Update Data
            val uri = Uri.parse("content://com.example.storage_database.provider/book/$newId")
            val values = ContentValues()
            values.put("name", "A Storm of Swords")
            values.put("pages", 1216)
            values.put("price", 24.05)
            contentResolver.update(uri, values, null, null)
        }

        binding.deleteData.setOnClickListener {
            // Delete Data
            val uri = Uri.parse("content://com.example.storage_database.provider/book/$newId")
            contentResolver.delete(uri, null, null)
        }
    }
}
