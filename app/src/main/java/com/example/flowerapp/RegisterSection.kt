package com.example.flowerapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterSection : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_section)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val nameEditText = findViewById<EditText>(R.id.name_field)
        val emailEditText = findViewById<EditText>(R.id.email_field)
        val passwordEditText = findViewById<EditText>(R.id.password_field)
        val loginButton = findViewById<TextView>(R.id.create_an_acc)
        val registerButton = findViewById<Button>(R.id.create_an_account)
        val terms = findViewById<CheckBox>(R.id.checkBox)
        val googleSignInButton = findViewById<Button>(R.id.Sign_Up) // Add a button for Google Sign-In

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Use your web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        loginButton.setOnClickListener {
            Log.d("Onboarding", "Register now pressed")
            val goToLogInSection = Intent(this@RegisterSection, LogInSection::class.java)
            startActivity(goToLogInSection)
        }
        val termsAndConditions = findViewById<TextView>(R.id.termsAndCoditions)

        termsAndConditions.setOnClickListener{
            val url = "https://ibb.co/4NvHSkX"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && terms.isChecked) {
                startActivity(Intent(this@RegisterSection, LoadingSection::class.java))
                registerUser(email, password, name)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up Google Sign-In button click listener
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Save user to Firestore
                        saveUserToFirestore(userId, name, email)
                    }
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterSection, LoadingSection::class.java))
                    startActivity(Intent(this, HomeSection::class.java))
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirestore(userId: String, name: String, email: String) {
        val db = Firebase.firestore
        val user = mapOf(
            "name" to name,
            "email" to email
        )

        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User profile saved successfully to Firestore")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save user profile to Firestore", e)
                Toast.makeText(this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHomeSection() {

        val intent = Intent(this, HomeSection::class.java)
        startActivity(intent)
        finish()
    }

    // Sign-In with Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Handle the result of the Google Sign-In intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign-in failed", e)
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Authenticate with Firebase using the Google Account
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToFirestore(userId, account.displayName ?: "User", account.email ?: "")
                    }
                    Toast.makeText(this, "Google sign-in successful", Toast.LENGTH_SHORT).show()

                    navigateToHomeSection()
                } else {
                    Toast.makeText(this, "Google sign-in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    public override fun onStart() {
        super.onStart()
//        auth.signOut()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHomeSection()
        }
    }
}