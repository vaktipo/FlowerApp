package com.example.flowerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentProfileActivity : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the TextView by its ID (replace with your actual ID)
        val textView = view.findViewById<TextView>(R.id.FaQ_set_text) // Replace 'textViewId' with your actual TextView ID

        // Set click listener on the TextView
        textView.setOnClickListener {
            // Create a new instance of FaQFragmentActivity
            val faqFragment = FaQFragmentActivity()

            // Begin the FragmentTransaction and replace the current fragment with FaQFragmentActivity
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, faqFragment) // Replace 'fragment_container' with the ID of your container
                .addToBackStack(null) // Add to back stack to allow back navigation
                .commit()
        }
    }
}