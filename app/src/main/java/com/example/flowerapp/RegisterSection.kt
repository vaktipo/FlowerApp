package com.example.flowerapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flowerapp.databinding.RegisterSectionBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterSection : AppCompatActivity() {
    // Variables for binding, auth, and Google sign-in
    private lateinit var binding: RegisterSectionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 123 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterSectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your client ID from Firebase Console
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Email/Password Registration
        binding.createAnAccount.setOnClickListener {
            val email = binding.emailField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val name = binding.nameField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            saveUserToFirestore(userId, name, email) // Save name and email to Firestore
                        }
                        navigateToHomeSection() // Redirect to HomeSection
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Google Sign-In
        binding.SignUp.setOnClickListener {
            signInWithGoogle()
        }

        // Navigate to LogInSection when "createAnAcc" is clicked
        binding.createAnAcc.setOnClickListener {
            val intent = Intent(this, LogInSection::class.java)
            startActivity(intent)
        }
    }
    // Save user data to Firestore
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

    // Navigate to HomeSection
    private fun navigateToHomeSection() {
        val intent = Intent(this, HomeSection::class.java)
        startActivity(intent)
        finish() // Prevent going back to RegisterSection
    }

    // Google Sign-In method
    private fun signInWithGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Handle Google Sign-In result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    // Handle Google Sign-In success or failure
    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account)
            }
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            Toast.makeText(this, "Google sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    // Firebase Authentication with Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val name = account.displayName ?: "User"
                    val email = account.email ?: ""
                    if (userId != null) {
                        saveUserToFirestore(userId, name, email) // Save user data to Firestore
                    }
                    navigateToHomeSection() // Redirect to HomeSection
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // On start of the activity, check if the user is already logged in
    public override fun onStart() {
        super.onStart()
        //auth.signOut()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHomeSection() // If logged in, navigate to HomeSection
        }
    }
}