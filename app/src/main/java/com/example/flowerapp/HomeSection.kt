package com.example.flowerapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flowerapp.databinding.HomeSectionBinding

class HomeSection : AppCompatActivity() {
    private lateinit var binding: HomeSectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeSectionBinding.inflate(layoutInflater)
        setContentView(binding.root) // Ensure this file exists
    }
}

