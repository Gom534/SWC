package com.ex.project

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class ReviewAdapter(private val reviews: List<Review>,
                    private val place: String?,
                    private val store: String?) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.NickNameText)
        val contentTextView: TextView = itemView.findViewById(R.id.reContentText)
        val ratingBar: RatingBar = itemView.findViewById(R.id.reRatingBar)
        val timeTextView: TextView = itemView.findViewById(R.id.reTimeText)
        val imageContainer: LinearLayout = itemView.findViewById(R.id.imgContainer)

        val anNameTextView: TextView = itemView.findViewById(R.id.anNameText)
        val anTimeText : TextView = itemView.findViewById(R.id.anTimeText)
        val anContentText: TextView = itemView.findViewById(R.id.anContentText)
        //답변 이동 버튼
        val replyBtn : Button  = itemView.findViewById(R.id.replyBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.titleTextView.text = review.title
        holder.contentTextView.text = review.content
        holder.ratingBar.rating = review.rating?.toFloat()?: 0f
        holder.timeTextView.text = review.time
        holder.imageContainer.removeAllViews() // 기존 이미지 뷰를 제거

        //답변이 있으면 표출하고 아니면 숨키기
        if (review.oner == "사장님" && review.answer == "답변 없음" && review.answer_time == "시간 없음") {
            holder.anNameTextView.visibility = View.GONE
            holder.anContentText.visibility = View.GONE
            holder.anTimeText.visibility = View.GONE
        } else {
            holder.anNameTextView.visibility = View.VISIBLE
            holder.anContentText.visibility = View.VISIBLE
            holder.anTimeText.visibility = View.VISIBLE

            holder.anNameTextView.text = review.oner
            holder.anContentText.text = review.answer
            holder.anTimeText.text = review.answer_time
        }

        //이미지 스크롤뷰 반복문 적용
        for (imageUrl in review.imageUrls) {
            val imageView = ImageView(holder.itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(250, 250)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .override(250, 250)
                .error(R.drawable.noimage)
                .into(imageView)

            //이미지 확대
            imageView.setOnClickListener{
                val intent = Intent(holder.itemView.context, Image::class.java).apply{
                    putExtra("Image_URL", imageUrl)
//                    putExtra("position", holder.adapterPosition)
//                    Log.d("position", holder.adapterPosition.toString())
                }
                holder.itemView.context.startActivity(intent)
            }

            // LinearLayout에 이미지 추가
            holder.imageContainer.addView(imageView)



        }

        //intent 키값 넘겨주기
        holder.replyBtn.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, Reply::class.java)
            intent.putExtra("REVIEW_ID", review.id)
            intent.putExtra("PLACE_KEY", place)
            intent.putExtra("STORE_KEY", store)
            context.startActivity(intent)
            }
        

    }
    override fun getItemCount() = reviews.size

}
