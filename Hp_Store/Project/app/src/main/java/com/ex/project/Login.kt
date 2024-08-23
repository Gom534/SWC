package com.ex.project

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import android.Manifest

class Login : AppCompatActivity() {
    private lateinit var idEditText: EditText
    private lateinit var pwEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var newIdButton: Button

    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)

        // FCM 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "FCM 토큰: $token")
            } else {
                Log.e("FCM", "FCM 토큰 가져오기 실패", task.exception)
            }
        }

        // 뷰 참조 초기화
        idEditText = findViewById(R.id.idInsert)
        pwEditText = findViewById(R.id.pwInsert)
        loginButton = findViewById(R.id.loginBtn)
        newIdButton = findViewById(R.id.newIdBtn)

        // 권한 체크 및 요청
        checkNotificationPermission()

        // 로그인 버튼 클릭 리스너
        loginButton.setOnClickListener {
            val id = idEditText.text.toString()
            val pw = pwEditText.text.toString()
            login(id, pw)
        }

        // 회원가입 버튼 클릭 리스너
        newIdButton.setOnClickListener {
            val intent = Intent(this@Login, NewId::class.java)
            startActivity(intent)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 이상 체크
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되지 않은 경우 요청
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
            } else {
                // 권한이 이미 부여된 경우 알림을 보낼 수 있습니다.
                sendNotification()
            }
        } else {
            // API 33 미만에서는 권한 요청이 필요하지 않음
            sendNotification()
        }
    }

    private fun sendNotification() {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                sendNotification()
            } else {

                Log.d("Notification", "Notification permission denied")
            }
        }
    }

    private fun login(id: String, pw: String) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef: DatabaseReference = database.getReference("로그인")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(id, pw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공 처리
                    val id2 = idEditText.text.toString().replace("@", "").replace(".", "")
                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (idSnapshot in dataSnapshot.children) {
                                val emailid = idSnapshot.key


                                if (id2 == emailid.toString()) {
                                    val placeSnapshot = idSnapshot.children.iterator().next()
                                    val place = placeSnapshot.key
                                    val storeSnapshot = placeSnapshot.children.iterator().next()
                                    val store = storeSnapshot.key
                                    //Log.d("TAG", "place: $place")
                                    //Log.d("TAG", "store: $store")
                                    val imformation = Intent(this@Login, Order::class.java).apply {
                                        putExtra("PLACE_KEY", place)
                                        putExtra("STORE_KEY", store)
                                    }

                                    startActivity(imformation)
                                    finish()
                                    break
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // 데이터 불러오기 실패한 경우
                            Log.d("TAG", "데이터 불러오기 실패: ", databaseError.toException())
                        }
                    })

                    val intent = Intent(this@Login, Order::class.java)
                    startActivity(intent)
                } else {
                    // 로그인 실패 처리
                    Toast.makeText(this, "아이디와 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                    idEditText.setText(null)
                    pwEditText.setText(null)
                }
            }
    }

    private fun enableEdgeToEdge() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    }
}
