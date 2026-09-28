package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AllowanceCycle
import kotlinx.coroutines.flow.Flow

@Dao
interface AllowanceDao {
    @Query("SELECT * FROM allowance_cycle WHERE id = 1 LIMIT 1")
    fun getAllowanceCycle(): Flow<AllowanceCycle?>

    @Query("SELECT * FROM allowance_cycle WHERE id = 1 LIMIT 1")
    suspend fun getAllowanceCycleSync(): AllowanceCycle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cycle: AllowanceCycle)

    @Query("DELETE FROM allowance_cycle")
    suspend fun clear()
}
