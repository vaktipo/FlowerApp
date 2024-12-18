import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flowerapp.R

data class Flower(val imageRes: Int, val title: String, val price: String, val description: String)

class FlowerAdapter(private val flowerList: List<Flower>) :
    RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder>() {

    inner class FlowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
        val titleView: TextView = itemView.findViewById(R.id.itemTitle)
        val priceView: TextView = itemView.findViewById(R.id.itemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item, parent, false)
        return FlowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
        val flower = flowerList[position]
        holder.titleView.text = flower.title
        holder.priceView.text = flower.price
        Glide.with(holder.imageView.context).load(flower.imageRes).into(holder.imageView)
    }

    override fun getItemCount(): Int = flowerList.size
}
