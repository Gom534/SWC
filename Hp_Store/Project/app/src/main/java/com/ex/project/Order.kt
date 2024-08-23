package com.ex.project

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class Order : AppCompatActivity() {

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderItemAdapter: OrderItemAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val orderList = mutableListOf<Pair<String, Pair<StringBuilder, Int>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val place = intent.getStringExtra("PLACE_KEY").orEmpty()
        val store = intent.getStringExtra("STORE_KEY").orEmpty()

        orderRecyclerView = findViewById(R.id.orderRecyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)


        orderItemAdapter = OrderItemAdapter(orderList, place, store, database, this)
        orderRecyclerView.layoutManager = LinearLayoutManager(this)
        orderRecyclerView.adapter = orderItemAdapter


        val myRef: DatabaseReference = database.getReference("결제완료")

        //초창기 값
        loadOrderData(myRef, place, store)

        // SwipeRefresh 새로고침
        swipeRefreshLayout.setOnRefreshListener {
            loadOrderData(myRef, place, store)
        }

        val sellerBtn = findViewById<Button>(R.id.sellerBtn)
        sellerBtn.setOnClickListener {
            val imfomation = Intent(this@Order, Seller::class.java).apply {
                putExtra("PLACE_KEY", place)
                putExtra("STORE_KEY", store)
            }
            startActivity(imfomation)
        }
        //정산페이지 이동
        val moneyBtn = findViewById<Button>(R.id.moneyBtn)
        moneyBtn.setOnClickListener{
            val moneyIntent = Intent(this@Order, Money::class.java).apply {
                putExtra("PLACE_KEY", place)
                putExtra("STORE_KEY", store)
            }
            startActivity(moneyIntent)
        }
    }

    private fun loadOrderData(myRef: DatabaseReference, place: String, store: String) {
        swipeRefreshLayout.isRefreshing = true // 새로고침 시작 표시

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ordersMap = mutableMapOf<String, Pair<StringBuilder, Int>>()

                for (orderSnapshot in dataSnapshot.child(place).child(store).children) {
                    val orderNo = orderSnapshot.key ?: continue

                    //주문번호가 int 일경우만 가지고오기 - 이전내역 빼기 위해서
                    val orderNoAsInt = orderNo.toIntOrNull() ?: continue

                    if (!ordersMap.containsKey(orderNoAsInt.toString())) { // orderNoAsInt를 사용
                        ordersMap[orderNoAsInt.toString()] = Pair(StringBuilder(), 0)
                    }

                    for (foodSnapshot in orderSnapshot.children) {
                        val foodName = foodSnapshot.key ?: continue
                        val price = foodSnapshot.child("가격").getValue(Int::class.java) ?: 0

                        if (ordersMap[orderNoAsInt.toString()]?.first?.isNotEmpty() == true) {
                            ordersMap[orderNoAsInt.toString()]?.first?.append(", ")
                        }
                        ordersMap[orderNoAsInt.toString()]?.first?.append("$foodName ")
                        ordersMap[orderNoAsInt.toString()] = Pair(ordersMap[orderNoAsInt.toString()]?.first ?: StringBuilder(), (ordersMap[orderNoAsInt.toString()]?.second ?: 0) + price)
                    }
                }

                orderList.clear()
                for ((orderNo, details) in ordersMap) {
                    orderList.add(Pair(orderNo, details))
                }

                orderItemAdapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false // 새로고침 종료
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "데이터 로드 실패: ${error.message}")
                swipeRefreshLayout.isRefreshing = false // 새로고침 종료
            }
        })
    }

}
