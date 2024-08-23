package com.ex.project

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.InputStream

class Search : AppCompatActivity() {
    private lateinit var restStopNames: List<String>
    private lateinit var editTextSearch: EditText
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        editTextSearch = findViewById(R.id.editTextSearch)
        listView = findViewById(R.id.listView2)
        fileRead()

        // Adapter 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, restStopNames)
        listView.adapter = adapter

        // 엔터 키 입력을 감지하여 검색을 완료하는 방법
        editTextSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // 검색 작업 완료 후 결과를 설정하고 종료
                val resultIntent = Intent(this@Search, NewId::class.java).apply {
                putExtra("EXTRA_KEY", editTextSearch.text.toString())
            }
                startActivity(resultIntent)
                finish()
                true
            } else {
                false
            }
        }

        // ListView 항목 클릭 이벤트 설정 - 검색 클릭시 값이 이상해 수정해야함 id 값을 하나 더 넣어서 찾아서 기준을 하나 잡아줘야함
       listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = restStopNames[position]
            editTextSearch.setText(selectedItem)
            Log.d("ListViewItemClick", "Clicked item: $view, $id")
            // 검색 기능 유지
            adapter.filter.filter(selectedItem)
        }

        // EditText 검색 기능 추가
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
        })
        val searchtoolbar = findViewById<Toolbar>(R.id.searchtoolbar)
        setSupportActionBar(searchtoolbar)

        // 뒤로 가기 버튼 설정
        val backBtn = searchtoolbar.findViewById<ImageButton>(R.id.searchbackBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fileRead() {
        val assetManager: AssetManager = applicationContext.assets
        val inputStream = assetManager.open("rest_stops1.csv")
        restStopNames = readRestStopNamesFromCsv(inputStream)
        //Log.d("RestStopNames", restStopNames.toString())
    }

    private fun readRestStopNamesFromCsv(inputStream: InputStream): List<String> {
        val nameList = mutableListOf<String>()

        inputStream.bufferedReader(Charsets.UTF_8).useLines { lines ->
            val iterator = lines.iterator()
            if (iterator.hasNext()) iterator.next() // 헤더 행을 건너뛰기
            iterator.forEachRemaining { line ->
                val columns = line.split(",")
                val restStopName = columns[0] // 첫 번째 열의 값
                nameList.add(restStopName)
            }
        }
        return nameList
    }
}
