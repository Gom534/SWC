package com.ex.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReviewFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var adapter: ReviewAdapter
    private var place: String? = null
    private var store: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_list, container, false)

        place = arguments?.getString("PLACE_KEY")
        store = arguments?.getString("STORE_KEY")

        recyclerView = view.findViewById(R.id.reRecyclerView)
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(place.toString())

        // Adapter 초기화
        adapter = ReviewAdapter(emptyList(), place, store)
        recyclerView.adapter = adapter

        loadData()

        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as? Seller)?.setButtonVisibility(false)

        //Reply에서 다시 돌아왔을때 데이터 refresh 메소드
        loadData()
    }

    override fun onPause() {
        super.onPause()

        loadData()
    }
    //Reply에서 다시 돌아왔을때 데이터 refresh 메소드
    private fun loadData() {
        myRef.child("리뷰페이지").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reviews = mutableListOf<Review>()

                for (reviewSnapshot in dataSnapshot.child(store.toString()).children) {
                    val reviewId = reviewSnapshot.key ?: "ID 없음"
                    val title = reviewSnapshot.child("title").getValue(String::class.java)
                    val content = reviewSnapshot.child("content").getValue(String::class.java)
                    val rating = reviewSnapshot.child("rating").getValue(Int::class.java)
                    val time = reviewSnapshot.child("time").getValue(String::class.java)

                    val oner = reviewSnapshot.child("답변").child("사장님").key
                    val answer = reviewSnapshot.child("답변").child("사장님").child("내용").getValue(String::class.java)
                    val answer_time = reviewSnapshot.child("답변").child("사장님").child("시간").getValue(String::class.java)

                    val imageUrls = mutableListOf<String>()
                    val imagesRef = reviewSnapshot.child("images")
                    imagesRef.children.forEach { imageSnapshot ->
                        val imageUri = imageSnapshot.getValue(String::class.java)
                        if (imageUri != null) {
                            imageUrls.add(imageUri)
                        }
                    }

                    val review = Review(
                        title ?: "제목 없음",
                        content ?: "내용 없음",
                        rating ?: 0,
                        time ?: "시간 없음",
                        imageUrls,
                        oner ?: "답변 없음",
                        answer ?: "답변 없음",
                        answer_time ?: "시간 없음",
                        reviewId
                    )
                    reviews.add(review)
                }

                val reversedReviews = reviews.reversed()

                // 어댑터에 새로운 데이터 설정
                adapter = ReviewAdapter(reversedReviews, place, store)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", databaseError.message)
            }
        })
    }
}
