package com.galent.ch20_firebase

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galent.ch20_firebase.databinding.ItemMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MyViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas: MutableList<ItemData>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(ItemMainBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        val model = datas[position]

        binding.itemEmailView.text = model.email
        binding.itemDateView.text = model.date
        binding.itemContentView.text = model.content

        // 롱클릭 시 삭제 다이얼로그 표시
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("삭제 확인")
                .setMessage("이 게시글을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    val db = FirebaseFirestore.getInstance()
                    db.collection("news").document(model.docId)
                        .delete()
                        .addOnSuccessListener {
                            // 안전하게 adapterPosition 사용
                            val adapterPos = holder.adapterPosition
                            if (adapterPos != RecyclerView.NO_POSITION && adapterPos < datas.size) {
                                datas.removeAt(adapterPos)
                                notifyItemRemoved(adapterPos)
                            }
                        }
                }
                .setNegativeButton("취소", null)
                .show()
            true
        }
    }
}
