package com.banhtrang.personal_color_app.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    suspend fun register(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- GOOGLE SIGN IN ---
    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout(context: Context) {
        // 1. Đăng xuất khỏi Firebase
        firebaseAuth.signOut()

        // 2. Xóa trí nhớ của Google (Bắt buộc để lần sau hiện lại bảng chọn tài khoản)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
    }

    // --- QUÊN MẬT KHẨU ---
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}