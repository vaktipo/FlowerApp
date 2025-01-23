package com.example.flowerapp

import ContactAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyContactsFragment : Fragment(R.layout.fragment_my_contacts) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val contactList = mutableListOf<String>()

    data class Contact(
        val Name: String, val Birthdate: String, val City: String, val contactId: String,
        val Postcode: String, val Street: String, val Building: String, val Phonenum: String, val Info: String
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the "Add New Contact" button by its ID
        val addContact = view.findViewById<Button>(R.id.addNewBttn)

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Pass the click listener to the adapter
        contactAdapter = ContactAdapter(contactList) { contactName ->
            // Handle the click: Navigate to contact details
            showContactDetails(contactName)
        }

        recyclerView.adapter = contactAdapter

        // Load contacts from Firestore
        loadContactsFromFirestore()

        // Navigate to AddContactFragment on button click
        addContact.setOnClickListener {
            val addContactFragment = AddContactFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, addContactFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadContactsFromFirestore() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the user ID
        val userId = currentUser.uid

        // Fetch contacts from Firestore
        db.collection("users")
            .document(userId)
            .collection("contacts")
            .get()
            .addOnSuccessListener { documents ->
                contactList.clear() // Clear the list to avoid duplicates
                for (document in documents) {
                    val name = document.getString("Name") ?: "Unknown"
                    contactList.add(name) // Add the contact name to the list
                }
                contactAdapter.notifyDataSetChanged() // Notify adapter to refresh RecyclerView
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load contacts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showContactDetails(contactName: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Fetch contact details from Firestore based on contact name
            db.collection("users")
                .document(userId)
                .collection("contacts")
                .whereEqualTo("Name", contactName)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.first()
                        val contactId = document.id
                        val contactBirthdate = document.getString("Birthdate") ?: "No Birthdate"
                        val contactCity = document.getString("City") ?: "No City"
                        val contactPostcode = document.getString("Postcode") ?: "No Postcode"
                        val contactStreet = document.getString("Street") ?: "No Street"
                        val contactBuilding = document.getString("Building") ?: "No Building"
                        val contactPhone = document.getString("Phonenum") ?: "No Phone"
                        val contactInfo = document.getString("Info") ?: "No Info"

                        // Create a Contact object
                        val contact = Contact(
                            contactName, contactBirthdate, contactCity, contactId,
                            contactPostcode, contactStreet, contactBuilding, contactPhone, contactInfo
                        )

                        // Pass the contact details to the ContactDetailsFragment
                        val contactDetailsFragment = EditContactFragment()
                        val bundle = Bundle().apply {
                            putString("Name", contact.Name)
                            putString("Birthdate", contact.Birthdate)
                            putString("City", contact.City)
                            putString("contactId", contact.contactId)
                            putString("Postcode", contact.Postcode)
                            putString("Street", contact.Street)
                            putString("Building", contact.Building)
                            putString("Phonenum", contact.Phonenum)
                            putString("Info", contact.Info)
                        }

                        contactDetailsFragment.arguments = bundle

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fl_wrapper, contactDetailsFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load contact details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}