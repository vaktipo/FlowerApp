package com.example.flowerapp

import TransactionAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PaymentFragment : Fragment(R.layout.fragment_payment) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        // Set up RecyclerView for transaction history
        val transactionRecyclerView: RecyclerView = view.findViewById(R.id.transactionRecyclerView)
        transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Sample transaction data
        val transactionList = listOf(
            Transaction("Blush Harmony", "Girlfriend's Birthday", 100),
            Transaction("Sunlit Serenity", "Mother's Bouquet", 150),
            Transaction("Spring Serenade", "John's wedding day", 100)
        )

        // Set adapter for transaction RecyclerView
        val adapter = TransactionAdapter(transactionList)
        transactionRecyclerView.adapter = adapter

        // Fetch card details from Firestore
        loadCardDetails(view)

        return view
    }

    private fun loadCardDetails(view: View) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        // Fetch card details from Firestore
        db.collection("users")
            .document(userId)
            .collection("cards")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "No card details found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val cardNumber = document.getString("CardNumber") ?: "**** **** **** ddd"
                    val cardDate = document.getString("CardDate") ?: "MM/YY"
                    val cvv = document.getString("CVV") ?: "**d"

                    // Format the card details
                    val formattedCardNumber = formatCardNumber(cardNumber)
                    val formattedCvv = formatCvv(cvv)

                    // Update the UI with card details
                    view.findViewById<TextView>(R.id.cardNumber).text = formattedCardNumber
                    view.findViewById<TextView>(R.id.cardDate).text = cardDate
                    view.findViewById<TextView>(R.id.textView8).text = formattedCvv
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load card details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatCardNumber(cardNumber: String): String {
        return if (cardNumber.length == 16) {
            "**** **** **** ${cardNumber.substring(12)}"
        } else {
            cardNumber
        }
    }

    private fun formatCvv(cvv: String): String {
        return if (cvv.length == 3) {
            "**${cvv.last()}"
        } else {
            cvv
        }
    }
}