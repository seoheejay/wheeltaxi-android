package com.galent.ch20_firebase.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.galent.ch20_firebase.MyApplication
import com.galent.ch20_firebase.WebViewActivity
import com.galent.ch20_firebase.databinding.FragmentRequestBinding
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.*

class RequestFragment : Fragment() {
    lateinit var binding: FragmentRequestBinding
    private var selectedDateTime: Calendar? = null
    private var selectedAddressTarget: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }
    private val addressLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val data = it.data?.getStringExtra("selectedAddress")
            Log.d("ADDRESS", "받은 주소: $data")
            selectedAddressTarget?.setText(data)
        }
    }

    override fun onStart() {
        super.onStart()

        val pref = requireActivity().getSharedPreferences("setting", Context.MODE_PRIVATE)
        val defaultStart = pref.getString("start", "")
        binding.editStart.setText(defaultStart ?: "")

        // 즉시 신청
        binding.btnSubmit.setOnClickListener {
            val start = binding.editStart.text.toString()
            val end = binding.editEnd.text.toString()
            val memo = binding.editMemo.text.toString()
            val memoStart = binding.editMemoStart.text.toString()

            if (start.isBlank() || end.isBlank()) {
                Toast.makeText(context, "출발지와 도착지를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = mapOf(
                "email" to MyApplication.email,
                "start" to start,
                "end" to end,
                "memo" to memo,
                "memoStart" to memoStart,
                "date" to SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )

            MyApplication.db.collection("requests")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(context, "콜택시 즉시 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    clearForm()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "신청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
        }

        // 예약 신청
        binding.btnSubmit2.setOnClickListener {
            showDateTimePicker()
        }

        // 출발지 주소 찾기
        binding.btnFindStartAddress.setOnClickListener {
            selectedAddressTarget = binding.editStart
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            addressLauncher.launch(intent)
        }

        // 도착지 주소 찾기
        binding.btnFindEndAddress.setOnClickListener {
            selectedAddressTarget = binding.editEnd
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            addressLauncher.launch(intent)
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                calendar.set(year, month, day, hour, minute)
                selectedDateTime = calendar
                saveScheduledRequest()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun saveScheduledRequest() {
        val start = binding.editStart.text.toString()
        val end = binding.editEnd.text.toString()
        val memo = binding.editMemo.text.toString()
        val memoStart = binding.editMemoStart.text.toString()

        if (start.isBlank() || end.isBlank() || selectedDateTime == null) {
            Toast.makeText(context, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val data = mapOf(
            "email" to MyApplication.email,
            "start" to start,
            "end" to end,
            "memo" to memo,
            "memoStart" to memoStart,
            //"date" to SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(selectedDateTime!!.time)
            //"date" to FieldValue.serverTimestamp()
            "date" to Date()
            )

        MyApplication.db.collection("requests")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(context, "예약 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener {
                Toast.makeText(context, "예약 신청에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }


    private fun clearForm() {
        binding.editStart.setText("")
        binding.editEnd.setText("")
        binding.editMemo.setText("")
        binding.editMemoStart.setText("")
    }
}
