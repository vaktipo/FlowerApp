import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerapp.CartAdapter
import com.example.flowerapp.CartItem
import com.example.flowerapp.R

class ShoppingCartFragment : Fragment(R.layout.fragment_shopping_cart) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var cartAdapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        totalPriceTextView = view.findViewById(R.id.totalPrice)

        cartAdapter = CartAdapter(cartItems) { item ->
            removeItemFromCart(item)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = cartAdapter

        // Example of adding an item programmatically
        addItemToCart(CartItem("Blush Harmony", 100, R.drawable.bouqet_image))
        addItemToCart(CartItem("Another", 200, R.drawable.bouqet_image))

        updateTotalPrice()
    }

    private fun addItemToCart(item: CartItem) {
        cartItems.add(item)
        cartAdapter.notifyItemInserted(cartItems.size - 1)
        updateTotalPrice()
    }

    private fun removeItemFromCart(item: CartItem) {
        val position = cartItems.indexOf(item)
        if (position != -1) {
            cartItems.removeAt(position)
            cartAdapter.notifyItemRemoved(position)
            updateTotalPrice()
        }
    }

    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.price }
        totalPriceTextView.text = "${total}zł"
    }
}
