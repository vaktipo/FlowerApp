package com.example.flowerapp

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class LoadingSection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_section)
        supportActionBar?.hide()
        simulateLoading()
    }
    private fun simulateLoading() {
        Handler(mainLooper).postDelayed({
            finish()
        }, 3000) // 3000 мс = 3 секунди
    }

}