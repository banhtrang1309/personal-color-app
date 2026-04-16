package com.banhtrang.personal_color_app.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banhtrang.personal_color_app.data.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // 1. Khai báo các công cụ cần thiết cho Firebase
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    // Biến lưu trữ dữ liệu nhập vào
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Hiệu ứng xoay vòng lúc chờ mạng

    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Personal Color AI",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // --- KHU VỰC ĐĂNG NHẬP BẰNG EMAIL (FIREBASE) ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation() // Biến chữ thành dấu chấm đen
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = !isLoading,
            onClick = {
                // Kiểm tra người dùng đã nhập đủ chưa
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập Email và Mật khẩu!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true // Khóa nút, bắt đầu gọi mạng
                scope.launch {
                    val result = authRepository.login(email, password)
                    isLoading = false // Mở khóa nút

                    if (result.isSuccess) {
                        Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                        onNavigateToHome() // CHUYỂN SANG HOMESCREEN
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "Tài khoản không đúng"
                        Toast.makeText(context, "Lỗi: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (isLoading) "Đang kết nối..." else "Đăng nhập", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Hoặc", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))

        // --- KHU VỰC CHỨC NĂNG CŨ CỦA BẠN ---
        Button(
            onClick = {
                // Chức năng Google sẽ làm sau
                Toast.makeText(context, "Đăng nhập Google sắp ra mắt", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("Đăng nhập bằng Google", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Đăng nhập SĐT sắp ra mắt", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Tiếp tục với Số điện thoại", fontSize = 16.sp)
        }

        // --- KHU VỰC CHUYỂN SANG MÀN ĐĂNG KÝ ---
        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Chưa có tài khoản? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Đăng ký ngay",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onNavigateToRegister()
                }
            )
        }
    }
}