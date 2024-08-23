package com.ex.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Reply : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.reply)

        //intent값 받아오기
        val reviewId = intent.getStringExtra("REVIEW_ID")
        val place = intent.getStringExtra("PLACE_KEY")
        val store = intent.getStringExtra("STORE_KEY")
//        Log.d("reviewId", reviewId.toString())
//        Log.d("reviewId", place.toString())
//        Log.d("reviewId", store.toString())
        //불러올 정보 정의
        val replyName : TextView = findViewById(R.id.replyNickNameText)
        val replyContent : TextView = findViewById(R.id.replyContentText)
        val replyTime : TextView = findViewById(R.id.replyTimeText)
        val replyRating : RatingBar = findViewById(R.id.replyRatingBar)
        val imageContainer: LinearLayout = findViewById(R.id.replyImgContainer)
        //firebase 넣어줄 값 정의
        val onerContent : EditText = findViewById(R.id.onerContentText)
        val inputBtn : Button = findViewById(R.id.replyInputBtn)


        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(place.toString()).child("리뷰페이지").child(store.toString())

        //스냅샷
        myRef.child(reviewId.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //불러올 정보들
                    val title = dataSnapshot.child("title").getValue(String::class.java)
                    val content = dataSnapshot.child("content").getValue(String::class.java)
                    val rating = dataSnapshot.child("rating").getValue(Int::class.java)
                    val time = dataSnapshot.child("time").getValue(String::class.java)
                    val images = dataSnapshot.child("images").children

                    //정보 넣어주기
                    replyName.text = title ?: "제목 없음"
                    replyContent.text = content ?: "내용 없음"
                    replyTime.text = time ?: "시간 없음"
                    replyRating.rating = rating?.toFloat() ?: 0f

                    //이미지 로드
                    for (imageSnapshot in images) {
                        val imageUrl = imageSnapshot.getValue(String::class.java) // URL 가져오기
                        val imageView = ImageView(this@Reply).apply {
                            layoutParams = LinearLayout.LayoutParams(250, 250)
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }

                        imageUrl?.let {
                            Glide.with(this@Reply)
                                .load(it)
                                .override(250, 250)
                                .error(R.drawable.noimage)
                                .into(imageView)
                        }
                        // LinearLayout에 이미지 추가
                        imageContainer.addView(imageView)
                    }

//                    Log.d("test", title.toString())
//                    Log.d("test", content.toString())
//                    Log.d("test", rating.toString())
//                    Log.d("test", time.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터베이스 오류 처리
                Log.e("FirebaseError", databaseError.message)
            }

        })

        inputBtn.setOnClickListener {
            val contentText = onerContent.text.toString()
            val currentTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Firebase에 데이터 추가
            myRef.child(reviewId.toString()).child("답변").child("사장님").setValue(mapOf(
                "내용" to contentText,
                "시간" to currentTime
            )).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@Reply, "답변이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Reply, "실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            finish()
        }
        val replybackBtn = findViewById<ImageView>(R.id.replybackBtn)
        replybackBtn.setOnClickListener {
            finish()
        }

    }
}