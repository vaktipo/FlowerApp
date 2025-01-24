package com.example.flowerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BouquetPage : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

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
        val backArrow: ImageView = findViewById(R.id.backArrow)
        val titleTextView: TextView = findViewById(R.id.bouquetName)
        val priceTextView: TextView = findViewById(R.id.price)
        val descriptionTextView: TextView = findViewById(R.id.bouquetDescription)
        val addToCartButton: Button = findViewById(R.id.button)

        // Get the data passed via Intent
        val flowerTitle = intent.getStringExtra("flower_title") ?: "Unknown Bouquet"
        val flowerPrice = intent.getStringExtra("flower_price") ?: "Unknown Price"
        val flowerDescription = intent.getStringExtra("flower_description") ?: ""

        // Set the data to the views
        titleTextView.text = flowerTitle
        priceTextView.text = flowerPrice
        descriptionTextView.text = flowerDescription

        // Handle click on backArrow to navigate to the previous activity
        backArrow.setOnClickListener {
            onBackPressed() // Handles back navigation
        }

        // Handle click on addToCartButton to add item to Firestore
        addToCartButton.setOnClickListener {
            if (userId != null) {
                val cartItem = mapOf(
                    "name" to flowerTitle,
                    "price" to flowerPrice
                )

                // Add the cart item to the Firestore database
                db.collection("users")
                    .document(userId)
                    .collection("cart")
                    .add(cartItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add to cart: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}