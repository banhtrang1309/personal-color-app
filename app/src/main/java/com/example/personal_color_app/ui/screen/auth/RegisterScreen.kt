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
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    // Biến quản lý trạng thái Tab: 0 là Gmail, 1 là Số điện thoại
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Gmail", "Số điện thoại")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tạo Tài Khoản",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Khám phá màu sắc cá nhân của bạn",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Thanh TabRow để chuyển đổi phương thức
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        // Thay đổi giao diện dựa trên Tab được chọn
        if (selectedTabIndex == 0) {
            // UI cho Đăng ký bằng Gmail (Google)
            Text(
                text = "Sử dụng tài khoản Google của bạn để đăng ký nhanh chóng và an toàn.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(
                onClick = { /* TODO: Xử lý Đăng ký Google (Firebase) */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Tiếp tục với Gmail (Google)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            // UI cho Đăng ký bằng Số điện thoại
            var phoneNumber by remember { mutableStateOf("") }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Nhập số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("VD: 0912345678") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* TODO: Xử lý gửi mã OTP (Firebase) */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Gửi mã xác nhận OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Nút chuyển hướng về màn Đăng nhập
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Đã có tài khoản? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Đăng nhập",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onNavigateToLogin()
                } // Gọi hàm khi click
            )
        }
    }
}