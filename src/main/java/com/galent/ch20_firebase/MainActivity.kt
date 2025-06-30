package com.galent.ch20_firebase

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.galent.ch20_firebase.databinding.ActivityMainBinding
import com.galent.ch20_firebase.ui.*
import com.google.firebase.messaging.FirebaseMessaging
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.View
import android.util.Base64
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addFab.visibility = View.GONE
        printHashKey(this)


        setSupportActionBar(binding.toolbar)

        // FCM 토큰 확인
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("25android", "FCM Token: $it")
        }

        binding.addFab.setOnClickListener {
            if (MyApplication.checkAuth()) {
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "인증을 먼저 진행해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 알림 권한 요청 (Android 13 이상)
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.all { permission -> permission.value != true }) {
                Toast.makeText(this, "알림 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
            }
        }

        // ActionBarDrawerToggle 설정 (툴바에 햄버거 버튼 연결)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 툴바 타이틀
        if (MyApplication.checkAuth() || MyApplication.email != null) {
            binding.toolbar.title = "${MyApplication.email} 님"
        } else {
            binding.toolbar.title = "안녕하세요"
        }

        // 초기 화면: HomeFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()

        // 메뉴 클릭 시 Fragment 전환
        binding.navigationView.setNavigationItemSelectedListener {
            val (selectedFragment, showFab) = when (it.itemId) {
                R.id.menu_home -> HomeFragment() to false
                R.id.menu_request -> RequestFragment() to false
                R.id.menu_map -> MapFragment() to false
                R.id.menu_community -> CommunityFragment() to true
                R.id.menu_setting -> SettingFragment() to false
                else -> HomeFragment() to false
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, selectedFragment)
                .commit()

            binding.drawerLayout.closeDrawers()
            binding.addFab.visibility = if (showFab) View.VISIBLE else View.GONE
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_login) {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.findItem(R.id.menu_login)
        item?.title = if (MyApplication.checkAuth() || MyApplication.email != null) "로그아웃" else "로그인"
        return super.onPrepareOptionsMenu(menu)
    }
    override fun attachBaseContext(newBase: Context?) {
        val pref = newBase?.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val fontScale = when (pref?.getInt("fontsize", 1)) {
            0 -> 0.85f
            1 -> 1.0f
            2 -> 1.15f
            else -> 1.0f
        }

        val config = Configuration(newBase?.resources?.configuration)
        config.fontScale = fontScale

        val context = newBase?.createConfigurationContext(config)
        super.attachBaseContext(context)
    }


    fun printHashKey(context: Context) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES // ✅ 모든 버전 호환
            )

            val signatures = packageInfo.signatures
            if (signatures != null) {
                for (signature in signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val hash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                    Log.d("HashKey", "해시키: $hash")
                }
            }
        } catch (e: Exception) {
            Log.e("HashKey", "해시키 생성 실패", e)
        }
    }


}
