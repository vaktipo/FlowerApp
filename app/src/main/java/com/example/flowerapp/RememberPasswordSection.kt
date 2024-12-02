package com.example.flowerapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flowerapp.databinding.LogInSectionBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

class RememberPasswordSection : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.remember_pass)
        supportActionBar?.hide()
        val emailEditText = findViewById<EditText>(R.id.email_field)
        val resetPassword = findViewById<Button>(R.id.resetPassword)
        auth = FirebaseAuth.getInstance()

        resetPassword.setOnClickListener{
            val resetEmail = emailEditText.text.toString()
            if (resetEmail.isNotEmpty() ) {
                auth.sendPasswordResetEmail(resetEmail)
                    .addOnSuccessListener{
                        Toast.makeText(this,"Please check your mail",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                val goToLogInSection = Intent(this@RememberPasswordSection, LogInSection::class.java)
                startActivity(goToLogInSection)
            } else {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            }

        }

    }
}
