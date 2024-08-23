package com.ex.project


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID
import com.bumptech.glide.Glide


class FoodAdd : AppCompatActivity() {
    private lateinit var storageReference: StorageReference
    private var filePath: Uri? = null
    private val pickImageRequestCode = 22
    private val permissionRequestCode = 101
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.food_add)

        val place = intent.getStringExtra("PLACE_KEY").orEmpty()
        val store = intent.getStringExtra("STORE_KEY").orEmpty()
        //Log.d("Fa", "place: $place")
        //Log.d("Fa", "store: $store")

        // ImageView 초기화
        imageView = findViewById(R.id.imageView)

        // Firebase Storage 초기화
        storageReference = FirebaseStorage.getInstance().reference

        // 버튼 초기화
        val buttonSelectImage: Button = findViewById(R.id.buttonSelectImage)

        // 이미지 선택 버튼 클릭 리스너
        buttonSelectImage.setOnClickListener {
            selectImage()
        }

        // 권한 요청
        requestPermission()

        val foodInsert = findViewById<EditText>(R.id.foodInsert) // 음식명 입력
        val priceInsert = findViewById<EditText>(R.id.priceInsert) // 가격 입력
        val numberInsert = findViewById<EditText>(R.id.numberInsert) // 전체 수량 입력
        val insertBtn = findViewById<Button>(R.id.insertBtn) // 입력 버튼

        insertBtn.setOnClickListener {
            uploadImageAndSaveData(place, store, foodInsert.text.toString(), priceInsert.text.toString(), numberInsert.text.toString())
        }

        val foodtoolbar = findViewById<Toolbar>(R.id.foodtoolbar)
        setSupportActionBar(foodtoolbar)

        // 뒤로 가기 버튼 설정
        val foodbackBtn = foodtoolbar.findViewById<ImageButton>(R.id.foodbackBtn)
        foodbackBtn.setOnClickListener {
            val imformation = Intent(this@FoodAdd, Seller::class.java).apply {
                putExtra("PLACE_KEY", place)
                putExtra("STORE_KEY", store)
            }
            startActivity(imformation)
        }
    }

    private fun selectImage() {
        // 인텐트를 사용하여 이미지 선택
        val imageIntent = Intent()
        imageIntent.type = "image/*"
        imageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(imageIntent, "Select Image"), pickImageRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequestCode && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // 선택한 이미지의 URI를 저장
            filePath = data.data
            // ImageView에 이미지 URI 설정
            imageView.setImageURI(filePath)
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            // 고유한 파일 이름 생성
            val ref = storageReference.child("images/" + UUID.randomUUID().toString())
            // 파일 업로드
            ref.putFile(filePath!!)
                .addOnSuccessListener { taskSnapshot ->
                    // 업로드 성공 시
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        Toast.makeText(this, "Image Uploaded: $downloadUrl", Toast.LENGTH_SHORT).show()
                        Glide.with(this)
                            .load(downloadUrl)
                            .into(imageView)
                    }
                }
                .addOnFailureListener { e ->
                    // 업로드 실패 시
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadImageAndSaveData(place: String, store: String, foodName: String, price: String, number: String) {
        if (filePath != null) {
            // 고유한 파일 이름 생성
            val ref = storageReference.child("images/" + UUID.randomUUID().toString())
            // 파일 업로드
            ref.putFile(filePath!!)
                .addOnSuccessListener { taskSnapshot ->
                    // 업로드 성공 시
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        saveData(place, store, foodName, price.toInt(), number.toInt(), downloadUrl)
                    }
                }
                .addOnFailureListener { e ->
                    // 업로드 실패 시
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveData(place: String, store: String, foodName: String, price: Int, number: Int, imageUrl: String) {
        Toast.makeText(this@FoodAdd, "$foodName, $price 원, $number 개가 입력되었습니다.", Toast.LENGTH_SHORT).show()

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef: DatabaseReference = database.getReference(place) // 음식 카테고리 정의
        myRef.child(store).child(foodName).child("가격").setValue(price) // 음식명 안에 가격 키값과 가격 값 저장
        myRef.child(store).child(foodName).child("총수량").setValue(number) // 음식명 안에 수량 키 값과 수량 값 저장
        myRef.child(store).child(foodName).child("현재수량").setValue(number)
        myRef.child(store).child(foodName).child("이미지").setValue(imageUrl) // 이미지 URL 저장

        // 입력 후 입력한 값 초기화
        findViewById<EditText>(R.id.foodInsert).setText(null)
        findViewById<EditText>(R.id.priceInsert).setText(null)
        findViewById<EditText>(R.id.numberInsert).setText(null)
        imageView.setImageURI(null)
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    permissionRequestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용됨
                Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 권한 거부됨
                // Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



