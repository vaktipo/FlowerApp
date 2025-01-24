package com.example.flowerapp

import android.content.Intent
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

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Set the content view for the activity
        setContentView(R.layout.bouquet_page)

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
        //val flowerImageRes = intent.getIntExtra("flower_imageRes", R.drawable.bouquet)

        // Find views to display the data
        val titleTextView: TextView = findViewById(R.id.bouquetName)
        val priceTextView: TextView = findViewById(R.id.price)
        val descriptionTextView: TextView = findViewById(R.id.bouquetDescription)
        //val imageView: ImageView = findViewById(R.id.bouquetImage)

        // Set the data to the views
        titleTextView.text = flowerTitle
        priceTextView.text = flowerPrice
        descriptionTextView.text = flowerDescription
        //imageView.setImageResource(flowerImageRes)

        // Handle click on backArrow to navigate to HomeSectionActivity
        val backArrow = findViewById<ImageView>(R.id.heart)
        backArrow.setOnClickListener {
            val intent = Intent(this, HomeSectionActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close current activity
        }
    }
}