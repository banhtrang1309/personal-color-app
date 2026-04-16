package com.banhtrang.personal_color_app.data

import com.banhtrang.personal_color_app.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiRepository {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun analyzePersonalColor(faceHex: String, wristHex: String): String {
        return withContext(Dispatchers.IO) {
            // Không dùng try-catch ở đây nữa, để lỗi văng thẳng về ResultScreen
            val prompt = """
                Bạn là chuyên gia tư vấn Personal Color.
                Khách hàng có:
                1. Mã màu da mặt tổng thể: $faceHex
                2. Mã màu vùng da cổ tay (tĩnh mạch): $wristHex
                
                Chỉ trả lời bằng 1 đoạn JSON duy nhất, đúng cấu trúc sau:
                {
                  "season": "Tên mùa chi tiết",
                  "description": "1 câu mô tả",
                  "palette": ["#HEX1", "#HEX2", "#HEX3", "#HEX4", "#HEX5", "#HEX6"],
                  "clothingTips": "Gợi ý quần áo",
                  "makeupTips": "Gợi ý trang điểm"
                }
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            var rawJson = response.text ?: throw Exception("AI không trả lời")

            // MẸO CHUYÊN NGHIỆP: Lột bỏ thẻ markdown nếu AI lỡ thêm vào
            rawJson = rawJson.replace("```json", "")
                .replace("```", "")
                .trim()

            rawJson // Trả về chuỗi JSON đã sạch sẽ
        }
    }
}