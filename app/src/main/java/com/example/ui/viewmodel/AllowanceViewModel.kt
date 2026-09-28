package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AllowanceCycle
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.model.PlannedPurchase
import com.example.data.repository.AllowanceRepository
import com.example.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

class AllowanceViewModel(
    private val repository: AllowanceRepository,
    initialHighContrast: Boolean = false,
    private val onThemeChanged: ((Boolean) -> Unit)? = null
) : ViewModel() {

    private val _isHighContrast = MutableStateFlow(initialHighContrast)
    val isHighContrast: StateFlow<Boolean> = _isHighContrast.asStateFlow()

    private val _dialogState = MutableStateFlow(
        DialogState(
            isSetupOpen = false,
            isAddExpenseOpen = false,
            expenseToEdit = null
        )
    )

    data class DialogState(
        val isSetupOpen: Boolean,
        val isAddExpenseOpen: Boolean,
        val expenseToEdit: Expense?
    )

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    val uiState: StateFlow<AllowanceUiState> = combine(
        repository.allowanceCycle,
        repository.allExpenses,
        repository.allPlannedPurchases,
        _dialogState,
        _isHighContrast
    ) { cycle, expenses, planned, dialog, highContrast ->
        computeState(cycle, expenses, planned, dialog, highContrast)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AllowanceUiState(isLoading = true, isHighContrast = initialHighContrast)
    )

    private fun computeState(
        cycle: AllowanceCycle?,
        expenses: List<Expense>,
        planned: List<PlannedPurchase>,
        dialog: DialogState,
        highContrast: Boolean
    ): AllowanceUiState {
        if (cycle == null) {
            return AllowanceUiState(
                cycle = null,
                isLoading = false,
                isSetupDialogOpen = dialog.isSetupOpen,
                isAddExpenseDialogOpen = dialog.isAddExpenseOpen,
                expenseToEdit = dialog.expenseToEdit,
                isHighContrast = highContrast
            )
        }

        val totalAllowance = cycle.totalAllowance
        val startDate = cycle.startDate
        val endDate = cycle.endDate
        val currencySymbol = cycle.currencySymbol

        val totalDays = DateUtils.daysInclusive(startDate, endDate)
        val daysRemaining = DateUtils.daysRemainingFromToday(endDate)
        val daysPassed = DateUtils.daysPassedFromStart(startDate, totalDays)

        // Filter expenses for this cycle window or all if dates match
        val cycleExpenses = expenses.filter { exp ->
            exp.date >= startDate && exp.date <= endDate
        }
        val totalSpent = cycleExpenses.sumOf { it.amount }
        val remainingBalance = totalAllowance - totalSpent

        // Ideal remaining balance based on time passed:
        // E.g., if 30 days total and 10 days remaining, ideal remaining balance = totalAllowance * (10 / 30)
        val idealRemainingBalance = if (totalDays > 0) {
            totalAllowance * (daysRemaining.toDouble() / totalDays.toDouble())
        } else {
            0.0
        }

        // Recommended daily spending based on remaining balance and remaining days
        val effectiveDaysForBudget = max(1, daysRemaining)
        val recommendedDailyBudget = max(0.0, remainingBalance) / effectiveDaysForBudget

        // Pacing Status
        val pacingStatus = when {
            remainingBalance < 0 -> BudgetPacingStatus.DEFICIT
            remainingBalance >= idealRemainingBalance * 1.05 -> BudgetPacingStatus.AHEAD_OF_BUDGET
            remainingBalance <= idealRemainingBalance * 0.90 -> BudgetPacingStatus.SPENDING_FAST
            else -> BudgetPacingStatus.ON_TRACK
        }

        val spentPercentage = if (totalAllowance > 0) {
            (totalSpent / totalAllowance).toFloat().coerceIn(0f, 1.5f)
        } else 0f

        val timePassedPercentage = if (totalDays > 0) {
            (daysPassed.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)
        } else 0f

        // Category breakdown
        val categoryTotals = cycleExpenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
        val categoryBreakdown = categoryTotals.map { (catName, sum) ->
            CategorySpend(
                categoryName = catName,
                amount = sum,
                percentage = if (totalSpent > 0) (sum / totalSpent).toFloat() else 0f
            )
        }.sortedByDescending { it.amount }

        // Spending Forecast / "When Will My Money Run Out?"
        // Burn rate is based on days elapsed so far in the period
        val daysElapsed = max(1, daysPassed)
        val dailyBurnRate = if (totalSpent > 0 && daysElapsed > 0) {
            totalSpent / daysElapsed.toDouble()
        } else {
            0.0
        }

        var daysOfMoneyLeft = 0
        var estimatedRunoutDateIso = ""
        var estimatedRunoutDateDisplay = ""
        var willRunOutEarly = false
        var runoutDaysDiff = 0
        var projectedSurplusOrDeficit = 0.0

        if (dailyBurnRate > 0.0) {
            val rawDays = remainingBalance / dailyBurnRate
            daysOfMoneyLeft = if (rawDays > 0) ceil(rawDays).toInt() else 0
            val todayStr = DateUtils.getTodayString()
            estimatedRunoutDateIso = DateUtils.addDaysToIso(todayStr, daysOfMoneyLeft)
            estimatedRunoutDateDisplay = DateUtils.formatMonthDay(estimatedRunoutDateIso)

            willRunOutEarly = daysOfMoneyLeft < daysRemaining
            runoutDaysDiff = daysRemaining - daysOfMoneyLeft

            // Projected at end of cycle: remaining balance - (burnRate * daysRemaining)
            projectedSurplusOrDeficit = remainingBalance - (dailyBurnRate * daysRemaining)
        }

        return AllowanceUiState(
            cycle = cycle,
            expenses = cycleExpenses,
            plannedPurchases = planned,
            isLoading = false,
            totalAllowance = totalAllowance,
            totalSpent = totalSpent,
            remainingBalance = remainingBalance,
            totalDays = totalDays,
            daysRemaining = daysRemaining,
            daysPassed = daysPassed,
            idealRemainingBalance = idealRemainingBalance,
            recommendedDailyBudget = recommendedDailyBudget,
            pacingStatus = pacingStatus,
            spentPercentage = spentPercentage,
            timePassedPercentage = timePassedPercentage,
            categoryBreakdown = categoryBreakdown,
            dailyBurnRate = dailyBurnRate,
            daysOfMoneyLeft = daysOfMoneyLeft,
            estimatedRunoutDateIso = estimatedRunoutDateIso,
            estimatedRunoutDateDisplay = estimatedRunoutDateDisplay,
            willRunOutBeforeCycleEnd = willRunOutEarly,
            runoutDaysDiff = runoutDaysDiff,
            projectedSurplusOrDeficit = projectedSurplusOrDeficit,
            isSetupDialogOpen = dialog.isSetupOpen,
            isAddExpenseDialogOpen = dialog.isAddExpenseOpen,
            expenseToEdit = dialog.expenseToEdit,
            isHighContrast = highContrast
        )
    }

    fun toggleHighContrast() {
        val newValue = !_isHighContrast.value
        _isHighContrast.value = newValue
        onThemeChanged?.invoke(newValue)
    }

    fun setHighContrast(enabled: Boolean) {
        _isHighContrast.value = enabled
        onThemeChanged?.invoke(enabled)
    }

    fun openSetupDialog() {
        _dialogState.value = _dialogState.value.copy(isSetupOpen = true)
    }

    fun closeSetupDialog() {
        _dialogState.value = _dialogState.value.copy(isSetupOpen = false)
    }

    fun openAddExpenseDialog(expense: Expense? = null) {
        _dialogState.value = _dialogState.value.copy(
            isAddExpenseOpen = true,
            expenseToEdit = expense
        )
    }

    fun closeAddExpenseDialog() {
        _dialogState.value = _dialogState.value.copy(
            isAddExpenseOpen = false,
            expenseToEdit = null
        )
    }

    fun saveAllowance(
        amount: Double,
        startDate: String,
        endDate: String,
        currencySymbol: String = "₱"
    ) {
        viewModelScope.launch {
            val cycle = AllowanceCycle(
                id = 1,
                totalAllowance = amount,
                startDate = startDate,
                endDate = endDate,
                currencySymbol = currencySymbol
            )
            repository.saveAllowanceCycle(cycle)
            closeSetupDialog()
        }
    }

    fun saveExpense(
        amount: Double,
        category: String,
        date: String,
        description: String,
        idToUpdate: Long? = null
    ) {
        viewModelScope.launch {
            if (idToUpdate != null && idToUpdate > 0) {
                repository.updateExpense(
                    Expense(
                        id = idToUpdate,
                        amount = amount,
                        category = category,
                        date = date,
                        description = description
                    )
                )
            } else {
                repository.addExpense(
                    Expense(
                        amount = amount,
                        category = category,
                        date = date,
                        description = description
                    )
                )
            }
            closeAddExpenseDialog()
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun addPlannedPurchase(name: String, amount: Double, category: String, notes: String) {
        viewModelScope.launch {
            repository.addPlannedPurchase(
                PlannedPurchase(
                    name = name,
                    amount = amount,
                    category = category,
                    notes = notes
                )
            )
        }
    }

    fun deletePlannedPurchase(purchase: PlannedPurchase) {
        viewModelScope.launch {
            repository.deletePlannedPurchase(purchase)
        }
    }

    fun convertPlannedToExpense(purchase: PlannedPurchase) {
        viewModelScope.launch {
            repository.addExpense(
                Expense(
                    amount = purchase.amount,
                    category = purchase.category,
                    date = DateUtils.getTodayString(),
                    description = purchase.name
                )
            )
            repository.deletePlannedPurchase(purchase)
        }
    }

    companion object {
        fun provideFactory(
            repository: AllowanceRepository,
            initialHighContrast: Boolean = false,
            onThemeChanged: ((Boolean) -> Unit)? = null
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AllowanceViewModel(repository, initialHighContrast, onThemeChanged) as T
                }
            }
    }
}
