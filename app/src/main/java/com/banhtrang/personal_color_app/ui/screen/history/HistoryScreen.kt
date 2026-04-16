package com.banhtrang.personal_color_app.ui.screen.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banhtrang.personal_color_app.data.ColorAnalysisResult
import com.banhtrang.personal_color_app.data.HistoryRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit
) {
    val historyRepository = remember { HistoryRepository() }
    val scope = rememberCoroutineScope()

    var historyList by remember { mutableStateOf<List<ColorAnalysisResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Gọi dữ liệu từ Firestore khi mở màn hình
    LaunchedEffect(Unit) {
        scope.launch {
            historyList = historyRepository.getHistory()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử phân tích", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                historyList.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Bạn chưa có lịch sử phân tích nào.", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(historyList) { item ->
                            HistoryItemCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(item: ColorAnalysisResult) {
    // Trạng thái mở rộng thẻ
    var isExpanded by remember { mutableStateOf(false) }

    // Chuyển đổi timestamp sang ngày tháng dễ đọc
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(item.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Mùa và Thời gian
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.season, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                Text(text = dateString, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dải màu sắc (Palette)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                item.palette.take(6).forEach { hex ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(hex)))
                    )
                }
            }

            // Phần nội dung mở rộng (Chỉ hiện khi bấm vào thẻ)
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("👕 Trang phục:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(item.clothingTips, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("💄 Trang điểm:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(item.makeupTips, fontSize = 14.sp)
                }
            }
        }
    }
}