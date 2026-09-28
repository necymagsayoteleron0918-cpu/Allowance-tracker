package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.afford.CanIAffordThisScreen
import com.example.ui.screens.dashboard.AllowanceSetupDialog
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.expenses.AddExpenseDialog
import com.example.ui.screens.expenses.ExpensesScreen
import com.example.ui.screens.forecast.SpendingForecastScreen
import com.example.ui.viewmodel.AllowanceViewModel

enum class AppScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "tab_dashboard"),
    EXPENSES("Expenses", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "tab_expenses"),
    AFFORD("Can I Afford?", Icons.Filled.Psychology, Icons.Outlined.Psychology, "tab_afford"),
    FORECAST("Forecast", Icons.Filled.ShowChart, Icons.Outlined.ShowChart, "tab_forecast")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllowanceApp(viewModel: AllowanceViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

    // Handle back button on sub-screens
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        currentScreen = AppScreen.DASHBOARD
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentScreen) {
                            AppScreen.DASHBOARD -> "Allowance Tracker"
                            AppScreen.EXPENSES -> "Daily Expenses"
                            AppScreen.AFFORD -> "Can I Afford This?"
                            AppScreen.FORECAST -> "Spending Forecast"
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    FilledTonalButton(
                        onClick = { viewModel.toggleHighContrast() },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Contrast,
                            contentDescription = if (uiState.isHighContrast) "Switch to Sky Blue & Pink Theme" else "Switch to High-Contrast Theme",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isHighContrast) "High Contrast" else "Sky & Pink",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                AppScreen.entries.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title, maxLines = 1) },
                        modifier = Modifier.testTag(screen.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> {
                        DashboardScreen(
                            uiState = uiState,
                            onOpenSetup = { viewModel.openSetupDialog() },
                            onAddExpense = { viewModel.openAddExpenseDialog() },
                            onNavigateToExpenses = { currentScreen = AppScreen.EXPENSES },
                            onNavigateToAfford = { currentScreen = AppScreen.AFFORD },
                            onNavigateToForecast = { currentScreen = AppScreen.FORECAST },
                            onDeleteExpense = { viewModel.deleteExpense(it) },
                            onToggleTheme = { viewModel.toggleHighContrast() }
                        )
                    }
                    AppScreen.EXPENSES -> {
                        ExpensesScreen(
                            uiState = uiState,
                            onAddExpense = { viewModel.openAddExpenseDialog() },
                            onEditExpense = { viewModel.openAddExpenseDialog(it) },
                            onDeleteExpense = { viewModel.deleteExpense(it) }
                        )
                    }
                    AppScreen.AFFORD -> {
                        CanIAffordThisScreen(
                            uiState = uiState,
                            onSaveToWishlist = { name, amount, cat, notes ->
                                viewModel.addPlannedPurchase(name, amount, cat, notes)
                            },
                            onCommitToExpense = { item ->
                                viewModel.convertPlannedToExpense(item)
                            },
                            onDirectBuy = { name, amount, cat ->
                                viewModel.saveExpense(
                                    amount = amount,
                                    category = cat,
                                    date = com.example.util.DateUtils.getTodayString(),
                                    description = name
                                )
                            },
                            onDeletePlanned = { viewModel.deletePlannedPurchase(it) }
                        )
                    }
                    AppScreen.FORECAST -> {
                        SpendingForecastScreen(
                            uiState = uiState
                        )
                    }
                }
            }
        }
    }

    // Allowance Setup Dialog
    if (uiState.isSetupDialogOpen) {
        AllowanceSetupDialog(
            initialCycle = uiState.cycle,
            onDismiss = { viewModel.closeSetupDialog() },
            onSave = { amount, start, end, symbol ->
                viewModel.saveAllowance(amount, start, end, symbol)
            }
        )
    }

    // Add or Edit Expense Dialog
    if (uiState.isAddExpenseDialogOpen) {
        AddExpenseDialog(
            currencySymbol = uiState.cycle?.currencySymbol ?: "₱",
            expenseToEdit = uiState.expenseToEdit,
            onDismiss = { viewModel.closeAddExpenseDialog() },
            onSave = { amount, category, date, description, idToUpdate ->
                viewModel.saveExpense(amount, category, date, description, idToUpdate)
            }
        )
    }
}
