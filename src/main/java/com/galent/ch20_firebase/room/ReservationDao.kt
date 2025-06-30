package com.galent.ch20_firebase.room

import androidx.room.*

@Dao
interface ReservationDao {
    @Insert
    fun insert(reservation: RegularReservation)

    @Query("SELECT * FROM RegularReservation ORDER BY id DESC")
    fun getAll(): List<RegularReservation>

    @Delete
    fun delete(reservation: RegularReservation)

    @Query("DELETE FROM RegularReservation")
    suspend fun deleteAll()


}

