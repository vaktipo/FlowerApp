package com.example.flowerapp

import ShoppingCartFragment
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.flowerapp.adapters.ImageAdapter
import com.example.flowerapp.models.ImageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID


// Updated Flower data class
data class Flower(val imageRes: Int, val title: String, val price: String,val description: String)

// FlowerAdapter remains unchanged
class FlowerAdapter(
    private val flowerList: List<Flower>,
    private val onItemClick: (Flower) -> Unit // Click listener passed from Fragment
) : RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder>() {

    inner class FlowerViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
        val titleView: TextView = itemView.findViewById(R.id.itemTitle)
        val priceView: TextView = itemView.findViewById(R.id.itemPrice)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): FlowerViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_item, parent, false)
        return FlowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
        val flower = flowerList[position]
        holder.titleView.text = flower.title
        holder.priceView.text = flower.price
        holder.imageView.setImageResource(flower.imageRes)

        // Set the click listener for the item
        holder.itemView.setOnClickListener {
            onItemClick(flower)  // Trigger the click action
        }
    }

    override fun getItemCount(): Int = flowerList.size
}
// FragmentHomeActivity
class FragmentHomeActivity : Fragment(R.layout.fragment_home) {
    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
    private lateinit var viewpager2: ViewPager2
    private lateinit var auth: FirebaseAuth
    private lateinit var nameOfUser: TextView
    private lateinit var upcomingEvent: TextView
    private lateinit var shoppingCart: ImageView
    private lateinit var cartSum: TextView

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nonActiveDotSizeDp = 5
        val activeDotWidthDp = 12
        val dotHeightDp = 5
        val density = resources.displayMetrics.density
        val nonActiveDotSizePx = (nonActiveDotSizeDp * density).toInt()
        val activeDotWidthPx = (activeDotWidthDp * density).toInt()
        val dotHeightPx = (dotHeightDp * density).toInt()
        val db = Firebase.firestore

        val params = LinearLayout.LayoutParams(
            nonActiveDotSizePx,
            dotHeightPx
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        viewpager2 = view.findViewById(R.id.viewpager2)
        auth = FirebaseAuth.getInstance()
        nameOfUser = view.findViewById(R.id.name)
        upcomingEvent = view.findViewById(R.id.upcomingEvent)
        shoppingCart = view.findViewById(R.id.imageView3)
        cartSum = view.findViewById(R.id.cartSum) // Reference the cartSum TextView

        // Fetch and display the total cart sum
        fetchCartSum(db)

        shoppingCart.setOnClickListener {
            val shoppingCartFragment = ShoppingCartFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, shoppingCartFragment)
                .addToBackStack(null)
                .commit()
        }


        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid


            // Fetch user information
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name") ?: "User"
                        nameOfUser.text = "$userName!"
                        nameOfUser.textSize = if (userName.length > 10) 25f else 30f
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "User profile not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch user data",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            // Fetch upcoming event
            fetchUpcomingEvent(db, userId)
        } else {
            Toast.makeText(requireContext(), "No user is logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LogInSection::class.java)
            startActivity(intent)
            requireActivity().finish()
        }




        // --- ViewPager2 Setup ---


        val imageList = arrayListOf(
            ImageItem(
                UUID.randomUUID().toString(),
                "android.resource://" + requireContext().packageName + "/" + R.drawable.deal
            ),
            ImageItem(
                UUID.randomUUID().toString(),
                "android.resource://" + requireContext().packageName + "/" + R.drawable.deal
            ),
            ImageItem(
                UUID.randomUUID().toString(),
                "android.resource://" + requireContext().packageName + "/" + R.drawable.deal
            ),
            ImageItem(
                UUID.randomUUID().toString(),
                "android.resource://" + requireContext().packageName + "/" + R.drawable.deal
            ),
            ImageItem(
                UUID.randomUUID().toString(),
                "android.resource://" + requireContext().packageName + "/" + R.drawable.deal
            ),
        )
        val imageAdapter = ImageAdapter()
        viewpager2.adapter = imageAdapter
        imageAdapter.submitList(imageList)

        val slideDotLL = view.findViewById<LinearLayout>(R.id.slideDotLL)
        val dotsImage = Array(imageList.size) { ImageView(requireContext()) }

        dotsImage.forEach {
            it.setImageResource(R.drawable.non_active_dot)
            slideDotLL.addView(it, params)
        }

        dotsImage[0].apply {
            setImageResource(R.drawable.active_dot)
            layoutParams = LinearLayout.LayoutParams(
                activeDotWidthPx,
                dotHeightPx
            ).apply {
                setMargins(8, 0, 8, 0)
            }
        }

        pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dotsImage.forEachIndexed { index, imageView ->
                    if (index == position) {
                        imageView.setImageResource(R.drawable.active_dot)
                        imageView.layoutParams = LinearLayout.LayoutParams(
                            activeDotWidthPx,
                            dotHeightPx
                        ).apply {
                            setMargins(8, 0, 8, 0)
                        }
                    } else {
                        imageView.setImageResource(R.drawable.non_active_dot)
                        imageView.layoutParams = LinearLayout.LayoutParams(
                            nonActiveDotSizePx,
                            dotHeightPx
                        ).apply {
                            setMargins(8, 0, 8, 0)
                        }
                    }
                }
                super.onPageSelected(position)
            }
        }
        viewpager2.registerOnPageChangeCallback(pageChangeListener)

        // --- RecyclerView for Flowers ---
        val flowerRecyclerView = view.findViewById<RecyclerView>(R.id.gridRecyclerView)
        val flowers = mutableListOf<Flower>()

        // Pass the click handler to the adapter
        val flowerAdapter = FlowerAdapter(flowers) { flower ->
            // Handle item click: Navigate to another activity (BouquetPage)
            val intent = Intent(requireContext(), BouquetPage::class.java).apply {
                putExtra("flower_title", flower.title)
                putExtra("flower_price", flower.price)
                putExtra("flower_imageRes", flower.imageRes)
                putExtra("flower_description", flower.description)
            }
            startActivity(intent)
        }

        flowerRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        flowerRecyclerView.adapter = flowerAdapter

        // Fetch flowers from Firestore

        db.collection("flowers")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("name") ?: "Unknown Flower"
                    val price = document.getString("price") ?: "0 zł"
                    val description = document.getString("desc") ?: "Unknown desc"

                    android.util.Log.d(
                        "FlowerData",
                        "Title: $title, Price: $price, Description: $description"
                    )

                    val imageResName = document.getString("imageRes") ?: "default_image"
                    val imageResId = resources.getIdentifier(
                        imageResName,
                        "drawable",
                        requireContext().packageName
                    )
                        .takeIf { it != 0 } ?: R.drawable.bouqet_image

                    flowers.add(Flower(imageResId, title, price, description))
                }
                flowerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreError", "Error fetching flowers: ", e)
            }
    }
    private fun fetchCartSum(db: FirebaseFirestore) {
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
                        cartSum.text = "0.00 zł"
                        return@addOnSuccessListener
                    }

                    var totalSum = 0.0
                    querySnapshot.forEach { document ->
                        val priceString = document.getString("price")
                        android.util.Log.d("FirestoreDebug", "Document: $document")
                        android.util.Log.d("FirestoreDebug", "Price String: $priceString")

                        // Remove any extra spaces and replace " zl" or "zł" with empty strings
                        val price = priceString?.replace("zl", "")?.replace("zł", "")?.trim()?.toDoubleOrNull()
                        if (price != null) {
                            totalSum += price
                        } else {
                            android.util.Log.d("FirestoreDebug", "Invalid price found: $priceString")
                        }
                    }

                    // Update the total sum and display
                    android.util.Log.d("FirestoreDebug", "Total Sum: $totalSum")
                    cartSum.text = String.format("%.2f zł", totalSum)
                    //Toast.makeText(requireContext(), "Total Sum: $totalSum zł", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Firestore query failed", Toast.LENGTH_SHORT).show()
                    android.util.Log.e("FirestoreError", "Error fetching cart: ", e)
                }
        } else {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUpcomingEvent(db: FirebaseFirestore, userId: String) {
        db.collection("users")
            .document(userId)
            .collection("events")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val firstEvent = documents.firstOrNull()
                    firstEvent?.let { event ->
                        val eventName = event.getString("eventname") ?: "No Title"
                        val eventDate = event.getString("eventdate") ?: "No Date"
//                        val eventDesc = event.getString("description") ?: "No Description"
                        upcomingEvent.text = "$eventDate - $eventName"
                    }
                } else {
                    upcomingEvent.text = "No upcoming events"
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreError", "Error fetching events: ", e)
                upcomingEvent.text = "Error loading"
            }
    }
}

