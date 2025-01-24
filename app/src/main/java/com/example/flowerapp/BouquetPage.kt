package com.example.flowerapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BouquetPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Set the content view for the activity
        setContentView(R.layout.bouquet_page)

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Apply window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find views
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val titleTextView: TextView = findViewById(R.id.bouquetName)
        val priceTextView: TextView = findViewById(R.id.price)
        val descriptionTextView: TextView = findViewById(R.id.bouquetDescription)

        // Get the data passed via Intent
        val flowerTitle = intent.getStringExtra("flower_title")
        val flowerPrice = intent.getStringExtra("flower_price")
        val flowerDescription = intent.getStringExtra("flower_description")

        // Set the data to the views
        titleTextView.text = flowerTitle
        priceTextView.text = flowerPrice
        descriptionTextView.text = flowerDescription

        // Handle click on backArrow to navigate back to the previous activity
        backArrow.setOnClickListener {
            onBackPressed() // Handles back navigation
        }
    }
}
