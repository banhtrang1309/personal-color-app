package com.banhtrang.personal_color_app.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.History // THÊM IMPORT ICON HISTORY
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.banhtrang.personal_color_app.data.AuthRepository

@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToHistory: () -> Unit, // ĐÃ THÊM: Hàm chuyển hướng tới Lịch sử
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // --- 1. Header (Lời chào + Nút Đăng xuất) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Xin chào,", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Tín đồ thời trang!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            val context = LocalContext.current
            val authRepository = remember { AuthRepository() }
            IconButton(
                onClick = {
                    authRepository.logout(context)
                    onLogout()
                }) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- 2. Thẻ chức năng chính (Nút gọi Camera) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { onNavigateToCamera() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = "Quét màu",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Quét Màu Sắc Cá Nhân",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. Thẻ chức năng phụ (Nút xem Lịch sử) - ĐÃ THÊM ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onNavigateToHistory() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = "Lịch sử",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Lịch sử phân tích",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- 4. Khu vực chờ tính năng AI ---
        Text(
            text = "Gợi ý cho bạn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Hãy thực hiện quét màu sắc cá nhân để AI gợi ý trang phục và màu son phù hợp nhất với bạn nhé!",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}