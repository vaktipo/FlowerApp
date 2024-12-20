package com.example.flowerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AddEventFragment : Fragment(R.layout.fragment_add_event) {

    private lateinit var nameField: EditText
    private lateinit var dateField: EditText
    private lateinit var recField: EditText
    private lateinit var benField: EditText
    private lateinit var saveButton: View
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_event, container, false)

        // Initialize the views
        nameField = rootView.findViewById(R.id.name_field)
        dateField = rootView.findViewById(R.id.date_field)
        recField = rootView.findViewById(R.id.rec_field)
        benField = rootView.findViewById(R.id.ben_field)
        saveButton = rootView.findViewById(R.id.saveBttn)

        // Set up button click listener
        saveButton.setOnClickListener {
            saveEventToFirestore { isSuccess ->
                if (isSuccess) {
                    val calendar = FragmentCalendarActivity()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fl_wrapper, calendar)
                        .addToBackStack(null)
                        .commit()
                    Toast.makeText(requireContext(), "Event successfully added!", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle failure
                    Toast.makeText(requireContext(), "Failed to add event.", Toast.LENGTH_SHORT).show()
                }
            }

            }


        return rootView
    }

    private fun saveEventToFirestore(onSuccess: (Boolean) -> Unit) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        // Get values from EditTexts
        val eventName = nameField.text.toString().trim()
        val eventDate = dateField.text.toString().trim()
        val eventRecurrence = recField.text.toString().trim()
        val eventBeneficiary = benField.text.toString().trim()

        // Validate inputs
        if (eventName.isEmpty() || eventDate.isEmpty() || eventBeneficiary.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        // Prepare data to save
        val eventData = hashMapOf(
            "eventname" to eventName,
            "eventdate" to eventDate,
            "recurrence" to eventRecurrence,
            "beneficiary" to eventBeneficiary
        )

        // Get the user ID
        val userId = currentUser.uid

        // Save to Firestore under `users/{userId}/events/{autoGeneratedId}`
        db.collection("users")
            .document(userId)
            .collection("events")
            .add(eventData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Event saved successfully", Toast.LENGTH_SHORT).show()

                // Optionally, navigate back or reset fields
                nameField.text.clear()
                dateField.text.clear()
                recField.text.clear()
                benField.text.clear()

                // Notify success
                onSuccess(true)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save event: ${e.message}", Toast.LENGTH_SHORT).show()
                onSuccess(false)
            }
    }
}