package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AllowanceCycle
import com.example.data.model.Expense
import com.example.data.model.PlannedPurchase

@Database(
    entities = [AllowanceCycle::class, Expense::class, PlannedPurchase::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun allowanceDao(): AllowanceDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun plannedPurchaseDao(): PlannedPurchaseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "allowance_tracker.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
