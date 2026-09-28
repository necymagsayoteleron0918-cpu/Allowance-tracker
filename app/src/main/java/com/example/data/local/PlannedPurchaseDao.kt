package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PlannedPurchase
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedPurchaseDao {
    @Query("SELECT * FROM planned_purchases ORDER BY timestamp DESC")
    fun getAllPlanned(): Flow<List<PlannedPurchase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: PlannedPurchase): Long

    @Update
    suspend fun update(purchase: PlannedPurchase)

    @Delete
    suspend fun delete(purchase: PlannedPurchase)

    @Query("DELETE FROM planned_purchases WHERE id = :id")
    suspend fun deleteById(id: Long)
}
