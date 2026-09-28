package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    private val monthDayFormat = SimpleDateFormat("MMMM d", Locale.US)
    private val shortMonthDayFormat = SimpleDateFormat("MMM d", Locale.US)

    fun getTodayString(): String {
        return isoFormat.format(Date())
    }

    fun parseIsoDate(dateString: String): Date {
        return try {
            isoFormat.parse(dateString) ?: Date()
        } catch (_: Exception) {
            Date()
        }
    }

    fun formatForDisplay(dateString: String): String {
        return try {
            val date = isoFormat.parse(dateString) ?: return dateString
            displayFormat.format(date)
        } catch (_: Exception) {
            dateString
        }
    }

    fun formatMonthDay(dateString: String): String {
        return try {
            val date = isoFormat.parse(dateString) ?: return dateString
            monthDayFormat.format(date)
        } catch (_: Exception) {
            dateString
        }
    }

    fun formatShortMonthDay(dateString: String): String {
        return try {
            val date = isoFormat.parse(dateString) ?: return dateString
            shortMonthDayFormat.format(date)
        } catch (_: Exception) {
            dateString
        }
    }

    fun addDaysToIso(dateString: String, daysToAdd: Int): String {
        val cal = Calendar.getInstance()
        cal.time = parseIsoDate(dateString)
        cal.add(Calendar.DAY_OF_YEAR, daysToAdd)
        return isoFormat.format(cal.time)
    }

    /**
     * Inclusive days between two dates.
     * E.g. Sep 1 to Sep 30 is 30 days.
     */
    fun daysInclusive(startDateStr: String, endDateStr: String): Int {
        val startCal = Calendar.getInstance().apply {
            time = parseIsoDate(startDateStr)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            time = parseIsoDate(endDateStr)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diff = endCal.timeInMillis - startCal.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
        return max(1, days + 1)
    }

    /**
     * Days remaining from today until endDate (inclusive of today if today <= endDate).
     */
    fun daysRemainingFromToday(endDateStr: String): Int {
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            time = parseIsoDate(endDateStr)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diff = endCal.timeInMillis - todayCal.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
        return if (days >= 0) days + 1 else 0
    }

    /**
     * Days passed from startDate until today (inclusive of today).
     */
    fun daysPassedFromStart(startDateStr: String, totalDays: Int): Int {
        val startCal = Calendar.getInstance().apply {
            time = parseIsoDate(startDateStr)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diff = todayCal.timeInMillis - startCal.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
        return when {
            days < 0 -> 0
            days >= totalDays -> totalDays
            else -> days + 1
        }
    }

    fun formatMoney(amount: Double, symbol: String = "₱"): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = if (amount % 1.0 == 0.0) 0 else 2
            maximumFractionDigits = 2
        }
        return "$symbol${formatter.format(amount)}"
    }
}
