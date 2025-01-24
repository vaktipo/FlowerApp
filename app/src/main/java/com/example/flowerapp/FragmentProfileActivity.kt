package com.example.flowerapp


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentProfileActivity : Fragment(R.layout.fragment_profile) {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get views by their IDs
        val FaQ = view.findViewById<TextView>(R.id.FaQ_set_text)
        val paymentFragment = view.findViewById<TextView>(R.id.pay_set_text)
        val contacts = view.findViewById<LinearLayout>(R.id.My_contacts)
        val logout = view.findViewById<TextView>(R.id.logoutButton)

        logout.setOnClickListener {
            auth.signOut() // Log out the user
            val intent = Intent(requireContext(), RegisterSection::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
            startActivity(intent)
        }

        // Set click listener for FaQ
        FaQ.setOnClickListener {
            val faqFragment = FaQFragmentActivity()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, faqFragment)
                .addToBackStack(null)
                .commit()
        }

        // Set click listener for Contacts
        contacts.setOnClickListener {
            val contactsFragment = MyContactsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, contactsFragment)
                .addToBackStack(null)
                .commit()
        }

        // Set click listener for PaymentFragment
        paymentFragment.setOnClickListener {
            checkForCards()
        }
    }

    private fun checkForCards() {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("cards")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // No cards found, open EmptyCardFragment
                        openEmptyCardFragment()
                    } else {
                        // Cards exist, open PaymentFragment
                        openPaymentFragment()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors (optional)
                    exception.printStackTrace()
                }
        } else {
            // Handle case where userId is null (e.g., user not logged in)
        }
    }

    private fun openEmptyCardFragment() {
        val emptyCardFragment = EmptyCardFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wrapper, emptyCardFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openPaymentFragment() {
        val paymentFragment = PaymentFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wrapper, paymentFragment)
            .addToBackStack(null)
            .commit()
    }
}