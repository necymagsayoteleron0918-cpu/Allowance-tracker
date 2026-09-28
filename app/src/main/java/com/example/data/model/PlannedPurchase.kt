package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planned_purchases")
data class PlannedPurchase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val category: String = ExpenseCategory.OTHER.title,
    val notes: String = "",
    val isPurchased: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
