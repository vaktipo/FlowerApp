package com.example.flowerapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import com.google.firebase.ktx.Firebase

class LogInSection : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001 // Request code for Google Sign-In
    private var isPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_section)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.email_field)
        val passwordEditText = findViewById<EditText>(R.id.password_field)
        val createAnAccount = findViewById<TextView>(R.id.create_an_acc)
        val loginButton = findViewById<Button>(R.id.LogIn)
        val googleSignInButton = findViewById<Button>(R.id.Log_in_google)
        val forgot = findViewById<TextView>(R.id.iForgotMy)
        val togglePasswordVisibility = findViewById<ImageView>(R.id.eye)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Use your web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        createAnAccount.setOnClickListener {
            Log.d("Onboarding", "create now pressed")
            val goToRegisterSection = Intent(this@LogInSection, RegisterSection::class.java)
            startActivity(goToRegisterSection)
        }

        forgot.setOnClickListener{
            Log.d("Onboarding", "forgot pressed")
            val goToRememberPasswordSection = Intent(this@LogInSection, RememberPasswordSection::class.java)
            startActivity(goToRememberPasswordSection)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                startActivity(Intent(this@LogInSection, LoadingSection::class.java))
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.visibility_on)
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.visibility_off)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }


        // Set up Google Sign-In button click listener
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }


    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this@LogInSection, HomeSectionActivity::class.java))
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

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

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LogInSection, LoadingSection::class.java))
                    startActivity(Intent(this@LogInSection, HomeSectionActivity::class.java))
                } else {
                    Toast.makeText(this, "Google login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "User is already logged in, navigating to HomeSection")
            val intent = Intent(this, HomeSectionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}