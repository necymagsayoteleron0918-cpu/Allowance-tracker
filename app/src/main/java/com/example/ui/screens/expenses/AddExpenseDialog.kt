package com.example.ui.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.util.DateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    currencySymbol: String,
    expenseToEdit: Expense?,
    onDismiss: () -> Unit,
    onSave: (amount: Double, category: String, date: String, description: String, idToUpdate: Long?) -> Unit
) {
    val today = DateUtils.getTodayString()
    val yesterday = DateUtils.addDaysToIso(today, -1)

    var amountText by remember {
        mutableStateOf(expenseToEdit?.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(expenseToEdit?.category ?: ExpenseCategory.FOOD.title)
    }
    var dateText by remember {
        mutableStateOf(expenseToEdit?.date ?: today)
    }
    var descriptionText by remember {
        mutableStateOf(expenseToEdit?.description ?: "")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (expenseToEdit != null) Icons.Default.Edit else Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (expenseToEdit != null) "Edit Expense" else "Log Daily Expense",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Amount input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorMessage = null
                    },
                    label = { Text("Amount") },
                    prefix = { Text("$currencySymbol ", fontWeight = FontWeight.Bold) },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_amount_input")
                )

                // Category Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ExpenseCategory.entries.forEach { cat ->
                            val isSelected = selectedCategory.equals(cat.title, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat.title },
                                label = {
                                    Text(
                                        "${cat.emoji} ${cat.title}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) cat.color else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}")
                            )
                        }
                    }
                }

                // Date selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Date",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = dateText == today,
                            onClick = { dateText = today },
                            label = { Text("Today") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = dateText == yesterday,
                            onClick = { dateText = yesterday },
                            label = { Text("Yesterday") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Custom Date (YYYY-MM-DD)") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_date_input")
                    )
                }

                // Description (Optional)
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("e.g. Canteen lunch, bus fare, bluebook") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_desc_input")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorMessage = "Please enter a valid amount greater than 0."
                        return@Button
                    }
                    onSave(
                        amount,
                        selectedCategory,
                        dateText,
                        descriptionText.trim(),
                        expenseToEdit?.id
                    )
                },
                modifier = Modifier.testTag("save_expense_btn")
            ) {
                Text(if (expenseToEdit != null) "Update Expense" else "Add Expense")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_expense_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
