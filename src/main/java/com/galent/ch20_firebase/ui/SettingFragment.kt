package com.galent.ch20_firebase.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.galent.ch20_firebase.AuthActivity
import com.galent.ch20_firebase.MainActivity
import com.galent.ch20_firebase.databinding.FragmentSettingBinding
import com.galent.ch20_firebase.MyApplication
import com.galent.ch20_firebase.R


class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val pref = requireActivity().getSharedPreferences("setting", Context.MODE_PRIVATE)
        val editor = pref.edit()

        val fontSizes = resources.getStringArray(R.array.font_sizes)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fontSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFontSize.adapter = adapter

        // 저장된 값 불러오기
        binding.switchPush.isChecked = pref.getBoolean("push", false)
        binding.switchDark.isChecked = pref.getBoolean("dark", false)
        binding.editDefaultStart.setText(pref.getString("start", ""))
        binding.spinnerFontSize.setSelection(pref.getInt("fontsize", 1)) // 0=작게, 1=보통, 2=크게

        // 값 변경 저장
        binding.switchPush.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("push", isChecked).apply()
        }

        binding.switchDark.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("dark", isChecked).apply()
            Toast.makeText(context, "다크 모드는 재시작 시 반영됩니다.", Toast.LENGTH_SHORT).show()
        }

        binding.editDefaultStart.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                editor.putString("start", binding.editDefaultStart.text.toString()).apply()
            }
        }

        binding.spinnerFontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                editor.putInt("fontsize", pos).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.settingAccount.setOnClickListener {
            Toast.makeText(context, "계정 관리 기능은 추후 지원됩니다.", Toast.LENGTH_SHORT).show()
        }

        binding.settingPayment.setOnClickListener {
            Toast.makeText(context, "간편결제 설정 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }

        binding.settingLogout.setOnClickListener {
            MyApplication.auth.signOut()
            MyApplication.email = null
            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            //이동 추후 수정 LoginActivity가 아니라 다른 명칭임
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        val fontScale = when (pref.getInt("fontsize", 1)) {
            0 -> 0.85f  // 작게
            1 -> 1.0f   // 보통
            2 -> 1.15f  // 크게
            else -> 1.0f
        }
        resources.configuration.fontScale = fontScale
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)

        val isDarkMode = pref.getBoolean("dark", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        binding.spinnerFontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                val prev = pref.getInt("fontsize", 1)
                if (pos != prev) {
                    editor.putInt("fontsize", pos).apply()
                    Toast.makeText(context, "글자 크기가 변경되었습니다. 앱을 다시 시작해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 저장된 보호자 정보 불러오기
        binding.editGuardianName.setText(pref.getString("guardian_name", ""))
        binding.editGuardianPhone.setText(pref.getString("guardian_phone", ""))

        // 포커스 잃었을 때 저장
        binding.editGuardianName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                editor.putString("guardian_name", binding.editGuardianName.text.toString()).apply()
            }
        }
        binding.editGuardianPhone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                editor.putString("guardian_phone", binding.editGuardianPhone.text.toString()).apply()
            }
        }




    }
}
