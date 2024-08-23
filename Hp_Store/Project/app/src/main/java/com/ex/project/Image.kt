package com.ex.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class Image : AppCompatActivity() {

//    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image)

        val imageView: ImageView = findViewById(R.id.fullImageView)
        val imageUrl = intent.getStringExtra("Image_URL")
//        position = intent.getIntExtra("position", -1)
//        Log.d("position", "onCreate: $position")

        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .error(R.drawable.noimage)  // 에러 시 보여줄 이미지
                .into(imageView)
        }

        val closeBtn: ImageView = findViewById(R.id.colseView)
        closeBtn.setOnClickListener {
            finish()
        }
    }

//    private fun finishResult() {
//        val resultIntent = Intent().apply {
//            putExtra("position", position)
//        }
//        setResult(RESULT_OK, resultIntent)
//        finish()
//    }
}