package com.banhtrang.personal_color_app.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    // --- State quản lý dữ liệu ---
    var isEmailMode by remember { mutableStateOf(true) } // Mặc định là Email
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Đăng ký tài khoản",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- 1. THANH CHUYỂN ĐỔI (TAB) ---
        TabRow(selectedTabIndex = if (isEmailMode) 0 else 1) {
            Tab(
                selected = isEmailMode,
                onClick = { isEmailMode = true },
                text = { Text("Email") }
            )
            Tab(
                selected = !isEmailMode,
                onClick = { isEmailMode = false },
                text = { Text("Số điện thoại") }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. NỘI DUNG ĐĂNG KÝ BIẾN ĐỔI ---
        if (isEmailMode) {
            // Đăng ký bằng Email
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
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Xác nhận mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
        } else {
            // Đăng ký bằng Số điện thoại
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Hệ thống sẽ gửi mã OTP về số điện thoại này.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. NÚT ĐĂNG KÝ CHÍNH ---
        Button(
            enabled = !isLoading,
            onClick = {
                if (isEmailMode) {
                    // Logic Đăng ký Email thật
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password.length < 6) {
                        Toast.makeText(context, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        val result = authRepository.register(email, password)
                        isLoading = false
                        if (result.isSuccess) {
                            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                            onNavigateToLogin() // Đăng ký xong quay về Login để đăng nhập
                        } else {
                            val error = result.exceptionOrNull()?.message ?: "Lỗi đăng ký"
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Đăng ký SĐT sẽ cập nhật sau", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (isLoading) "Đang xử lý..." else "Đăng ký ngay", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Hoặc", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. ĐĂNG KÝ BẰNG GOOGLE ---
        Button(
            onClick = { /* Chờ xử lý SHA-1 */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("Tiếp tục với Google", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quay lại Đăng nhập
        Row {
            Text(text = "Đã có tài khoản? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Đăng nhập",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}