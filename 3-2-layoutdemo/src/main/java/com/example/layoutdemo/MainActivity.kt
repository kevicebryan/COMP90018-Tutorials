package com.example.layoutdemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.layoutdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting for Fragments
        val initial = LayoutDemoFragment.newInstance(LayoutDemoFragment.LINEAR_DEMO)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.layout_fragment, initial)
            .addToBackStack(null)
            .commit()

        // Setting for Navigation Bar
        binding.navView.setOnItemSelectedListener { item ->
            val fragmentType = when (item.itemId) {
                // To show Linear layout demonstration
                R.id.navigation_linear -> LayoutDemoFragment.LINEAR_DEMO
                // To show Relative layout demonstration
                R.id.navigation_relative -> LayoutDemoFragment.RELATIVE_DEMO
                // To show List view demonstration
                R.id.navigation_list -> LayoutDemoFragment.LIST_DEMO
                // To show Recycler demonstration
                R.id.navigation_recycler -> LayoutDemoFragment.RECYCLER_DEMO
                else -> return@setOnItemSelectedListener false
            }

            val fragment = LayoutDemoFragment.newInstance(fragmentType)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_fragment, fragment)
                .addToBackStack(null)
                .commit()
            true
        }
    }
}
