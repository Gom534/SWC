package com.ex.project

import android.content.Context
import android.os.Message
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class Db(context: Context){
    private val myRef = FirebaseDatabase.getInstance().getReference("messages")


    init{
        var database = Firebase.database.reference
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.getValue(Message::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터베이스 작업이 취소될 때마다 호출됩니다.
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

    }
}
