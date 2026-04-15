package com.example.personal_color_app.ui.screen.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Dữ liệu mẫu (Lát nữa AI sẽ trả về cái này)
data class ColorResult(
    val season: String,
    val description: String,
    val palette: List<String>,
    val clothingTips: String,
    val makeupTips: String
)

@Composable
fun ResultScreen(
    faceHexCode: String,   // Thêm biến này
    wristHexCode: String,  // Thêm biến này
    onNavigateBack: () -> Unit
) {
    // Giả lập logic phân tích: Nếu HEX có thiên hướng vàng -> Mùa Thu
    val result = ColorResult(
        season = "Mùa Thu Ấm Áp",
        description = "Bạn sở hữu tone da ấm, phù hợp với những gam màu trầm, sang trọng và gần gũi với thiên nhiên.",
        palette = listOf("#6B4226", "#8E3200", "#D5CEA3", "#E14D2A", "#FD841F", "#3E6D9C"),
        clothingTips = "Hãy ưu tiên các loại vải có chất liệu tự nhiên, màu cam đất, xanh rêu hoặc nâu be.",
        makeupTips = "Son màu đỏ gạch hoặc cam cháy sẽ làm gương mặt bạn bừng sáng."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // --- 1. Header ---
        Text(text = "Kết quả phân tích", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = result.season, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = result.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. Bảng màu Palette ---
        Text(text = "Bảng màu của bạn", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(180.dp).padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(result.palette) { hex ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(color = Color(android.graphics.Color.parseColor(hex)), shape = RoundedCornerShape(12.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. Gợi ý chi tiết ---
        SuggestionItem(icon = Icons.Rounded.Checkroom, title = "Trang phục", content = result.clothingTips)
        Spacer(modifier = Modifier.height(16.dp))
        SuggestionItem(icon = Icons.Rounded.Face, title = "Trang điểm", content = result.makeupTips)

        Spacer(modifier = Modifier.weight(1f))

        // --- 4. Nút bấm ---
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Xong", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SuggestionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}