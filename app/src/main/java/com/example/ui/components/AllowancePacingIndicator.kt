package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.viewmodel.BudgetPacingStatus

@Composable
fun PacingStatusBadge(
    status: BudgetPacingStatus,
    modifier: Modifier = Modifier
) {
    val (label, bgColor, textColor, icon) = when (status) {
        BudgetPacingStatus.AHEAD_OF_BUDGET -> Quadruple(
            "Ahead of Budget",
            Color(0xFFDCFCE7),
            StatusGreen,
            Icons.Default.TrendingDown
        )
        BudgetPacingStatus.ON_TRACK -> Quadruple(
            "On Track",
            Color(0xFFE0F2FE),
            Color(0xFF0369A1),
            Icons.Default.CheckCircle
        )
        BudgetPacingStatus.SPENDING_FAST -> Quadruple(
            "Spending Fast",
            Color(0xFFFEF3C7),
            StatusAmber,
            Icons.Default.TrendingUp
        )
        BudgetPacingStatus.DEFICIT -> Quadruple(
            "Over Budget",
            Color(0xFFFEE2E2),
            StatusRed,
            Icons.Default.Warning
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DualPacingBar(
    spentPercentage: Float,
    timePassedPercentage: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Spent ${(spentPercentage * 100).toInt()}%",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Cycle Passed ${(timePassedPercentage * 100).toInt()}%",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Background line showing time elapsed
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = timePassedPercentage.coerceIn(0f, 1f))
                    .height(10.dp)
                    .background(Color(0xFFCBD5E1))
            )
            // Foreground line showing budget spent
            val spentColor = when {
                spentPercentage > 1f -> StatusRed
                spentPercentage > timePassedPercentage + 0.15f -> StatusAmber
                else -> StatusGreen
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = spentPercentage.coerceIn(0f, 1f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(spentColor)
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
