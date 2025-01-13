import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerapp.R
import com.example.flowerapp.Transaction


class TransactionAdapter(private val transactionList: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.titleTextView.text = transaction.title
        holder.descriptionTextView.text = transaction.description
        holder.amountTextView.text = "-${transaction.amount}zł"
    }

    override fun getItemCount() = transactionList.size

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.transactionTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.transactionDescription)
        val amountTextView: TextView = view.findViewById(R.id.transactionAmount)
    }
}
