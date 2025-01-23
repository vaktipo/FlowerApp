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

class EditContactFragment : Fragment(R.layout.fragment_edit_contact) {

    // Declare EditTexts
    private lateinit var nameField: EditText
    private lateinit var birthDateField: EditText
    private lateinit var cityField: EditText
    private lateinit var postField: EditText
    private lateinit var streetField: EditText
    private lateinit var buildField: EditText
    private lateinit var phoneField: EditText
    private lateinit var infoField: EditText
    private lateinit var saveButton: Button

    // Firebase instances
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var contactId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_edit_contact, container, false)

        // Initialize views
        nameField = rootView.findViewById(R.id.name_field)
        birthDateField = rootView.findViewById(R.id.birthDate_field)
        cityField = rootView.findViewById(R.id.city_field)
        postField = rootView.findViewById(R.id.post_field)
        streetField = rootView.findViewById(R.id.street_field)
        buildField = rootView.findViewById(R.id.build_field)
        phoneField = rootView.findViewById(R.id.phone_field)
        infoField = rootView.findViewById(R.id.info_field)
        saveButton = rootView.findViewById(R.id.saveBttn)

        // Retrieve data passed from the previous fragment (via arguments)
        contactId = arguments?.getString("contactId")
        val name = arguments?.getString("Name")
        val birthDate = arguments?.getString("Birthdate")
        val city = arguments?.getString("City")
        val post = arguments?.getString("Postcode")
        val street = arguments?.getString("Street")
        val building = arguments?.getString("Building")
        val phone = arguments?.getString("Phonenum")
        val info = arguments?.getString("Info")

        // Set the current contact data in the EditText fields
        nameField.setText(name)
        birthDateField.setText(birthDate)
        cityField.setText(city)
        postField.setText(post)
        streetField.setText(street)
        buildField.setText(building)
        phoneField.setText(phone)
        infoField.setText(info)

        // Save button click listener
        saveButton.setOnClickListener {
            // Get the updated data from EditTexts
            val updatedName = nameField.text.toString().trim()
            val updatedBirthDate = birthDateField.text.toString().trim()
            val updatedCity = cityField.text.toString().trim()
            val updatedPost = postField.text.toString().trim()
            val updatedStreet = streetField.text.toString().trim()
            val updatedBuilding = buildField.text.toString().trim()
            val updatedPhone = phoneField.text.toString().trim()
            val updatedInfo = infoField.text.toString().trim()

            // Validate the inputs
            if (updatedName.isEmpty() || updatedBirthDate.isEmpty() || updatedCity.isEmpty() || updatedPost.isEmpty() || updatedStreet.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                // Check if the contact ID is available
                if (contactId != null) {
                    // Call the function to update the contact in Firestore
                    updateContact(contactId!!, updatedName, updatedBirthDate, updatedCity, updatedPost, updatedStreet, updatedBuilding, updatedPhone, updatedInfo)
                } else {
                    Toast.makeText(requireContext(), "Contact ID is missing", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
    }

    // Update contact in Firestore
    private fun updateContact(contactId: String, updatedName: String, updatedBirthDate: String, updatedCity: String, updatedPost: String, updatedStreet: String, updatedBuilding: String, updatedPhone: String, updatedInfo: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Reference to the specific contact document
            val contactRef = firestore.collection("users")
                .document(userId) // The user document
                .collection("contacts") // The contacts subcollection
                .document(contactId) // The contact document ID

            // Create a map with updated data
            val updatedContact: MutableMap<String, Any> = hashMapOf(
                "Name" to updatedName,
                "Birthdate" to updatedBirthDate,
                "City" to updatedCity,
                "Postcode" to updatedPost,
                "Street" to updatedStreet,
                "Building" to updatedBuilding,
                "Phonenum" to updatedPhone,
                "Info" to updatedInfo
            )

            // Update the contact document in Firestore
            contactRef.update(updatedContact)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Contact updated successfully", Toast.LENGTH_SHORT).show()

                    // Optionally, navigate back after the update
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error updating contact: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}