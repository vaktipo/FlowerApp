package com.example.flowerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddCardFragment : Fragment() {

    private lateinit var card_number: EditText
    private lateinit var card_date: EditText
    private lateinit var cvv: EditText
    private lateinit var saveButton: View

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_card, container, false)

        // Initialize the views
        card_number = rootView.findViewById(R.id.cardNumber)
        card_date = rootView.findViewById(R.id.cardDate)
        cvv = rootView.findViewById(R.id.cardCVV)
        saveButton = rootView.findViewById(R.id.saveBttn)

        // Set up input validations
        setupCardNumberValidation()
        setupCVVValidation()
        setupDateValidation()

        // Set up button click listener
        saveButton.setOnClickListener {
            saveContactToFirestore { isSuccess ->
                if (isSuccess) {
                    val payment = PaymentFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fl_wrapper, payment)
                        .addToBackStack(null)
                        .commit()
                    Toast.makeText(requireContext(), "Card successfully added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to add card.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
    }

    private fun setupCardNumberValidation() {
        card_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.length > 16) {
                    card_number.setText(input.substring(0, 16))
                    card_number.setSelection(16) // Move cursor to the end
                }
            }
        })
    }

    private fun setupCVVValidation() {
        cvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.length > 3) {
                    cvv.setText(input.substring(0, 3))
                    cvv.setSelection(3) // Move cursor to the end
                }
            }
        })
    }

    private fun setupDateValidation() {
        card_date.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                val input = s.toString().replace("/", "")
                if (input.length > 4) {
                    card_date.setText(input.substring(0, 4))
                    card_date.setSelection(4)
                    return
                }

                if (input.length == 4) {
                    val formattedDate = "${input.substring(0, 2)}/${input.substring(2)}"
                    isUpdating = true
                    card_date.setText(formattedDate)
                    card_date.setSelection(formattedDate.length)
                    isUpdating = false
                }
            }
        })
    }

    private fun saveContactToFirestore(onSuccess: (Boolean) -> Unit) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        // Get values from EditTexts
        val cardNumber = card_number.text.toString().trim()
        val cardDate = card_date.text.toString().trim()
        val cardCVV = cvv.text.toString().trim()

        // Validate inputs
        if (cardNumber.isEmpty() || cardDate.isEmpty() || cardCVV.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        if (cardNumber.length != 16) {
            Toast.makeText(requireContext(), "Card number must be 16 digits", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        if (cardCVV.length != 3) {
            Toast.makeText(requireContext(), "CVV must be 3 digits", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        if (!cardDate.matches(Regex("\\d{2}/\\d{2}"))) {
            Toast.makeText(requireContext(), "Date must be in MM/YY format", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        // Prepare data to save
        val cardData = hashMapOf(
            "CardNumber" to cardNumber,
            "CardDate" to cardDate,
            "CVV" to cardCVV
        )

        // Save to Firestore
        db.collection("users")
            .document(currentUser.uid)
            .collection("cards")
            .add(cardData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Card saved successfully", Toast.LENGTH_SHORT).show()
                card_number.text.clear()
                card_date.text.clear()
                cvv.text.clear()
                onSuccess(true)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save card: ${e.message}", Toast.LENGTH_SHORT).show()
                onSuccess(false)
            }
    }
}