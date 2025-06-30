package com.galent.ch20_firebase.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galent.ch20_firebase.databinding.ItemReservationBinding

class ReservationAdapter(val items: MutableList<RegularReservation>) :
    RecyclerView.Adapter<ReservationAdapter.Holder>() {

    inner class Holder(val binding: ItemReservationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.binding.textInfo.text = "${item.dayOfWeek} - ${item.start} → ${item.end}"
    }
    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }

}
