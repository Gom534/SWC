package com.ex.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NewId : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_id)

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        val yesBtn = findViewById<Button>(R.id.yesBtn)
        val idTextView = findViewById<EditText>(R.id.idTextView)
        val passwordTextView = findViewById<EditText>(R.id.passwordTextView)
        val passwordTextView2 = findViewById<EditText>(R.id.passwordTextView2)
        val cancelBtn = findViewById<Button>(R.id.cancelBtn)
        val placeNameTextView = findViewById<EditText>(R.id.placeNameTextView)
        val storeNameTextView = findViewById<EditText>(R.id.storeNameTextView)

        placeNameTextView.setOnClickListener {
            startActivity(Intent(this@NewId, Search::class.java))
        }

        // 전달된 값을 받아서 설정
        val editTextSearch = intent.getStringExtra("EXTRA_KEY")
        placeNameTextView.setText(editTextSearch)
        //Log.d("TAG", "EXTRA_KEY: $editTextSearch")


        // 확인 버튼 클릭 시 회원가입 처리
        yesBtn.setOnClickListener {
            val email = idTextView.text.toString()
            val emailId = idTextView.text.toString().replace("@","").replace(".","")
            val password = passwordTextView.text.toString()
            val confirmPassword = passwordTextView2.text.toString()
            val placeName = placeNameTextView.text.toString().replace(" ", "")
            val storeName = storeNameTextView.text.toString()
            // 비밀번호 확인
            if (password == confirmPassword) {
                // Firebase 회원가입 실행
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공 시 처리
                            startActivity(Intent(this, Login::class.java))
                            finish()

                            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                            val myRef: DatabaseReference = database.getReference("로그인")
                            myRef.child(emailId).child(placeName).child(storeName).setValue("사업자 번호")
                            Toast.makeText(this@NewId, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            // 회원가입 실패 시 처리
                            Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 버튼 클릭 시 처리
        cancelBtn.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

}