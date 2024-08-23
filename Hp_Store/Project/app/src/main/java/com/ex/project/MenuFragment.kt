package com.ex.project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.menu_list, container, false)

        val place = arguments?.getString("PLACE_KEY")
        val store = arguments?.getString("STORE_KEY")

        // RecyclerView 및 LinearLayout 초기화
        recyclerView = view.findViewById(R.id.MenuRecyclerView)

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(place.toString())

        // 데이터 읽기
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val foods = mutableListOf<Food>()
                //var totalMoney = 0

                for (foodSnapshot in dataSnapshot.child(store.toString()).children) {
                    val foodName = foodSnapshot.key
                    val price = foodSnapshot.child("가격").getValue(Int::class.java)
                    val number = foodSnapshot.child("총수량").getValue(Int::class.java)
                    val nowNumber = foodSnapshot.child("현재수량").getValue(Int::class.java)
                    val uri = foodSnapshot.child("이미지").getValue(String::class.java)

                    val food = Food(foodName.orEmpty(), price ?: 0, uri.orEmpty())
                    foods.add(food)
                }

                // RecyclerView 어댑터 설정
                val adapter = FoodAdapter(requireActivity(), foods)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", "데이터 불러오기 실패: ", databaseError.toException())
            }
        })

        return view
    }
    override fun onResume() {
        super.onResume()
        (activity as? Seller)?.setButtonVisibility(true)

    }
}