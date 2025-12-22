package com.example.goaltracker.presentation.splash

object QuoteProvider {
    val quotes = listOf(
        "Başarı, her gün tekrarlanan küçük çabaların toplamıdır.",
        "Hedefsiz bir gemiye hiçbir rüzgar yardım edemez.",

        "Vazgeçmediğin sürece, başarısız olmuş sayılmazsın.",
        "Gelecek, bugün ne yaptığına bağlıdır.",
        "En uzun yolculuklar bile tek bir adımla başlar.",

        "Dünden daha iyi olmaya çalış.",
    )

    fun getRandomQuote(): String {
        return quotes.random()
    }
}