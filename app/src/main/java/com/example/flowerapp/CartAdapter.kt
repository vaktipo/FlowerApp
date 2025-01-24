package com.example.flowerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Updated CartItem class to hold price as Double
data class CartItem(val name: String, val price: Double)

class CartAdapter(
    private val items: MutableList<CartItem>,  // List to hold the cart items
    private val onRemove: (CartItem) -> Unit    // Callback for removing an item
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Find views
        val nameTextView: TextView = view.findViewById(R.id.giftName)
        val priceTextView: TextView = view.findViewById(R.id.giftPrice)
        val imageView: ImageView = view.findViewById(R.id.giftImage)
        val removeButton: ImageView = view.findViewById(R.id.removeItem)  // Assuming an ImageView for remove button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        // Inflate layout for each item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        // Bind data to the views
        holder.nameTextView.text = item.name
        holder.priceTextView.text = String.format("%.2f zł", item.price)  // Format price as double with 2 decimal places


        // Set up the remove button click listener
        holder.removeButton.setOnClickListener {
            onRemove(item)  // Call the removal function passed as a callback
        }
    }

    override fun getItemCount(): Int = items.size  // Return the size of the items list
}