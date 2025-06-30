package com.galent.ch20_firebase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.galent.ch20_firebase.ItemData
import com.galent.ch20_firebase.MyAdapter
import com.galent.ch20_firebase.MyApplication
import com.galent.ch20_firebase.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {
    lateinit var binding: FragmentCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        // Fab 클릭 시 게시글 추가 연결
        binding.communityFab.setOnClickListener {
            if (MyApplication.checkAuth()) {
                val intent = android.content.Intent(requireContext(), com.galent.ch20_firebase.AddActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(context, "로그인 후 작성이 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if (MyApplication.checkAuth() || MyApplication.email != null) {
            binding.communityRecyclerView.visibility = View.VISIBLE

            MyApplication.db.collection("news")
                .get()
                .addOnSuccessListener { result ->
                    val itemList = mutableListOf<ItemData>()
                    for (document in result) {
                        val item = document.toObject(ItemData::class.java)
                        item.docId = document.id
                        itemList.add(item)
                    }
                    binding.communityRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.communityRecyclerView.adapter = MyAdapter(itemList)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Firestore에서 데이터 획득에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "인증된 사용자만 커뮤니티를 볼 수 있습니다.", Toast.LENGTH_SHORT).show()
            binding.communityRecyclerView.visibility = View.GONE
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = binding.youtubeWebView
        webView.settings.javaScriptEnabled = true
        webView.loadData(
            """<iframe width="100%" height="100%" 
                    src="https://www.youtube.com/embed/1OWcrTJ2z6Q" 
                    frameborder="0" allowfullscreen></iframe>""",
                    "text/html", "utf-8"
        )

    }


}

