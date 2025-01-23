package com.example.flowerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddContactFragment : Fragment(R.layout.fragment_add_contact) {
    private lateinit var name_field: EditText
    private lateinit var birthDate_field: EditText
    private lateinit var city_field: EditText
    private lateinit var post_field: EditText
    private lateinit var street_field: EditText
    private lateinit var build_field: EditText
    private lateinit var phone_field: EditText
    private lateinit var info_field: EditText
    private lateinit var saveButton: View

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_contact, container, false)

        // Initialize the views
        name_field = rootView.findViewById(R.id.name_field)
        birthDate_field = rootView.findViewById(R.id.birthDate_field)
        city_field = rootView.findViewById(R.id.city_field)
        post_field = rootView.findViewById(R.id.post_field)
        street_field = rootView.findViewById(R.id.street_field)
        build_field = rootView.findViewById(R.id.build_field)
        phone_field = rootView.findViewById(R.id.phone_field)
        info_field = rootView.findViewById(R.id.info_field)
        saveButton = rootView.findViewById(R.id.saveBttn)

        // Set up input validations
        setupPostcodeValidation()
        setupBirthdateValidation()
        setupPhoneNumberValidation()

        // Set up button click listener
        saveButton.setOnClickListener {
            saveContactToFirestore { isSuccess ->
                if (isSuccess) {
                    val contacts = MyContactsFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fl_wrapper, contacts)
                        .addToBackStack(null)
                        .commit()
                    Toast.makeText(requireContext(), "Contact successfully added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to add contact.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
    }

    private fun setupPostcodeValidation() {
        post_field.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                val input = s.toString().replace("-", "")
                if (input.length > 5) {
                    post_field.setText(input.substring(0, 5))
                    post_field.setSelection(5)
                    return
                }

                if (input.length == 5) {
                    val formattedPost = "${input.substring(0, 2)}-${input.substring(2)}"
                    isUpdating = true
                    post_field.setText(formattedPost)
                    post_field.setSelection(formattedPost.length)
                    isUpdating = false
                }
            }
        })
    }

    private fun setupBirthdateValidation() {
        birthDate_field.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                val input = s.toString().replace(".", "")
                if (input.length > 8) {
                    birthDate_field.setText(input.substring(0, 8))
                    birthDate_field.setSelection(8)
                    return
                }

                if (input.length == 8) {
                    val formattedDate =
                        "${input.substring(0, 2)}.${input.substring(2, 4)}.${input.substring(4)}"
                    isUpdating = true
                    birthDate_field.setText(formattedDate)
                    birthDate_field.setSelection(formattedDate.length)
                    isUpdating = false
                }
            }
        })
    }

    private fun setupPhoneNumberValidation() {
        phone_field.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                val input = s.toString().replace(" ", "").replace("+48", "").trim()
                if (input.length > 9) {
                    phone_field.setText("+48 ${input.substring(0, 3)} ${input.substring(3, 6)} ${input.substring(6, 9)}")
                    phone_field.setSelection(phone_field.text.length)
                    return
                }

                if (input.length in 1..9) {
                    val formattedPhone = when (input.length) {
                        in 1..3 -> "+48 $input"
                        in 4..6 -> "+48 ${input.substring(0, 3)} ${input.substring(3)}"
                        else -> "+48 ${input.substring(0, 3)} ${input.substring(3, 6)} ${input.substring(6)}"
                    }
                    isUpdating = true
                    phone_field.setText(formattedPhone)
                    phone_field.setSelection(formattedPhone.length)
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
        val Name = name_field.text.toString().trim()
        val Birthdate = birthDate_field.text.toString().trim()
        val City = city_field.text.toString().trim()
        val Postcode = post_field.text.toString().trim()
        val Street = street_field.text.toString().trim()
        val Building = build_field.text.toString().trim()
        val Phonenum = phone_field.text.toString().trim()
        val Info = info_field.text.toString().trim()

        // Validate inputs
        if (Name.isEmpty() || Birthdate.isEmpty() || City.isEmpty() || Postcode.isEmpty() || Street.isEmpty() || Building.isEmpty() || Phonenum.isEmpty() || Info.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        if (!Postcode.matches(Regex("\\d{2}-\\d{3}"))) {
            Toast.makeText(requireContext(), "Postcode must be in format DD-DDD", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        if (!Birthdate.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}"))) {
            Toast.makeText(requireContext(), "Birthdate must be in format DD.MM.YYYY", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        if (!Phonenum.matches(Regex("\\+48 \\d{3} \\d{3} \\d{3}"))) {
            Toast.makeText(requireContext(), "Phone number must be in format +48 DDD DDD DDD", Toast.LENGTH_SHORT).show()
            onSuccess(false)
            return
        }

        // Prepare data to save
        val eventData = hashMapOf(
            "Name" to Name,
            "Birthdate" to Birthdate,
            "City" to City,
            "Postcode" to Postcode,
            "Street" to Street,
            "Building" to Building,
            "Phonenum" to Phonenum,
            "Info" to Info
        )

        val userId = currentUser.uid

        db.collection("users")
            .document(userId)
            .collection("contacts")
            .add(eventData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Contact saved successfully", Toast.LENGTH_SHORT).show()
                name_field.text.clear()
                birthDate_field.text.clear()
                city_field.text.clear()
                post_field.text.clear()
                street_field.text.clear()
                build_field.text.clear()
                phone_field.text.clear()
                info_field.text.clear()
                onSuccess(true)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save contact: ${e.message}", Toast.LENGTH_SHORT).show()
                onSuccess(false)
            }
    }
}