package com.example.goaltracker.core.model

enum class ReminderType {
    NONE,       // Kapalı
    ONE_TIME,   // Günde 1 kere

    DAILY,
    INTERVAL    // Aralıklı (Örn: 09:00 - 23:00 arası 2 saatte bir)
}