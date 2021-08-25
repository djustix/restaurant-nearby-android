package com.djustix.nearbites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.djustix.nearbites.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}