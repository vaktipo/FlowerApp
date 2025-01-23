import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerapp.R

class ContactAdapter(private val contacts: List<String>, private val onContactClick: (String) -> Unit) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // ViewHolder to hold reference to the contact name TextView
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contactName)
    }

    // Inflate the contact item layout and return a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    // Bind the contact name and set click listener
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contactName = contacts[position]
        holder.contactName.text = contactName

        // Set click listener for the contact item
        holder.itemView.setOnClickListener {
            onContactClick(contactName)  // Trigger the click event when the contact item is clicked
        }
    }

    // Return the size of the contact list
    override fun getItemCount(): Int = contacts.size
}