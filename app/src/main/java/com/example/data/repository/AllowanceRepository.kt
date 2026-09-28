package com.example.data.repository

import com.example.data.local.AllowanceDao
import com.example.data.local.ExpenseDao
import com.example.data.local.PlannedPurchaseDao
import com.example.data.model.AllowanceCycle
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.model.PlannedPurchase
import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow

class AllowanceRepository(
    private val allowanceDao: AllowanceDao,
    private val expenseDao: ExpenseDao,
    private val plannedPurchaseDao: PlannedPurchaseDao
) {
    val allowanceCycle: Flow<AllowanceCycle?> = allowanceDao.getAllowanceCycle()
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val allPlannedPurchases: Flow<List<PlannedPurchase>> = plannedPurchaseDao.getAllPlanned()

    suspend fun saveAllowanceCycle(cycle: AllowanceCycle) {
        allowanceDao.insertOrUpdate(cycle)
    }

    suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteExpenseById(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun addPlannedPurchase(purchase: PlannedPurchase): Long {
        return plannedPurchaseDao.insert(purchase)
    }

    suspend fun updatePlannedPurchase(purchase: PlannedPurchase) {
        plannedPurchaseDao.update(purchase)
    }

    suspend fun deletePlannedPurchase(purchase: PlannedPurchase) {
        plannedPurchaseDao.delete(purchase)
    }

    suspend fun seedInitialDataIfNeeded() {
        val existing = allowanceDao.getAllowanceCycleSync()
        if (existing == null) {
            val today = DateUtils.getTodayString()
            // Set cycle to start 5 days ago and end 25 days from today (30-day student budget)
            val start = DateUtils.addDaysToIso(today, -5)
            val end = DateUtils.addDaysToIso(today, 24)

            val initialCycle = AllowanceCycle(
                id = 1,
                totalAllowance = 3000.0,
                startDate = start,
                endDate = end,
                currencySymbol = "₱"
            )
            allowanceDao.insertOrUpdate(initialCycle)

            // Seed realistic student expenses over the past few days
            val seedExpenses = listOf(
                Expense(
                    amount = 120.0,
                    category = ExpenseCategory.FOOD.title,
                    date = DateUtils.addDaysToIso(today, -4),
                    description = "Canteen lunch & milk tea"
                ),
                Expense(
                    amount = 50.0,
                    category = ExpenseCategory.TRANSPORTATION.title,
                    date = DateUtils.addDaysToIso(today, -3),
                    description = "Commute jeepney / tricycle fare"
                ),
                Expense(
                    amount = 180.0,
                    category = ExpenseCategory.SCHOOL.title,
                    date = DateUtils.addDaysToIso(today, -2),
                    description = "Binder notebook & exam bluebook"
                ),
                Expense(
                    amount = 150.0,
                    category = ExpenseCategory.FOOD.title,
                    date = DateUtils.addDaysToIso(today, -1),
                    description = "After-school group snack"
                ),
                Expense(
                    amount = 65.0,
                    category = ExpenseCategory.TRANSPORTATION.title,
                    date = today,
                    description = "Bus fare to campus"
                )
            )
            expenseDao.insertAll(seedExpenses)

            // Seed a sample planned purchase in the simulator/wishlist
            plannedPurchaseDao.insert(
                PlannedPurchase(
                    name = "Scientific Calculator",
                    amount = 450.0,
                    category = ExpenseCategory.SCHOOL.title,
                    notes = "For Physics class midterms"
                )
            )
        }
    }
}
