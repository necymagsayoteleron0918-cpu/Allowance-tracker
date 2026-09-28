package com.example.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.AllowanceCycle
import com.example.util.DateUtils

@Composable
fun AllowanceSetupDialog(
    initialCycle: AllowanceCycle?,
    onDismiss: () -> Unit,
    onSave: (amount: Double, startDate: String, endDate: String, currencySymbol: String) -> Unit
) {
    val today = DateUtils.getTodayString()
    var amountText by remember {
        mutableStateOf(initialCycle?.totalAllowance?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "3000")
    }
    var startDateText by remember {
        mutableStateOf(initialCycle?.startDate ?: today)
    }
    var selectedPeriodDays by remember {
        val currentDays = if (initialCycle != null) {
            DateUtils.daysInclusive(initialCycle.startDate, initialCycle.endDate)
        } else 30
        mutableIntStateOf(currentDays)
    }
    var customDaysText by remember {
        mutableStateOf(selectedPeriodDays.toString())
    }
    var currencySymbol by remember {
        mutableStateOf(initialCycle?.currencySymbol ?: "₱")
    }

    val calculatedEndDate = remember(startDateText, selectedPeriodDays) {
        DateUtils.addDaysToIso(startDateText, (selectedPeriodDays - 1).coerceAtLeast(0))
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (initialCycle != null) "Edit Allowance Setup" else "Setup Your Allowance",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Track your budget, know your daily limits, and make your allowance last!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Currency selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Currency Symbol",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("₱", "$", "€", "£").forEach { sym ->
                            FilterChip(
                                selected = currencySymbol == sym,
                                onClick = { currencySymbol = sym },
                                label = { Text(sym, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("currency_chip_$sym")
                            )
                        }
                    }
                }

                // Allowance Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorMessage = null
                    },
                    label = { Text("Total Allowance Amount") },
                    prefix = { Text("$currencySymbol ", fontWeight = FontWeight.Bold) },
                    placeholder = { Text("e.g. 2000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("allowance_amount_input")
                )

                // Start Date Input
                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    label = { Text("Start Date (YYYY-MM-DD)") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("allowance_start_date_input")
                )

                // Period preset buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Allowance Duration",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(7 to "1 Week", 15 to "15 Days", 30 to "1 Month").forEach { (days, label) ->
                            FilterChip(
                                selected = selectedPeriodDays == days,
                                onClick = {
                                    selectedPeriodDays = days
                                    customDaysText = days.toString()
                                },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Custom Days
                    OutlinedTextField(
                        value = customDaysText,
                        onValueChange = {
                            customDaysText = it
                            val parsed = it.toIntOrNull()
                            if (parsed != null && parsed > 0) {
                                selectedPeriodDays = parsed
                            }
                        },
                        label = { Text("Or Enter Number of Days") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Calculated Cycle Summary
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Period Preview",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${DateUtils.formatForDisplay(startDateText)}  ➔  ${DateUtils.formatForDisplay(calculatedEndDate)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Total: $selectedPeriodDays days",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

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
                        errorMessage = "Please enter a valid allowance amount."
                        return@Button
                    }
                    if (selectedPeriodDays <= 0) {
                        errorMessage = "Please enter a valid number of days."
                        return@Button
                    }
                    onSave(amount, startDateText, calculatedEndDate, currencySymbol)
                },
                modifier = Modifier.testTag("save_allowance_btn")
            ) {
                Text("Save Allowance")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_allowance_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
