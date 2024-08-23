package com.ex.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ex.project.databinding.SellerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Seller : AppCompatActivity() {

    private var menuFragment = MenuFragment()
    private var reviewFragment = ReviewFragment()
    lateinit var mainBinding : SellerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = SellerBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val place = intent.getStringExtra("PLACE_KEY").orEmpty()
        val store = intent.getStringExtra("STORE_KEY").orEmpty()

        menuFragment = MenuFragment().apply {
            arguments = Bundle().apply {
                putString("PLACE_KEY", place)
                putString("STORE_KEY", store)
            }
        }
        reviewFragment = ReviewFragment().apply {
            arguments = Bundle().apply {
                putString("PLACE_KEY", place)
                putString("STORE_KEY", store)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.sellerFragLayout, menuFragment)
            .commit()

        setButtonVisibility(true)

        mainBinding.reviewFrgBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.sellerFragLayout, reviewFragment)
                .addToBackStack(null)
                .commit()
            setButtonVisibility(false)
        }
        mainBinding.menuFrgBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.sellerFragLayout, menuFragment)
                .addToBackStack(null)
                .commit()
            setButtonVisibility(true)
        }

        val button1 = findViewById<Button>(R.id.FoodAddBtn)
        button1.setOnClickListener {
            val information = Intent(this@Seller, FoodAdd::class.java).apply {
                putExtra("PLACE_KEY", place)
                putExtra("STORE_KEY", store)
            }
            startActivity(information)
            finish()
        }

        val sellertoolbar = findViewById<Toolbar>(R.id.sellertoolbar)
        setSupportActionBar(sellertoolbar)

        val sellerbackBtn = sellertoolbar.findViewById<ImageButton>(R.id.sellerbackBtn)
        sellerbackBtn.setOnClickListener {
            val information = Intent(this@Seller, Order::class.java).apply {
                putExtra("PLACE_KEY", place)
                putExtra("STORE_KEY", store)
            }
            startActivity(information)
        }
    }
     fun setButtonVisibility(isVisible: Boolean) {
        mainBinding.FoodAddBtn.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}




