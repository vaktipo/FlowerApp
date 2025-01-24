package com.example.flowerapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text
import android.view.View


class BouquetPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Set the content view for the activity first
        setContentView(R.layout.bouquet_page)

        // Now you can find views
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        // Bring it to the front
        backArrow.bringToFront()

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Apply window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the data passed via Intent
        val flowerTitle = intent.getStringExtra("flower_title")
        val flowerPrice = intent.getStringExtra("flower_price")
        val flowerDescription = intent.getStringExtra("flower_description")

        // Find views to display the data
        val titleTextView: TextView = findViewById(R.id.bouquetName)
        val priceTextView: TextView = findViewById(R.id.price)
        val descriptionTextView: TextView = findViewById(R.id.bouquetDescription)

        // Set the data to the views
        titleTextView.text = flowerTitle
        priceTextView.text = flowerPrice
        descriptionTextView.text = flowerDescription
    }
}
