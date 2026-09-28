package com.example

import com.example.data.local.AllowanceDao
import com.example.data.local.ExpenseDao
import com.example.data.local.PlannedPurchaseDao
import com.example.data.model.AllowanceCycle
import com.example.data.model.Expense
import com.example.data.model.PlannedPurchase
import com.example.data.repository.AllowanceRepository
import com.example.ui.viewmodel.AllowanceViewModel
import com.example.util.DateUtils
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max

class AllowanceCalculationsTest {

    @Test
    fun testCanIAffordThis_mathMatchesUserExample() {
        val currentBalance = 2000.0
        val plannedPurchase = 500.0
        val daysRemaining = 10

        val balanceAfter = currentBalance - plannedPurchase
        assertEquals(1500.0, balanceAfter, 0.001)

        val newDailyBudget = if (daysRemaining > 0) balanceAfter / daysRemaining else 0.0
        assertEquals(150.0, newDailyBudget, 0.001)
    }

    @Test
    fun testIdealRemainingBalanceCalculation() {
        val totalAllowance = 3000.0
        val totalDays = 30
        val daysRemaining = 20

        val idealRemaining = totalAllowance * (daysRemaining.toDouble() / totalDays.toDouble())
        assertEquals(2000.0, idealRemaining, 0.001)
    }

    @Test
    fun testRunoutForecastBurnRate() {
        val totalSpent = 900.0
        val daysElapsed = 5
        val burnRate = totalSpent / daysElapsed
        assertEquals(180.0, burnRate, 0.001)

        val remainingBalance = 1800.0
        val daysOfMoneyLeft = (remainingBalance / burnRate).toInt()
        assertEquals(10, daysOfMoneyLeft)
    }

    @Test
    fun testThemeToggle_switchesBetweenSkyPinkAndHighContrast() {
        val dummyAllowanceDao = object : AllowanceDao {
            override fun getAllowanceCycle() = flowOf(null)
            override suspend fun getAllowanceCycleSync() = null
            override suspend fun insertOrUpdate(cycle: AllowanceCycle) {}
            override suspend fun clear() {}
        }
        val dummyExpenseDao = object : ExpenseDao {
            override fun getAllExpenses() = flowOf(emptyList<Expense>())
            override fun getExpensesInRange(startDate: String, endDate: String) = flowOf(emptyList<Expense>())
            override fun getTotalSpentInRange(startDate: String, endDate: String) = flowOf(null)
            override suspend fun insertExpense(expense: Expense) = 0L
            override suspend fun insertAll(expenses: List<Expense>) {}
            override suspend fun updateExpense(expense: Expense) {}
            override suspend fun deleteExpense(expense: Expense) {}
            override suspend fun deleteExpenseById(id: Long) {}
            override suspend fun clearAll() {}
        }
        val dummyPlannedDao = object : PlannedPurchaseDao {
            override fun getAllPlanned() = flowOf(emptyList<PlannedPurchase>())
            override suspend fun insert(purchase: PlannedPurchase) = 0L
            override suspend fun update(purchase: PlannedPurchase) {}
            override suspend fun delete(purchase: PlannedPurchase) {}
            override suspend fun deleteById(id: Long) {}
        }
        val repository = AllowanceRepository(dummyAllowanceDao, dummyExpenseDao, dummyPlannedDao)

        var persistedState: Boolean? = null
        val viewModel = AllowanceViewModel(
            repository = repository,
            initialHighContrast = false,
            onThemeChanged = { persistedState = it }
        )

        assertFalse(viewModel.isHighContrast.value)

        // Toggle to High-Contrast
        viewModel.toggleHighContrast()
        assertTrue(viewModel.isHighContrast.value)
        assertEquals(true, persistedState)

        // Toggle back to Sky Blue & Pink
        viewModel.toggleHighContrast()
        assertFalse(viewModel.isHighContrast.value)
        assertEquals(false, persistedState)
    }
}
