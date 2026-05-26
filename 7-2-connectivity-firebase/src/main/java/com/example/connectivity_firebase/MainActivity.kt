package com.example.connectivity_firebase

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.connectivity_firebase.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialFireBase("Test")

        binding.messageButton.setOnClickListener {
            val outputText = binding.messageContent.text.toString()
            databaseReference.setValue(outputText)
        }
    }

    private fun initialFireBase(referenceName: String) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(referenceName)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val inputText = snapshot.getValue(String::class.java)
                // update the field automatically from the firebase database
                binding.messageDisplay.text = inputText
            }

            override fun onCancelled(error: DatabaseError) {
                // handle a cancellation
            }
        })
    }
}
