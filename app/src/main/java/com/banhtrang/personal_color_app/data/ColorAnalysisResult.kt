package com.banhtrang.personal_color_app.data

data class ColorAnalysisResult(
    val id: String = "", // ID của tài liệu trên Firestore
    val timestamp: Long = System.currentTimeMillis(), // Thời gian quét
    val season: String = "",
    val description: String = "",
    val palette: List<String> = emptyList(),
    val clothingTips: String = "",
    val makeupTips: String = ""
)