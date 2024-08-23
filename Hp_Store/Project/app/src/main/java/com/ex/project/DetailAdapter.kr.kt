package com.ex.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class DetailItemAdapter(
    private val detailList: MutableList<DetailItem>
    ): RecyclerView.Adapter<DetailItemAdapter.DetailViewHolder>() {

    class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val detailNum: TextView = itemView.findViewById(R.id.detailNubTextView)
        val detailFood: TextView = itemView.findViewById(R.id.detailFoodTextView)
        val detailPrice: TextView = itemView.findViewById(R.id.detailPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_item, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val detailItem = detailList[position]
        holder.detailNum.text = "주문번호: ${detailItem.orderNum}"
        holder.detailFood.text = "음식명: ${detailItem.foodNames.toString().trim()}"
        holder.detailPrice.text = "가격: ${detailItem.totalPrice} 원"
    }

    override fun getItemCount(): Int = detailList.size


}