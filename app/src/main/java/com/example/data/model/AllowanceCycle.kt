package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allowance_cycle")
data class AllowanceCycle(
    @PrimaryKey val id: Int = 1,
    val totalAllowance: Double,
    val startDate: String, // ISO YYYY-MM-DD
    val endDate: String,   // ISO YYYY-MM-DD
    val currencySymbol: String = "₱",
    val updatedAt: Long = System.currentTimeMillis()
)
