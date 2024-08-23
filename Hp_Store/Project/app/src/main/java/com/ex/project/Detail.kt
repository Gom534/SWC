package com.ex.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class DetailItem(
    val orderNum: String,
    val foodNames: StringBuilder,
    val totalPrice: Int
)

class Detail : AppCompatActivity() {

    private lateinit var detailRecyclerView: RecyclerView
    private lateinit var detailItemAdapter: DetailItemAdapter

    private val detailList = mutableListOf<DetailItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.detail)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val place = intent.getStringExtra("PLACE_KEY").orEmpty()
        val store = intent.getStringExtra("STORE_KEY").orEmpty()
        val selectedDate = intent.getStringExtra("DATE_KEY").orEmpty()

//        Log.d("Detail", "$place $store $selectedDate")
        detailRecyclerView = findViewById(R.id.detailRecyclerView)

        detailItemAdapter = DetailItemAdapter(detailList)
        detailRecyclerView.layoutManager = LinearLayoutManager(this)
        detailRecyclerView.adapter = detailItemAdapter

        val myRef: DatabaseReference = database.getReference("결제완료")

        loadDetailData(myRef, place, store, selectedDate)

        val detailBackBtn = findViewById<ImageButton>(R.id.detailBackBtn)
        detailBackBtn.setOnClickListener {
            finish()
        }

        val detailHomeBtn = findViewById<ImageButton>(R.id.detailHomeBtn)
        detailHomeBtn.setOnClickListener{
            val home = Intent(this@Detail, Order::class.java).apply{
                putExtra("PLACE_KEY", place)
                putExtra("STORE_KEY", store)
            }
            startActivity(home)
        }
    }

    private fun loadDetailData(myRef: DatabaseReference, place: String, store: String, selectedDate: String) {
        myRef.child(place).child(store).child("이전내역").child(selectedDate)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val detailMap = mutableMapOf<String, Pair<StringBuilder, Int>>()

                    for (orderSnapshot in dataSnapshot.children) {
                        val orderNum = orderSnapshot.key ?: continue

                        if (!detailMap.containsKey(orderNum)) {
                            detailMap[orderNum] = Pair(StringBuilder(), 0)
                        }

                        for (foodSnapshot in orderSnapshot.children) {
                            val foodName = foodSnapshot.key ?: continue
                            val price = foodSnapshot.child("가격").getValue(Int::class.java) ?: 0

                            if (detailMap[orderNum]?.first?.isNotEmpty() == true) {
                                detailMap[orderNum]?.first?.append(", ")
                            }
                            detailMap[orderNum]?.first?.append(foodName)
                            detailMap[orderNum] = Pair(
                                detailMap[orderNum]?.first ?: StringBuilder(),
                                (detailMap[orderNum]?.second ?: 0) + price
                            )
                        }
                    }

                    detailList.clear()
                    for ((orderNo, details) in detailMap) {
                        detailList.add(DetailItem(orderNo, details.first, details.second))
                    }

                    detailItemAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Detail", "loadDetailData:onCancelled", databaseError.toException())
                }
            })
    }

}
