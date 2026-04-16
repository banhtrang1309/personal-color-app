package com.banhtrang.personal_color_app.data

import com.banhtrang.personal_color_app.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiRepository {
    // Khởi tạo Gemini với model "flash" (nhanh, nhẹ, thông minh) và lấy Key từ két sắt
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    // Hàm gọi AI, sử dụng Coroutine (suspend) vì việc gọi mạng tốn thời gian
    suspend fun analyzePersonalColor(faceHex: String, wristHex: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Đây là Prompt - Câu lệnh chúa tể để ép AI làm theo ý mình
                val prompt = """
                    Bạn là một chuyên gia thời trang và tư vấn Personal Color (Màu sắc cá nhân) hàng đầu.
                    Khách hàng của tôi vừa cung cấp 2 dữ liệu màu sắc được quét từ camera:
                    1. Mã màu da mặt tổng thể: $faceHex
                    2. Mã màu vùng da cổ tay (tĩnh mạch): $wristHex
                    
                    Dựa vào 2 mã HEX này, hãy phân tích Undertone (Ấm/Lạnh) và Overtone để xác định chính xác họ thuộc mùa nào trong 4 mùa (Xuân, Hạ, Thu, Đông) và nhánh nào (VD: Warm Autumn, Cool Summer...).
                    
                    YÊU CẦU BẮT BUỘC: Bạn CHỈ ĐƯỢC PHÉP trả lời bằng 1 đoạn JSON duy nhất, theo đúng cấu trúc sau, tuyệt đối KHÔNG thêm markdown ```json hay bất kỳ câu chữ giải thích nào bên ngoài:
                    {
                      "season": "Tên mùa chi tiết (VD: Mùa Thu Ấm Áp)",
                      "description": "1 câu mô tả ngắn gọn, truyền cảm hứng về đặc điểm của tone da này.",
                      "palette": ["#HEX1", "#HEX2", "#HEX3", "#HEX4", "#HEX5", "#HEX6"],
                      "clothingTips": "Gợi ý 2-3 màu sắc quần áo nên mặc và chất liệu phù hợp.",
                      "makeupTips": "Gợi ý màu son và màu phấn má phù hợp nhất."
                    }
                    
                    Hãy đảm bảo mảng "palette" chứa đúng 6 mã HEX là những màu sắc "chân ái" nhất cho tone da này.
                """.trimIndent()

                // Gửi câu hỏi cho Gemini và chờ câu trả lời
                val response = generativeModel.generateContent(prompt)

                // Trả về chuỗi JSON (nếu lỗi thì trả về rỗng)
                response.text ?: ""

            } catch (e: Exception) {
                e.printStackTrace()
                // Nếu rớt mạng hoặc lỗi, trả về JSON giả báo lỗi
                """
                {
                  "season": "Lỗi phân tích",
                  "description": "Không thể kết nối với AI lúc này. Vui lòng thử lại sau.",
                  "palette": ["#000000", "#333333", "#666666", "#999999", "#CCCCCC", "#FFFFFF"],
                  "clothingTips": "Đang gặp sự cố mạng.",
                  "makeupTips": "Đang gặp sự cố mạng."
                }
                """.trimIndent()
            }
        }
    }
}