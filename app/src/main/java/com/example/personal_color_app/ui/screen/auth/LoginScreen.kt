package com.example.personal_color_app.ui.screen.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit) {
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
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Nút Đăng nhập bằng Google
        Button(
            onClick = { /* Xử lý Login Google sau */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Đăng nhập bằng Google", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Hoặc", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        // UI Đăng nhập bằng SĐT thô
        var phoneNumber by remember { mutableStateOf("") }
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Xử lý lấy mã OTP sau */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Tiếp tục với Số điện thoại", fontSize = 16.sp)
        }

        // --- ĐOẠN CODE BỔ SUNG ĐỂ CHUYỂN TRANG ---
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
                // Modifier.clickable là nơi kích hoạt sự kiện chuyển trang
                modifier = Modifier.clickable {
                    onNavigateToRegister()
                }
            )
        }
    }
}