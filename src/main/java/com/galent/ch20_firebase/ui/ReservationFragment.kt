package com.galent.ch20_firebase.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.galent.ch20_firebase.databinding.FragmentReservationBinding
import com.galent.ch20_firebase.room.AppDatabase
import com.galent.ch20_firebase.room.RegularReservation
import com.galent.ch20_firebase.room.ReservationAdapter
import kotlinx.coroutines.*

class ReservationFragment : Fragment() {
    lateinit var binding: FragmentReservationBinding
    private lateinit var adapter: ReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val db = AppDatabase.getInstance(requireContext())
        val dao = db.reservationDao()

        ArrayAdapter.createFromResource(
            requireContext(),
            com.galent.ch20_firebase.R.array.days_of_week,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDay.adapter = adapter
        }

        binding.btnDeleteAll.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("전체 삭제")
                .setMessage("모든 정기 예약을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.deleteAll()  // 예약 전체 삭제 DAO 메서드 호출
                        withContext(Dispatchers.Main) {
                            adapter.clearAll()  // 어댑터 리스트 비우기
                            Toast.makeText(requireContext(), "모든 예약이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }


        binding.btnSave.setOnClickListener {
            val start = binding.editStart.text.toString()
            val end = binding.editEnd.text.toString()
            val day = binding.spinnerDay.selectedItem.toString()

            if (start.isBlank() || end.isBlank()) {
                Toast.makeText(context, "출발지와 도착지를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                dao.insert(RegularReservation(start = start, end = end, dayOfWeek = day))
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "정기 예약 등록 완료", Toast.LENGTH_SHORT).show()
                    binding.editStart.setText("")
                    binding.editEnd.setText("")
                    updateList()
                }
            }
        }

        updateList()
    }

    private fun updateList() {
        val dao = AppDatabase.getInstance(requireContext()).reservationDao()

        CoroutineScope(Dispatchers.IO).launch {
            val list = dao.getAll()
            withContext(Dispatchers.Main) {
                adapter = ReservationAdapter(list.toMutableList())
                binding.reservationRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext())
                binding.reservationRecyclerView.adapter = adapter
            }
        }
    }
}
