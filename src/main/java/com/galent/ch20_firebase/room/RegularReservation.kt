package com.galent.ch20_firebase.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RegularReservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val start: String,
    val end: String,
    val dayOfWeek: String
)
