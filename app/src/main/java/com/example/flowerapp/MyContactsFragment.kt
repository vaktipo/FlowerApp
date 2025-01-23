import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerapp.R

class MyContactsFragment : Fragment(R.layout.fragment_my_contacts) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter

    private val contactList = listOf(
        "Ava Wilson",
        "Daneil Anderson",
        "Henry White",
        "Jane",
        "Mom",
        "Sophia Taylor"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        contactAdapter = ContactAdapter(contactList)
        recyclerView.adapter = contactAdapter
    }
}
