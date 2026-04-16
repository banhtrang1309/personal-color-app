package com.banhtrang.personal_color_app.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Kiểm tra xem đã đăng nhập chưa
    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    // Đăng ký tài khoản mới
    suspend fun register(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Đăng nhập
    suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Đăng xuất
    fun logout() {
        firebaseAuth.signOut()
    }
}