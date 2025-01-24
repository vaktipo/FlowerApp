import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerapp.CartAdapter
import com.example.flowerapp.CartItem
import com.example.flowerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ShoppingCartFragment : Fragment(R.layout.fragment_shopping_cart) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var cartAdapter: CartAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        totalPriceTextView = view.findViewById(R.id.totalPrice)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        cartAdapter = CartAdapter(cartItems) { item ->
            // Handle remove item from the UI and Firestore
            removeItemFromCart(item)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = cartAdapter

        // Fetch cart items from Firestore
        fetchCartItemsFromFirestore()
    }

    private fun fetchCartItemsFromFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                        totalPriceTextView.text = "0.00 zł"
                    } else {
                        cartItems.clear()  // Clear any existing items
                        querySnapshot.forEach { document ->
                            val title = document.getString("name") ?: "Unknown Flower"
                            val priceString = document.getString("price") ?: "0 zl"
                            val price = priceString.replace("zl", "").replace("zł", "").trim().toDoubleOrNull() ?: 0.0


                            // Create a CartItem object from Firestore data
                            val cartItem = CartItem(title, price)

                            // Add the CartItem to the list
                            cartItems.add(cartItem)
                        }
                        cartAdapter.notifyDataSetChanged()
                        updateTotalPrice()  // Update the total price after fetching items
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show()
                    android.util.Log.e("FirestoreError", "Error fetching cart items: ", e)
                }
        } else {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeItemFromCart(item: CartItem) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val cartItemRef = db.collection("users")
                .document(userId)
                .collection("cart")
                .whereEqualTo("name", item.name)  // Assuming 'title' is unique for items in the cart.

            cartItemRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val docId = querySnapshot.documents[0].id  // Get the document ID
                        db.collection("users")
                            .document(userId)
                            .collection("cart")
                            .document(docId)
                            .delete()
                            .addOnSuccessListener {
                                // Remove item from the UI
                                cartItems.remove(item)
                                cartAdapter.notifyDataSetChanged()
                                updateTotalPrice()  // Update the total price after removal
                                Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to remove item", Toast.LENGTH_SHORT).show()
                                android.util.Log.e("FirestoreError", "Error removing item: ", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to find item to remove", Toast.LENGTH_SHORT).show()
                    android.util.Log.e("FirestoreError", "Error fetching cart item to remove: ", e)
                }
        }
    }

    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.price }
        totalPriceTextView.text = String.format("%.2f zł", total)
    }
}