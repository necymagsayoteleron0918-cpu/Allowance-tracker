package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.CategoryEntertainmentColor
import com.example.ui.theme.CategoryFoodColor
import com.example.ui.theme.CategoryOtherColor
import com.example.ui.theme.CategoryPersonalColor
import com.example.ui.theme.CategorySchoolColor
import com.example.ui.theme.CategoryTranspoColor

enum class ExpenseCategory(
    val title: String,
    val color: Color,
    val icon: ImageVector,
    val emoji: String
) {
    FOOD("Food", CategoryFoodColor, Icons.Default.Restaurant, "🍔"),
    TRANSPORTATION("Transportation", CategoryTranspoColor, Icons.Default.DirectionsBus, "🚌"),
    SCHOOL("School", CategorySchoolColor, Icons.Default.School, "📚"),
    PERSONAL("Personal", CategoryPersonalColor, Icons.Default.Person, "👕"),
    ENTERTAINMENT("Entertainment", CategoryEntertainmentColor, Icons.Default.SportsEsports, "🎮"),
    OTHER("Other", CategoryOtherColor, Icons.Default.Category, "🏷️");

    companion object {
        fun fromString(value: String): ExpenseCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.title.equals(value, ignoreCase = true) }
                ?: OTHER
        }
    }
}
