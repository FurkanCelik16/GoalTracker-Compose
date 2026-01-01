package com.example.goaltracker.core.common.util

import android.content.Context
import java.time.LocalDate
import androidx.core.content.edit

class SplashManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("GoalTrackerPrefs", Context.MODE_PRIVATE)

    fun shouldShowSplash(): Boolean {
        val lastShownDate = sharedPref.getString("last_splash_date", "")
        val todayDate = LocalDate.now().toString()
        return lastShownDate != todayDate
    }

    fun setSplashShown() {
        val todayDate = LocalDate.now().toString()
        sharedPref.edit { putString("last_splash_date", todayDate) }
    }
}