package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.AllowanceRepository
import com.example.ui.AllowanceApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AllowanceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AllowanceRepository(
            database.allowanceDao(),
            database.expenseDao(),
            database.plannedPurchaseDao()
        )

        val prefs = getSharedPreferences("allowance_prefs", Context.MODE_PRIVATE)
        val initialHighContrast = prefs.getBoolean("high_contrast_mode", false)

        setContent {
            val viewModel: AllowanceViewModel = viewModel(
                factory = AllowanceViewModel.provideFactory(
                    repository = repository,
                    initialHighContrast = initialHighContrast,
                    onThemeChanged = { enabled ->
                        prefs.edit().putBoolean("high_contrast_mode", enabled).apply()
                    }
                )
            )

            val isHighContrast by viewModel.isHighContrast.collectAsStateWithLifecycle()

            MyApplicationTheme(
                darkTheme = false,
                highContrast = isHighContrast
            ) {
                AllowanceApp(viewModel = viewModel)
            }
        }
    }
}
