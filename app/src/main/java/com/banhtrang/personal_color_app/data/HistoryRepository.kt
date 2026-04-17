package com.banhtrang.personal_color_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class HistoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Lấy ID người dùng hiện tại
    private val userId: String? get() = auth.currentUser?.uid

    // Lưu kết quả vào thư mục: users / [userId] / history / [auto_id]
    suspend fun saveToHistory(result: ColorAnalysisResult): Result<Unit> {
        return try {
            val uid = userId ?: throw Exception("Chưa đăng nhập")
            firestore.collection("users").document(uid)
                .collection("history")
                .add(result)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy toàn bộ lịch sử, sắp xếp mới nhất lên đầu
    suspend fun getHistory(): List<ColorAnalysisResult> {
        val uid = userId ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(ColorAnalysisResult::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}