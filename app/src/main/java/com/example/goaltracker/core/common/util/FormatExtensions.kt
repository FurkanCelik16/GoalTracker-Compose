package com.example.goaltracker.core.common.util

import java.util.Locale

fun Float.formatAmount(): String {
    return if (this % 1.0f == 0.0f) {
        this.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", this)
    }
}