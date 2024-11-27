package com.example.flowerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerapp.databinding.HomeSectionBinding
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeSection : AppCompatActivity() {
    private lateinit var binding: HomeSectionBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeSectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = Firebase.firestore

            // Fetch user profile from Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name") ?: "Your name"
                        binding.name.text = "$userName!"
                    } else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        } else {
            // If no user is logged in, redirect to Login
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LogInSection::class.java)
            startActivity(intent)
            finish()
        }

    }
}