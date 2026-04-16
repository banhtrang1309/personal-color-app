package com.banhtrang.personal_color_app.utils

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.FirebaseNetworkException

object AuthErrorHandler {

    fun getMessage(exception: Throwable?): String {
        return when (exception) {
            // 1. NHÓM LỖI ĐĂNG NHẬP & ĐỊNH DẠNG
            is FirebaseAuthInvalidCredentialsException -> {
                val rawMsg = exception.message?.lowercase() ?: ""
                // Tách riêng lỗi sai định dạng (thiếu @, sai cú pháp)
                if (rawMsg.contains("badly formatted") || rawMsg.contains("invalid_email")) {
                    "Định dạng Email không hợp lệ"
                } else {
                    // Lỗi gõ sai email hoặc mật khẩu
                    "Tài khoản hoặc Mật khẩu không chính xác!"
                }
            }

            // Lỗi này thỉnh thoảng vẫn xuất hiện nếu Firebase tắt chế độ chống dò rỉ
            is FirebaseAuthInvalidUserException -> "Tài khoản không tồn tại trên hệ thống!"

            // 2. NHÓM LỖI ĐĂNG KÝ
            is FirebaseAuthUserCollisionException -> "Email này đã được sử dụng. Vui lòng đăng nhập!"
            is FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu (cần tối thiểu 6 ký tự)!"

            // 3. NHÓM LỖI CHUNG
            is FirebaseNetworkException -> "Không có kết nối mạng. Vui lòng kiểm tra Wifi/4G!"

            // 4. CÁC LỖI KHÁC VÀ LỖI ĐỤNG ĐỘ GOOGLE
            else -> {
                val rawMessage = exception?.message ?: "Lỗi không xác định"
                if (rawMessage.contains("already exists") || rawMessage.contains("different credential")) {
                    "Email này đã gắn với Mật khẩu. Vui lòng dùng Mật khẩu để đăng nhập!"
                } else {
                    "Đã có lỗi xảy ra: $rawMessage"
                }
            }
        }
    }
}