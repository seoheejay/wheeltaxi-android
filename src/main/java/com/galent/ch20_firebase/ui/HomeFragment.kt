package com.galent.ch20_firebase.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.galent.ch20_firebase.databinding.FragmentHomeBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import com.bumptech.glide.Glide
import com.galent.ch20_firebase.MyApplication
import com.galent.ch20_firebase.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.Locale


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    private val apiKey = "REMOVED"
    private val city = "Seoul"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        fetchWeather()
        fetchLatestRequest()
        fetchGuardianInfo()

        binding.btnGoToReservation.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, ReservationFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun fetchLatestRequest() {
        Log.d("checkEmail", "현재 로그인된 이메일: ${MyApplication.email}")

        MyApplication.db.collection("requests")
            .whereEqualTo("email", MyApplication.email)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                Log.d("checkFirestore", "받은 문서 수: ${result.size()}")
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val timestamp = doc.getTimestamp("date")
                    val formatted = timestamp?.toDate()?.let {
                        java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it)
                    } ?: "날짜 없음"

                    val summary = "출발: ${doc["start"]}\n도착: ${doc["end"]}\n시간: $formatted"
                    binding.textSummary.text = summary
                } else {
                    binding.textSummary.text = "신청 내역이 없습니다."
                }
            }
            .addOnFailureListener {
                binding.textSummary.text = "신청 정보를 불러오지 못했습니다."
                Log.e("checkFirestore", "오류: ${it.message}")
            }
    }

    private fun fetchGuardianInfo() {
        val pref = requireContext().getSharedPreferences("setting", Context.MODE_PRIVATE)
        val guardianName = pref.getString("guardian_name", "")
        val guardianPhone = pref.getString("guardian_phone", "")

        if (!guardianName.isNullOrBlank() && !guardianPhone.isNullOrBlank()) {
            binding.textGuardianInfo.text = "보호자: $guardianName\n전화번호: $guardianPhone"
            binding.btnCallGuardian.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$guardianPhone"))
                startActivity(dialIntent)
            }
        } else {
            binding.textGuardianInfo.text = "보호자 정보가 없습니다."
            binding.btnCallGuardian.setOnClickListener {
                Toast.makeText(requireContext(), "설정에서 보호자 정보를 먼저 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }






    private fun fetchWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlString = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=kr"
                Log.d("Weather", "Request URL: $urlString")

                val result = URL(urlString).readText()

                val json = JSONObject(result)
                val temp = json.getJSONObject("main").getDouble("temp")
                val desc = json.getJSONArray("weather").getJSONObject(0).getString("description")
                val name = json.getString("name")
                val icon = json.getJSONArray("weather").getJSONObject(0).getString("icon")
                val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                withContext(Dispatchers.Main) {
                    if (isAdded && context != null) {
                        binding.textWeather.text = "[$name] $desc, ${temp}도"

                        Glide.with(this@HomeFragment)
                            .load(iconUrl)
                            .error(R.drawable.ic_weather_error)
                            .into(binding.imageWeatherIcon)
                    }
                }
            } catch (e: Exception) {
                Log.e("Weather", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "날씨 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
