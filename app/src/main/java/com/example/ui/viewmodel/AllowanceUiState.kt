package com.example.ui.viewmodel

import com.example.data.model.AllowanceCycle
import com.example.data.model.Expense
import com.example.data.model.PlannedPurchase

enum class BudgetPacingStatus {
    AHEAD_OF_BUDGET,
    ON_TRACK,
    SPENDING_FAST,
    DEFICIT
}

enum class AffordabilityVerdict {
    AFFORDABLE,
    MODERATE,
    CAUTION,
    CANNOT_AFFORD
}

data class CategorySpend(
    val categoryName: String,
    val amount: Double,
    val percentage: Float
)

data class AllowanceUiState(
    val cycle: AllowanceCycle? = null,
    val expenses: List<Expense> = emptyList(),
    val plannedPurchases: List<PlannedPurchase> = emptyList(),
    val isLoading: Boolean = true,

    // Dashboard computed metrics
    val totalAllowance: Double = 0.0,
    val totalSpent: Double = 0.0,
    val remainingBalance: Double = 0.0,
    val totalDays: Int = 1,
    val daysRemaining: Int = 0,
    val daysPassed: Int = 0,
    val idealRemainingBalance: Double = 0.0,
    val recommendedDailyBudget: Double = 0.0,
    val pacingStatus: BudgetPacingStatus = BudgetPacingStatus.ON_TRACK,
    val spentPercentage: Float = 0f,
    val timePassedPercentage: Float = 0f,

    // Category breakdown
    val categoryBreakdown: List<CategorySpend> = emptyList(),

    // Spending Forecast
    val dailyBurnRate: Double = 0.0,
    val daysOfMoneyLeft: Int = 0,
    val estimatedRunoutDateIso: String = "",
    val estimatedRunoutDateDisplay: String = "",
    val willRunOutBeforeCycleEnd: Boolean = false,
    val runoutDaysDiff: Int = 0, // positive if runs out early, negative if surplus days
    val projectedSurplusOrDeficit: Double = 0.0,

    // Dialog & Navigation states
    val isSetupDialogOpen: Boolean = false,
    val isAddExpenseDialogOpen: Boolean = false,
    val expenseToEdit: Expense? = null,

    // Theme mode state (Sky Blue & Pink vs High-Contrast Light Mode)
    val isHighContrast: Boolean = false
)
