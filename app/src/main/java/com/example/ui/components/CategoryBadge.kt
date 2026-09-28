package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseCategory

@Composable
fun CategoryIcon(
    categoryName: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 22.dp
) {
    val category = ExpenseCategory.fromString(categoryName)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(category.color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.title,
            tint = category.color,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun CategoryPill(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    val category = ExpenseCategory.fromString(categoryName)
    Surface(
        color = category.color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${category.emoji} ${category.title}",
                color = category.color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
