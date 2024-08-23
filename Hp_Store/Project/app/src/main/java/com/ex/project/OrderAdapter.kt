package com.ex.project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderItemAdapter(
    private val orderList: MutableList<Pair<String, Pair<StringBuilder, Int>>>,
    private val place: String,
    private val store: String,
    private val database: FirebaseDatabase,
    private val context: Context
) : RecyclerView.Adapter<OrderItemAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderNubTextView: TextView = itemView.findViewById(R.id.orderNubTextView)
        val foodSumTextView: TextView = itemView.findViewById(R.id.foodSumTextView)
        val priceSumTextView: TextView = itemView.findViewById(R.id.priceSumTextView)
        val trash: ImageButton = itemView.findViewById(R.id.trashBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_list, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val (orderNo, details) = orderList[position]
        val (foodNames, totalPrice) = details
        holder.orderNubTextView.text = "주문번호: $orderNo"
        holder.foodSumTextView.text = "음식명: ${foodNames.toString().trim()}"
        holder.priceSumTextView.text = "가격: $totalPrice"

        holder.trash.setOnClickListener {
            // 선택 창 띄우기
            val alert = AlertDialog.Builder(context)
            alert.setTitle("삭제 확인")
            alert.setMessage("삭제하시겠습니까?")
            alert.setPositiveButton("예") { dialog, which ->
                moveToPreviousOrder(orderNo, foodNames.toString().trim(), totalPrice, position)
            }
            alert.setNegativeButton("아니오") { dialog, which ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = alert.create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int = orderList.size

    // 현재 날짜
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        return dateFormat.format(Date())
    }

    private fun moveToPreviousOrder(orderNo: String, foodNames: String, totalPrice: Int, position: Int) {
        val myRef = database.getReference("결제완료/$place/$store/$orderNo")

        val currentDate = getCurrentDate()

        // 이전 내역 - 현재 날짜의 주문번호 하위로 이동할 참조 생성
        val previousOrdersRef = database.getReference("결제완료/$place/$store/이전내역/$currentDate/$orderNo")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    previousOrdersRef.setValue(dataSnapshot.value).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 원래 주문 데이터를 삭제
                            myRef.removeValue().addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    // 리스트에서 삭제
                                    orderList.removeAt(position) // 리스트에서 삭제
                                    notifyItemRemoved(position)
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "데이터 읽기 취소됨: ${databaseError.message}")
            }
        })
    }
}
