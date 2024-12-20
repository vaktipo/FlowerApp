import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.flowerapp.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.example.flowerapp.EditingEventFragment

class EventScreenFragment : Fragment(R.layout.fragment_event_screen) {
    private lateinit var nameField: TextView
    private lateinit var dateField: TextView
    private lateinit var recField: TextView
    private lateinit var benField: TextView
    private lateinit var deleteButton: View
    private lateinit var editButton: Button

    // Firebase instances
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // To store eventId
    private var eventId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_event_screen, container, false)

        // Initialize views
        nameField = rootView.findViewById(R.id.eventName)
        dateField = rootView.findViewById(R.id.dateText)
        recField = rootView.findViewById(R.id.recText)
        benField = rootView.findViewById(R.id.benText)
        deleteButton = rootView.findViewById(R.id.deleteBttn)
        editButton = rootView.findViewById(R.id.editEventBttn)

        // Retrieve data from arguments
        val eventName = arguments?.getString("eventName") ?: "No Name"
        val eventDate = arguments?.getString("eventDate") ?: "No Date"
        val beneficiary = arguments?.getString("beneficiary") ?: "No Beneficiary"
        eventId = arguments?.getString("eventId") // Retrieve the eventId from arguments

        // Set the data in the corresponding fields
        nameField.text = eventName
        dateField.text = eventDate
        benField.text = beneficiary

        // Edit button click listener
        editButton.setOnClickListener {
            if (!eventId.isNullOrEmpty()) {
                val editEventFragment = EditingEventFragment()

                // Pass current event data to the editing fragment
                val bundle = Bundle()
                bundle.putString("eventId", eventId)
                bundle.putString("eventName", eventName)
                bundle.putString("eventDate", eventDate)
                bundle.putString("beneficiary", beneficiary)
                editEventFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_wrapper, editEventFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Event ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete button click listener
        deleteButton.setOnClickListener {
            if (!eventId.isNullOrEmpty()) {
                // Proceed to delete the event
                deleteEvent(eventId!!)
            } else {
                Toast.makeText(requireContext(), "Event ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    // Delete event from Firestore
    private fun deleteEvent(eventId: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val eventRef = firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventId)

            eventRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error deleting event: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}