package com.banhtrang.personal_color_app.ui.screen.result

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banhtrang.personal_color_app.data.AiRepository
import com.banhtrang.personal_color_app.data.ColorAnalysisResult // IMPORT DATA CLASS CHUẨN
import com.banhtrang.personal_color_app.data.HistoryRepository // IMPORT REPOSITORY
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun ResultScreen(
    faceHexCode: String,
    wristHexCode: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val aiRepository = remember { AiRepository() }
    val historyRepository = remember { HistoryRepository() } // KHỞI TẠO HISTORY
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var resultData by remember { mutableStateOf<ColorAnalysisResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val jsonString = aiRepository.analyzePersonalColor(faceHexCode, wristHexCode)
                val json = JSONObject(jsonString)

                val paletteArray = json.getJSONArray("palette")
                val paletteList = mutableListOf<String>()
                for (i in 0 until paletteArray.length()) {
                    paletteList.add(paletteArray.getString(i))
                }

                val newData = ColorAnalysisResult(
                    season = json.getString("season"),
                    description = json.getString("description"),
                    palette = paletteList,
                    clothingTips = json.getString("clothingTips"),
                    makeupTips = json.getString("makeupTips")
                )

                resultData = newData
                isLoading = false

                // LƯU KẾT QUẢ VÀO FIRESTORE NGAY KHI PHÂN TÍCH XONG
                val saveResult = historyRepository.saveToHistory(newData)
                if (saveResult.isSuccess) {
                    Toast.makeText(context, "Đã lưu vào Lịch sử", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                isLoading = false
                val rawError = e.message ?: "Lỗi không xác định"
                errorMessage = when {
                    rawError.contains("503") || rawError.contains("high demand") -> "Hệ thống AI đang quá tải. Vui lòng thử lại sau vài phút nhé!"
                    rawError.contains("Unable to resolve host") || rawError.contains("timeout") -> "Mạng yếu hoặc không kết nối internet. Kiểm tra lại Wifi/4G."
                    rawError.contains("API key not valid") -> "Khóa kết nối AI không hợp lệ."
                    else -> "Đã xảy ra sự cố: Rất tiếc không thể kết nối AI lúc này."
                }
                android.util.Log.e("LOI_AI_CUA_HIEU", "Chi tiết lỗi nguyên thủy: ", e)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        when {
            isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("AI đang phân tích sắc tố da...", fontSize = 16.sp)
                }
            }
            errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.Warning, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Red)
                    Text(text = errorMessage!!, textAlign = TextAlign.Center)
                    Button(onClick = onNavigateBack, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Quay lại")
                    }
                }
            }
            resultData != null -> {
                ResultContent(resultData!!, onNavigateBack)
            }
        }
    }
}

// Giữ nguyên ResultContent và SuggestionItem như cũ
@Composable
fun ResultContent(result: ColorAnalysisResult, onNavigateBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        Text(text = "Kết quả của bạn là:", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = result.season, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = result.description, fontSize = 14.sp, lineHeight = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Bảng màu chân ái", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(200.dp).padding(top = 8.dp),
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
        SuggestionItem(Icons.Rounded.Checkroom, "Gợi ý trang phục", result.clothingTips)
        Spacer(modifier = Modifier.height(16.dp))
        SuggestionItem(Icons.Rounded.Face, "Bí kíp trang điểm", result.makeupTips)

        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Về trang chủ", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SuggestionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}