package com.example.flowerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class CartItem(val name: String, val price: Int, val imageRes: Int)

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.giftName)
        val priceTextView: TextView = view.findViewById(R.id.giftPrice)
        val imageView: ImageView = view.findViewById(R.id.giftImage)
        val removeButton: ImageView = view.findViewById(R.id.removeItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.priceTextView.text = "${item.price}zł"
        holder.imageView.setImageResource(item.imageRes)
        holder.removeButton.setOnClickListener {
            onRemove(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
