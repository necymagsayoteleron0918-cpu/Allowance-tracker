package com.example.ui.screens.forecast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalHighContrastMode
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.viewmodel.AllowanceUiState
import com.example.util.DateUtils
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun SpendingForecastScreen(
    uiState: AllowanceUiState,
    modifier: Modifier = Modifier
) {
    val currency = uiState.cycle?.currencySymbol ?: "₱"
    val cycleEnd = uiState.cycle?.endDate ?: DateUtils.getTodayString()
    val cycleEndDisplay = DateUtils.formatMonthDay(cycleEnd)
    val isHighContrast = LocalHighContrastMode.current
    val cardBorder = if (isHighContrast) BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline) else null

    val currentBurnRate = uiState.dailyBurnRate
    val hasEnoughData = currentBurnRate > 0.0

    // Interactive What-If Simulator Rate
    var simulatedBurnRate by remember(currentBurnRate) {
        mutableFloatStateOf(if (currentBurnRate > 0) currentBurnRate.toFloat() else 150f)
    }

    val simulatedDaysLeft = if (simulatedBurnRate > 0) {
        ceil(uiState.remainingBalance / simulatedBurnRate).toInt().coerceAtLeast(0)
    } else 0
    val simulatedRunoutIso = DateUtils.addDaysToIso(DateUtils.getTodayString(), simulatedDaysLeft)
    val simulatedRunoutDisplay = DateUtils.formatMonthDay(simulatedRunoutIso)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("forecast_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feature Header Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = cardBorder,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
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
                            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "“When Will My Money Run Out?”",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Forecast your runway based on your actual daily spending pace.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Primary Forecast Hero Card (contains the exact prompt quotes)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(),
                border = cardBorder,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SPENDING FORECAST",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "Auto-Calculated",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!hasEnoughData) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "📊", fontSize = 32.sp)
                            Text(
                                text = "Not enough spending recorded yet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Log your daily expenses so the app can detect your average burn rate!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // User prompt exact requirement #1 & #2:
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "“You are currently spending around ${DateUtils.formatMoney(currentBurnRate, currency)} per day.”",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = if (uiState.willRunOutBeforeCycleEnd) StatusRed else StatusGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "“At this spending rate, your allowance may run out around ${uiState.estimatedRunoutDateDisplay}.”",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (uiState.willRunOutBeforeCycleEnd) StatusRed else StatusGreen
                                    )
                                }
                            }
                        }

                        // Cycle Comparison Verdict
                        if (uiState.willRunOutBeforeCycleEnd) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = StatusRed.copy(alpha = 0.12f),
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
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = StatusRed,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Early Runout Alert!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = StatusRed
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Your allowance may run out ${uiState.runoutDaysDiff} days before your period ends ($cycleEndDisplay). Reduce spending to ${DateUtils.formatMoney(uiState.recommendedDailyBudget, currency)}/day to make it last.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = StatusGreen.copy(alpha = 0.12f),
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
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "On Track to the Finish!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = StatusGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "At this rate, your money will last through the end of your allowance period ($cycleEndDisplay) with an estimated surplus of ${DateUtils.formatMoney(max(0.0, uiState.projectedSurplusOrDeficit), currency)}!",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Mandatory prompt label:
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Estimated based on your current spending pattern. Updates automatically as you log expenses.",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Summary Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Days Elapsed",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.daysPassed} days",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Days Remaining",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.daysRemaining} days",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Money Left",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "~${uiState.daysOfMoneyLeft} days",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.willRunOutBeforeCycleEnd) StatusRed else StatusGreen
                        )
                    }
                }
            }
        }

        // Interactive "What-If" Spending Simulator Slider
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.elevatedCardElevation(),
                border = cardBorder,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "What-If Rate Simulator",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "${DateUtils.formatMoney(simulatedBurnRate.toDouble(), currency)} / day",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Slide to test how changing your daily spending changes your runout date:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val maxSliderValue = max(500f, (currentBurnRate * 2.5).toFloat()).coerceAtLeast(300f)
                    Slider(
                        value = simulatedBurnRate,
                        onValueChange = { simulatedBurnRate = it },
                        valueRange = 20f..maxSliderValue,
                        steps = 15,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("what_if_slider")
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Simulation Result:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "At ${DateUtils.formatMoney(simulatedBurnRate.toDouble(), currency)}/day, your remaining ${DateUtils.formatMoney(uiState.remainingBalance, currency)} will last for $simulatedDaysLeft days (until $simulatedRunoutDisplay).",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Student Budgeting Tips
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = StatusAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Student Budgeting Survival Rules",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }
                    Text(
                        text = "1. Pack snacks or water to avoid impulse canteen spending.\n2. Always check 'Can I Afford This?' before buying wants.\n3. Keep 1-2 days' daily budget aside for school photocopies or printouts.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
