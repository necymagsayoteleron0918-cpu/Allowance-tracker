package com.example.ui.screens.afford

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.data.model.ExpenseCategory
import com.example.data.model.PlannedPurchase
import com.example.ui.components.CategoryIcon
import com.example.ui.components.CategoryPill
import com.example.ui.theme.LocalHighContrastMode
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.viewmodel.AllowanceUiState
import com.example.util.DateUtils
import kotlin.math.max

@Composable
fun CanIAffordThisScreen(
    uiState: AllowanceUiState,
    onSaveToWishlist: (name: String, amount: Double, category: String, notes: String) -> Unit,
    onCommitToExpense: (PlannedPurchase) -> Unit,
    onDirectBuy: (name: String, amount: Double, category: String) -> Unit,
    onDeletePlanned: (PlannedPurchase) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = uiState.cycle?.currencySymbol ?: "₱"
    val daysRemaining = max(1, uiState.daysRemaining)
    val isHighContrast = LocalHighContrastMode.current
    val cardBorder = if (isHighContrast) BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline) else null

    var priceInput by remember { mutableStateOf("") }
    var itemNameInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.PERSONAL.title) }

    val plannedPrice = priceInput.toDoubleOrNull() ?: 0.0
    val currentBalance = uiState.remainingBalance
    val balanceAfter = currentBalance - plannedPrice
    val currentDailyBudget = uiState.recommendedDailyBudget
    val newDailyBudget = if (daysRemaining > 0) max(0.0, balanceAfter) / daysRemaining else 0.0

    val isInputValid = plannedPrice > 0.0

    // Affordability Verdict Assessment
    val (verdictTitle, verdictMsg, verdictColor, verdictIcon) = when {
        plannedPrice <= 0.0 -> Quadruple(
            "Enter an item price",
            "See how this purchase impacts your daily student budget.",
            MaterialTheme.colorScheme.primary,
            Icons.Default.Lightbulb
        )
        plannedPrice > currentBalance -> Quadruple(
            "⚠️ Over Budget / Cannot Afford",
            "This item exceeds your current balance by ${DateUtils.formatMoney(plannedPrice - currentBalance, currency)}. You'll be in deficit!",
            StatusRed,
            Icons.Default.ErrorOutline
        )
        newDailyBudget < (currentDailyBudget * 0.4) -> Quadruple(
            "⚡ High Budget Squeeze",
            "This severely shrinks your daily budget. You'll only have ${DateUtils.formatMoney(newDailyBudget, currency)}/day to survive on!",
            StatusAmber,
            Icons.Default.Warning
        )
        newDailyBudget < (currentDailyBudget * 0.75) -> Quadruple(
            "💡 Feasible with Minor Caution",
            "Manageable, but you'll need to trim down non-essential spending for the next $daysRemaining days.",
            StatusAmber,
            Icons.Default.Lightbulb
        )
        else -> Quadruple(
            "✅ Safe & Affordable",
            "Minimal impact on your daily budget. You can comfortably afford this!",
            StatusGreen,
            Icons.Default.CheckCircle
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("can_i_afford_this_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feature Header Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = cardBorder,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "“Can I Afford This?”",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Test purchases before spending to protect your daily allowance.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Input Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(),
                border = cardBorder,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "What do you want to buy?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Price Input (Highlighted)
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Price of Planned Purchase") },
                        prefix = { Text("$currency ", fontWeight = FontWeight.Bold) },
                        placeholder = { Text("e.g. 500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planned_price_input")
                    )

                    // Optional Item Name
                    OutlinedTextField(
                        value = itemNameInput,
                        onValueChange = { itemNameInput = it },
                        label = { Text("Item Name / Goal (Optional)") },
                        placeholder = { Text("e.g. New Shoes, Gaming Headset, Milk Tea") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planned_name_input")
                    )

                    // Category chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ExpenseCategory.entries.forEach { cat ->
                            val isSelected = selectedCategory.equals(cat.title, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat.title },
                                label = { Text("${cat.emoji} ${cat.title}", fontSize = 12.sp) }
                            )
                        }
                    }

                    // Quick example prices
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf(100, 250, 500, 1000).forEach { amount ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { priceInput = amount.toString() }
                            ) {
                                Text(
                                    text = "$currency$amount",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Simulation Results
        if (isInputValid) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.elevatedCardElevation(),
                    border = cardBorder,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Verdict Banner
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = verdictColor.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = verdictIcon,
                                    contentDescription = null,
                                    tint = verdictColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = verdictTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = verdictColor
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = verdictMsg,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        // Prominent Result Quotes (Explicitly required by user prompt)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "💬", fontSize = 16.sp)
                                    Text(
                                        text = "What happens if you buy this:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Text(
                                    text = "“After this purchase, you will have ${DateUtils.formatMoney(balanceAfter, currency)} left.”",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (balanceAfter < 0) StatusRed else MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "“Your available budget would become ${DateUtils.formatMoney(newDailyBudget, currency)} per day for the next $daysRemaining days.”",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Comparison Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Current Daily
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Current Daily",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${DateUtils.formatMoney(currentDailyBudget, currency)}/d",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // New Daily
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (newDailyBudget < currentDailyBudget * 0.5) {
                                        StatusAmber.copy(alpha = 0.15f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    }
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "New Daily",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${DateUtils.formatMoney(newDailyBudget, currency)}/d",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (balanceAfter < 0) StatusRed else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Action Buttons: "Bought It! Add to Expenses" or "Save to Wishlist"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val name = if (itemNameInput.isNotBlank()) itemNameInput.trim() else "Planned Item"
                                    onSaveToWishlist(name, plannedPrice, selectedCategory, "")
                                    priceInput = ""
                                    itemNameInput = ""
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_to_wishlist_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save to List", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val name = if (itemNameInput.isNotBlank()) itemNameInput.trim() else "Purchased Item"
                                    onDirectBuy(name, plannedPrice, selectedCategory)
                                    priceInput = ""
                                    itemNameInput = ""
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bought_it_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bought It!", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Saved Planned Purchases / Wishlist Section
        item {
            Text(
                text = "Planned Purchases & Wishlist (${uiState.plannedPurchases.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (uiState.plannedPurchases.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🎯", fontSize = 28.sp)
                        Text(
                            text = "No saved planned purchases yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Enter an item above and tap 'Save to List' to keep track of things you're saving for!",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(uiState.plannedPurchases, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            CategoryIcon(categoryName = item.category, size = 36.dp, iconSize = 18.dp)
                            Column {
                                Text(
                                    text = item.name,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${item.category} • ${DateUtils.formatMoney(item.amount, currency)}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Tap to test affordability
                            FilledTonalButton(
                                onClick = {
                                    priceInput = item.amount.toInt().toString()
                                    itemNameInput = item.name
                                    selectedCategory = item.category
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Check", fontSize = 11.sp)
                            }

                            // Tap to convert to actual expense
                            IconButton(
                                onClick = { onCommitToExpense(item) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Bought item",
                                    tint = StatusGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Delete
                            IconButton(
                                onClick = { onDeletePlanned(item) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
