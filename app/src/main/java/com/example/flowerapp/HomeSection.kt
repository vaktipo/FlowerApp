//package com.example.flowerapp
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.TextView
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.widget.ViewPager2
//import com.bumptech.glide.Glide
//import com.example.flowerapp.adapters.ImageAdapter
//import com.example.flowerapp.databinding.HomeSectionBinding
//import com.example.flowerapp.models.ImageItem
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import java.util.UUID
//
//data class Flower(val imageRes: Int, val title: String, val price: String)
//
//class FlowerAdapter(private val flowerList: List<Flower>) :
//    RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder>() {
//
//    inner class FlowerViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
//        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
//        val titleView: TextView = itemView.findViewById(R.id.itemTitle)
//        val priceView: TextView = itemView.findViewById(R.id.itemPrice)
//    }
//
//    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): FlowerViewHolder {
//        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.grid_item, parent, false)
//        return FlowerViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
//        val flower = flowerList[position]
//        holder.titleView.text = flower.title
//        holder.priceView.text = flower.price
//        Glide.with(holder.imageView.context).load(flower.imageRes).into(holder.imageView)
//    }
//
//    override fun getItemCount(): Int = flowerList.size
//}
//
//class HomeSection : AppCompatActivity() {
//    private lateinit var viewpager2: ViewPager2
//    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
//    private lateinit var auth: FirebaseAuth
//    private lateinit var nameOfUser: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.home_section)
//        supportActionBar?.hide()
//
//        val nonActiveDotSizeDp = 5
//        val activeDotWidthDp = 12
//        val dotHeightDp = 5
//        val density = resources.displayMetrics.density
//        val nonActiveDotSizePx = (nonActiveDotSizeDp * density).toInt()
//        val activeDotWidthPx = (activeDotWidthDp * density).toInt()
//        val dotHeightPx = (dotHeightDp * density).toInt()
//
//        val params = LinearLayout.LayoutParams(
//            nonActiveDotSizePx,
//            dotHeightPx
//        ).apply {
//            setMargins(8, 0, 8, 0)
//        }
//
//        auth = FirebaseAuth.getInstance()
//        nameOfUser = findViewById(R.id.name)
//
//        val currentUser = auth.currentUser
//
//        if (currentUser != null) {
//            val userId = currentUser.uid
//            val db = Firebase.firestore
//            db.collection("users").document(userId).get()
//                .addOnSuccessListener { document ->
//                    if (document != null) {
//                        val userName = document.getString("name") ?: "User"
//                        if (userName.length > 10) {
//                            nameOfUser.text = "$userName!"
//                            nameOfUser.textSize = 25f
//                        } else {
//                            nameOfUser.text = "$userName!"
//                            nameOfUser.textSize = 30f
//                        }
//
//                    } else {
//                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
//                    e.printStackTrace()
//                }
//        } else {
//            // If no user is logged in, redirect to Login
//            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, LogInSection::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        viewpager2 = findViewById<ViewPager2>(R.id.viewpager2)
//
//        val imageList = arrayListOf(
//            ImageItem(
//                UUID.randomUUID().toString(),
//                "android.resource://" + packageName + "/" + R.drawable.deal
//            ),
//            ImageItem(
//                UUID.randomUUID().toString(),
//                "android.resource://" + packageName + "/" + R.drawable.deal
//            ),
//            ImageItem(
//                UUID.randomUUID().toString(),
//                "android.resource://" + packageName + "/" + R.drawable.deal
//            ),
//            ImageItem(
//                UUID.randomUUID().toString(),
//                "android.resource://" + packageName + "/" + R.drawable.deal
//            ),
//            ImageItem(
//                UUID.randomUUID().toString(),
//                "android.resource://" + packageName + "/" + R.drawable.deal
//            ),
//        )
//        val imageAdapter = ImageAdapter()
//        viewpager2.adapter = imageAdapter
//        imageAdapter.submitList(imageList)
//
//        val slideDotLL = findViewById<LinearLayout>(R.id.slideDotLL)
//        val dotsImage = Array(imageList.size) { ImageView(this) }
//
//        dotsImage.forEach {
//            it.setImageResource(R.drawable.non_active_dot)
//            slideDotLL.addView(it, params)
//        }
//
//        dotsImage[0].apply {
//            setImageResource(R.drawable.active_dot)
//            layoutParams = LinearLayout.LayoutParams(
//                activeDotWidthPx,
//                dotHeightPx
//            ).apply {
//                setMargins(8, 0, 8, 0)
//            }
//        }
//
//        pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                dotsImage.forEachIndexed { index, imageView ->
//                    if (index == position) {
//                        imageView.setImageResource(R.drawable.active_dot)
//                        imageView.layoutParams = LinearLayout.LayoutParams(
//                            activeDotWidthPx,
//                            dotHeightPx
//                        ).apply {
//                            setMargins(8, 0, 8, 0)
//                        }
//                    } else {
//                        imageView.setImageResource(R.drawable.non_active_dot)
//                        imageView.layoutParams = LinearLayout.LayoutParams(
//                            nonActiveDotSizePx,
//                            dotHeightPx
//                        ).apply {
//                            setMargins(8, 0, 8, 0)
//                        }
//                    }
//                }
//                super.onPageSelected(position)
//            }
//        }
//        viewpager2.registerOnPageChangeCallback(pageChangeListener)
//
//        // --- Add RecyclerView for Flowers ---
//        val flowerRecyclerView = findViewById<RecyclerView>(R.id.gridRecyclerView)
//        val flowers = listOf(
//            Flower(R.drawable.bouqet_image, "Blush Harmony", "100 zł"),
//            Flower(R.drawable.bouqet_image, "Sunlit Serenity", "150 zł"),
//            Flower(R.drawable.bouqet_image, "Rosy Dreams", "120 zł"),
//            Flower(R.drawable.bouqet_image, "Pink Paradise", "140 zł"),
//            Flower(R.drawable.bouqet_image, "Sunlit Serenity", "150 zł"),
//            Flower(R.drawable.bouqet_image, "Rosy Dreams", "120 zł"),
//            Flower(R.drawable.bouqet_image, "Pink Paradise", "140 zł"),
//            Flower(R.drawable.bouqet_image, "Sunlit Serenity", "150 zł"),
//            Flower(R.drawable.bouqet_image, "Rosy Dreams", "120 zł"),
//            Flower(R.drawable.bouqet_image, "Pink Paradise", "140 zł")
//        )
//        val flowerAdapter = FlowerAdapter(flowers)
//        flowerRecyclerView.layoutManager = GridLayoutManager(this, 2)
//        flowerRecyclerView.adapter = flowerAdapter
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        viewpager2.unregisterOnPageChangeCallback(pageChangeListener)
//    }
//}
//
////package com.example.flowerapp
////
////import android.content.Intent
////import android.os.Bundle
////import android.widget.ImageView
////import android.widget.LinearLayout
////import android.widget.TextView
////import android.widget.Toast
////import androidx.appcompat.app.AppCompatActivity
////import androidx.recyclerview.widget.GridLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import androidx.viewpager2.widget.ViewPager2
////import com.bumptech.glide.Glide
////import com.example.flowerapp.adapters.ImageAdapter
////import com.example.flowerapp.models.ImageItem
////import com.google.android.material.bottomnavigation.BottomNavigationView
////import com.google.firebase.auth.FirebaseAuth
////import com.google.firebase.firestore.ktx.firestore
////import com.google.firebase.ktx.Firebase
////import java.util.UUID
////
////// Updated Flower data class
//////data class Flower(val imageRes: Int, val title: String, val price: String)
////
////// FlowerAdapter
//////class FlowerAdapter(private val flowerList: List<Flower>) :
////    RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder>() {
////
////    inner class FlowerViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
////        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
////        val titleView: TextView = itemView.findViewById(R.id.itemTitle)
////        val priceView: TextView = itemView.findViewById(R.id.itemPrice)
////    }
////
////    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): FlowerViewHolder {
////        val view = android.view.LayoutInflater.from(parent.context)
////            .inflate(R.layout.grid_item, parent, false)
////        return FlowerViewHolder(view)
////    }
////
////    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
////        val flower = flowerList[position]
////        holder.titleView.text = flower.title
////        holder.priceView.text = flower.price
////        holder.imageView.setImageResource(flower.imageRes) // Load image from resource ID
////    }
////
////    override fun getItemCount(): Int = flowerList.size
////}
////
////// HomeSection Activity
////class HomeSection : AppCompatActivity() {
////    private lateinit var viewpager2: ViewPager2
////    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
////    private lateinit var auth: FirebaseAuth
////    private lateinit var nameOfUser: TextView
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.home_section)
////        supportActionBar?.hide()
////
////        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.BottomNavigationView)
////
////
////        bottomNavigationView.setOnItemSelectedListener { item ->
////            when (item.itemId) {
////                R.id.bottom_home -> {
////                    bottomNavigationView.selectedItemId = R.id.bottom_home
////                    true
////                }
////                R.id.bottom_calendar -> {
////
////                    true
////                }
////                R.id.bottom_account -> {
////
////                    val intent = Intent(this, ProfileSection::class.java)
////                    startActivity(intent)
////                    true
////                }
////                else -> false
////            }
////        }
////
////        auth = FirebaseAuth.getInstance()
////        nameOfUser = findViewById(R.id.name)
////
////        val currentUser = auth.currentUser
////        if (currentUser != null) {
////            val userId = currentUser.uid
////            val db = Firebase.firestore
////            db.collection("users").document(userId).get()
////                .addOnSuccessListener { document ->
////                    if (document != null) {
////                        val userName = document.getString("name") ?: "User"
////                        nameOfUser.text = "$userName!"
////                        nameOfUser.textSize = if (userName.length > 10) 25f else 30f
////                    } else {
////                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
////                    }
////                }
////                .addOnFailureListener {
////                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
////                }
////        } else {
////            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
////            val intent = Intent(this, LogInSection::class.java)
////            startActivity(intent)
////            finish()
////        }
////
////        // --- ViewPager2 Setup ---
////        viewpager2 = findViewById(R.id.viewpager2)
////        val imageList = arrayListOf(
////            ImageItem(UUID.randomUUID().toString(), "android.resource://" + packageName + "/" + R.drawable.deal),
////            ImageItem(UUID.randomUUID().toString(), "android.resource://" + packageName + "/" + R.drawable.deal)
////        )
////        val imageAdapter = ImageAdapter()
////        viewpager2.adapter = imageAdapter
////        imageAdapter.submitList(imageList)
////
////        // --- RecyclerView for Flowers ---
////        val flowerRecyclerView = findViewById<RecyclerView>(R.id.gridRecyclerView)
////        val flowers = mutableListOf<Flower>()
////        val flowerAdapter = FlowerAdapter(flowers)
////        flowerRecyclerView.layoutManager = GridLayoutManager(this, 2)
////        flowerRecyclerView.adapter = flowerAdapter
////
////        // Fetch flowers from Firestore
////        val db = Firebase.firestore
////        db.collection("flowers")
////            .get()
////            .addOnSuccessListener { documents ->
////                for (document in documents) {
////                    val title = document.getString("name") ?: "Unknown Flower"
////                    val price = document.getString("price") ?: "0 zł"
////                    val imageResName = document.getString("imageRes") ?: "default_image"
////
////                    // Map Firestore imageRes name to drawable resource ID
////                    val imageResId = resources.getIdentifier(imageResName, "drawable", packageName)
////                        .takeIf { it != 0 } ?: R.drawable.bouqet_image
////
////                    flowers.add(Flower(imageResId, title, price))
////                }
////                flowerAdapter.notifyDataSetChanged()
////            }
////            .addOnFailureListener {
////                Toast.makeText(this, "Failed to load flowers", Toast.LENGTH_SHORT).show()
////            }
////    }
////
////    override fun onDestroy() {
////        super.onDestroy()
////        viewpager2.unregisterOnPageChangeCallback(pageChangeListener)
////    }
////}
