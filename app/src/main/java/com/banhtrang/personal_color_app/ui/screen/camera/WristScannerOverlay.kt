package com.banhtrang.personal_color_app.ui.screen.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun WristScannerOverlay() {
    // graphicsLayer với alpha = 0.99f là một mẹo bắt buộc trong Compose
    // để tạo một lớp đệm (offscreen buffer), giúp hiệu ứng "đục lỗ" (BlendMode.Clear)
    // không bị lủng xuyên qua và làm đen luôn cả camera bên dưới.
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.99f }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 1. Vẽ lớp nền làm tối toàn bộ màn hình (alpha = 0.6f để mờ 60%)
        drawRect(color = Color.Black.copy(alpha = 0.6f))

        // --- CẤU HÌNH KÍCH THƯỚC ---

        // Kích thước ô vuông đích 50x50 dp (phải khớp với logic hàm lấy màu của bạn)
        val targetBoxSizePx = 50.dp.toPx()
        val boxLeft = (canvasWidth - targetBoxSizePx) / 2
        val boxTop = (canvasHeight - targetBoxSizePx) / 2

        // Kích thước khung cánh tay (hình bầu dục đứt nét bên ngoài)
        val guideWidth = canvasWidth * 0.6f
        val guideHeight = canvasHeight * 0.5f
        val guideLeft = (canvasWidth - guideWidth) / 2
        val guideTop = (canvasHeight - guideHeight) / 2

        // 2. "Đục lỗ" ô vuông 50x50 ở giữa bằng BlendMode.Clear
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(boxLeft, boxTop),
            size = Size(targetBoxSizePx, targetBoxSizePx),
            blendMode = BlendMode.Clear // Phép thuật nằm ở đây!
        )

        // 3. Vẽ viền trắng liền nét cho ô vuông đích (Target Box)
        drawRect(
            color = Color.White,
            topLeft = Offset(boxLeft, boxTop),
            size = Size(targetBoxSizePx, targetBoxSizePx),
            style = Stroke(width = 2.dp.toPx())
        )

        // 4. Vẽ khung hướng dẫn cánh tay (Nét đứt, bo góc tròn)
        drawRoundRect(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(guideLeft, guideTop),
            size = Size(guideWidth, guideHeight),
            cornerRadius = CornerRadius(40.dp.toPx(), 40.dp.toPx()),
            style = Stroke(
                width = 2.dp.toPx(),
                // Tạo nét đứt: dài 20px, khoảng cách 20px
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
            )
        )
    }
}