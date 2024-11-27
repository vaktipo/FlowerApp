package com.example.flowerapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeSection : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var nameOfUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_section)
        auth = FirebaseAuth.getInstance()
        nameOfUser = findViewById(R.id.name)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val db = Firebase.firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name") ?: "User"
                        if (userName.length > 10){
                            nameOfUser.text = "$userName!"
                            nameOfUser.textSize = 25f
                        }
                        else {
                            nameOfUser.textSize = 30f
                        }

                    }
                    else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LogInSection::class.java)
            startActivity(intent)
            finish()
        }
    }
}