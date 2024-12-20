package com.example.flowerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EditingEventFragment : Fragment(R.layout.fragment_editing_event) {

    private lateinit var eventNameEditText: EditText
    private lateinit var eventDateEditText: EditText
    private lateinit var beneficiaryEditText: EditText
    private lateinit var recurrenceEditText: EditText
    private lateinit var saveButton: Button

    // Firebase instances
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // To store eventId
    private var eventId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_editing_event, container, false)

        // Initialize views
        eventNameEditText = rootView.findViewById(R.id.name_field)
        eventDateEditText = rootView.findViewById(R.id.date_field)
        beneficiaryEditText = rootView.findViewById(R.id.ben_field)
        recurrenceEditText = rootView.findViewById(R.id.rec_field)
        saveButton = rootView.findViewById(R.id.saveBttn)

        // Retrieve data passed from EventScreenFragment
        eventId = arguments?.getString("eventId")
        val eventName = arguments?.getString("eventName")
        val eventDate = arguments?.getString("eventDate")
        val beneficiary = arguments?.getString("beneficiary")
        val recurrence = arguments?.getString("recurrence")

        // Set the current event data in the edit text fields
        eventNameEditText.setText(eventName)
        eventDateEditText.setText(eventDate)
        beneficiaryEditText.setText(beneficiary)
        recurrenceEditText.setText(recurrence)

        // Save button click listener
        saveButton.setOnClickListener {
            // Get the updated data from EditTexts
            val updatedEventName = eventNameEditText.text.toString().trim()
            val updatedEventDate = eventDateEditText.text.toString().trim()
            val updatedBeneficiary = beneficiaryEditText.text.toString().trim()
            val updatedRecurrence = recurrenceEditText.text.toString().trim()

            // Validate the inputs
            if (updatedEventName.isEmpty() || updatedEventDate.isEmpty() || updatedBeneficiary.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Check if the date format is valid
                if (!isValidDate(updatedEventDate)) {
                    Toast.makeText(requireContext(), "Invalid date format. Use dd.MM.yyyy", Toast.LENGTH_SHORT).show()
                } else {
                    // Proceed to update event if validation passes
                    if (eventId != null) {
                        // Call function to update event in Firestore
                        updateEvent(eventId!!, updatedEventName, updatedEventDate, updatedBeneficiary, updatedRecurrence)
                    } else {
                        Toast.makeText(requireContext(), "Event ID is missing", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return rootView
    }

    // Check if the date is in the correct format
    private fun isValidDate(date: String): Boolean {
        return try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            format.isLenient = false
            format.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Update event in Firestore
    // Update event in Firestore
    private fun updateEvent(eventId: String, updatedEventName: String, updatedEventDate: String, updatedBeneficiary: String, updatedRecurrence:String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Reference to the specific event document
            val eventRef = firestore.collection("users")
                .document(userId) // The user document
                .collection("events") // The events subcollection
                .document(eventId) // The event document ID

            // Create a map with updated data
            val updatedEvent: MutableMap<String, Any> = hashMapOf(
                "eventname" to updatedEventName,
                "eventdate" to updatedEventDate,
                "beneficiary" to updatedBeneficiary,
                "recurrence" to updatedRecurrence
            )

            // Update the event document in Firestore
            eventRef.update(updatedEvent)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Event updated successfully", Toast.LENGTH_SHORT).show()

                    // Optionally, navigate back after the update
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error updating event: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}