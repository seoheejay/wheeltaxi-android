package com.galent.ch20_firebase

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.galent.ch20_firebase.MyApplication.Companion.email
import com.galent.ch20_firebase.databinding.ActivityAddBinding
import java.text.SimpleDateFormat
import java.util.Date

class AddActivity : AppCompatActivity() {

    val TAG = "25android"
    lateinit var binding: ActivityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add_save){
            if(binding.addEditView.text.isNotEmpty()){
                val date_format = SimpleDateFormat("yyyy-MM-dd")
                // Firestore에 저장하기 : email, 시간, 게시글
                val data = mapOf(
                    "email" to MyApplication.email,
                    "content" to binding.addEditView.text.toString(),
                    "date" to date_format.format(Date())
                )
                MyApplication.db.collection("news")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d(TAG, "data save OK")
                        val helper = MyNotificationHelper(this)
                        helper.showNotification("Firestore","게시글이 추가되었습니다.")
                        finish()
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "data save ERROR")
                    }
            }
            else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}