package com.example.flowerapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    // Initializing variable for binding, auth, and Google sign-in
    private lateinit var binding: LogInSectionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 123 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting up binding
        binding = LogInSectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your client ID from Firebase Console
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Adding onClickListener to the Login button
        binding.LogIn.setOnClickListener {
            Log.d(TAG, "Login button clicked") // Debug log

            val email = binding.emailField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Sign-in successful for email: $email")
                        val user = auth.currentUser
                        startActivity(Intent(this, HomeSection::class.java))
                        finish()
                    } else {
                        Log.w(TAG, "Sign-in failed for email: $email", task.exception)
                        Toast.makeText(this, "Sign-in failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }

        }

        // Adding onClickListener to the Google Sign-In button
        binding.LogInGoogle.setOnClickListener {
            Log.d(TAG, "Google Sign-In button clicked") // Debug log
            signInWithGoogle()
        }

        // Navigate to RegisterSection when "createAnAcc" text is clicked
        binding.createAnAcc.setOnClickListener {
            Log.d(TAG, "Navigate to RegisterSection") // Debug log
            val intent = Intent(this, RegisterSection::class.java)
            startActivity(intent)
        }
    }

    // Google Sign-In method
    private fun signInWithGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Handle the result of Google Sign-In
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

    // Firebase authentication with Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = FirebaseAuth.getInstance().currentUser
                    // Proceed to next screen
                    val intent = Intent(this, HomeSection::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    // On start of the activity, check if the user is already logged in
    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "User is already logged in, navigating to HomeSection")
            val intent = Intent(this, HomeSection::class.java)
            startActivity(intent)
            finish() // Prevents returning to LogInSection after successful login
        }
    }
}
