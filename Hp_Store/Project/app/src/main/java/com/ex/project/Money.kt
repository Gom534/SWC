package com.ex.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class Money : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var totalMoneyTextView: TextView
    private lateinit var totalFoodTextView: TextView
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.money)

        totalMoneyTextView = findViewById(R.id.totalMoney)
        totalFoodTextView = findViewById(R.id.totalFood)

        val place = intent.getStringExtra("PLACE_KEY").orEmpty()
        Log.d("장소", place)
        val store = intent.getStringExtra("STORE_KEY").orEmpty()
        Log.d("장소", store)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        val calendarView = findViewById<CalendarView>(R.id.calender)

        // 현재 날짜를 기본값으로 설정
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

        // CalendarView에 기본 날짜 설정
        calendarView.setDate(calendar.timeInMillis)

        // 초기 데이터 로드
        loadMoneyData(myRef, place, store, selectedDate)

        // 날짜 변경 리스너 설정
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            Log.d("날짜 선택", selectedDate)
            loadMoneyData(myRef, place, store, selectedDate)
        }

        val moneyBackBtn = findViewById<ImageButton>(R.id.moneyBackBtn)
        moneyBackBtn.setOnClickListener {
            val information = Intent(this@Money, Order::class.java).apply {
                putExtra("PLACE_KEY", place)
                putExtra("STORE_KEY", store)
            }
            startActivity(information)
        }

        val detailBtn = findViewById<Button>(R.id.detailBtn)
        detailBtn.setOnClickListener {
            val detail = Intent(this@Money, Detail::class.java).apply {
                putExtra("PLACE_KEY", place)
                putExtra("STORE_KEY", store)
                putExtra("DATE_KEY", selectedDate)
            }
            startActivity(detail)
        }
    }

    private fun loadMoneyData(myRef: DatabaseReference, place: String, store: String, selectedDate: String) {
        myRef.child("결제완료")
            .child(place)
            .child(store)
            .child("이전내역")
            .child(selectedDate)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var totalAmount = 0L
                    val foodCountMap = mutableMapOf<String, Int>()

                    if (dataSnapshot.exists()) {
                        for (orderSnapshot in dataSnapshot.children) {
                            if (orderSnapshot.hasChildren()) {
                                for (foodSnapshot in orderSnapshot.children) {
                                    val foodName = foodSnapshot.key.orEmpty()
                                    val price = foodSnapshot.child("가격").getValue(Long::class.java) ?: 0L

                                    totalAmount += price

                                    if (foodName.isNotEmpty()) {
                                        foodCountMap[foodName] = foodCountMap.getOrDefault(foodName, 0) + 1

                                    }
                                }
                            }
                        }

                        totalMoneyTextView.text = "$totalAmount 원"

                        val foodDisplayText = StringBuilder()
                        for ((foodName, count) in foodCountMap) {
                            foodDisplayText.append("$foodName : $count 개 \n")

                        }

                        totalFoodTextView.text = foodDisplayText.toString().trim()
                    } else {
                        totalMoneyTextView.text = "0 원"
                        totalFoodTextView.text = ""
                        Log.d("불러온 데이터", "해당 날짜에 데이터가 없습니다.")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("loadMoneyData", "loadMoneyData:onCancelled", databaseError.toException())
                }
            })
    }


}
