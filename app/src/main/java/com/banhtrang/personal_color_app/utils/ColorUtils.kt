package com.banhtrang.personal_color_app.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

object ColorUtils {
    /**
     * Hàm này nhận vào một Bitmap, lấy một vùng nhỏ ở giữa ảnh và tính màu trung bình
     */
    fun getAverageColorFromCenter(bitmap: Bitmap): String {
        val width = bitmap.width
        val height = bitmap.height

        // Chọn một vùng quét 50x50 pixel ở chính giữa ảnh
        val rectSize = 50
        val startX = (width - rectSize) / 2
        val startY = (height - rectSize) / 2

        var totalR = 0
        var totalG = 0
        var totalB = 0
        var pixelCount = 0

        try {
            for (x in startX until startX + rectSize) {
                for (y in startY until startY + rectSize) {
                    val pixelColor = bitmap.getPixel(x, y)
                    totalR += Color.red(pixelColor)
                    totalG += Color.green(pixelColor)
                    totalB += Color.blue(pixelColor)
                    pixelCount++
                }
            }

            // Tính trung bình cộng
            val avgR = totalR / pixelCount
            val avgG = totalG / pixelCount
            val avgB = totalB / pixelCount

            // Chuyển đổi sang mã HEX (VD: #FF5733) để dễ hiển thị
            val hexColor = String.format("#%02x%02x%02x", avgR, avgG, avgB)

            Log.d("ColorUtils", "RGB: ($avgR, $avgG, $avgB) -> HEX: $hexColor")
            return hexColor

        } catch (e: Exception) {
            Log.e("ColorUtils", "Lỗi khi phân tích ảnh: ${e.message}")
            return "#FFFFFF" // Trả về màu trắng nếu lỗi
        }
    }
}