package com.example.personal_color_app.ui.screen.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun FaceScannerOverlay() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.99f }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 1. Phủ mờ toàn bộ màn hình
        drawRect(color = Color.Black.copy(alpha = 0.6f))

        // 2. Cấu hình ô vuông quét (giữ nguyên ở chính giữa để hợp với ColorUtils)
        val targetBoxSizePx = 50.dp.toPx()
        val boxLeft = (canvasWidth - targetBoxSizePx) / 2
        val boxTop = (canvasHeight - targetBoxSizePx) / 2

        // 3. Cấu hình khung khuôn mặt (Oval dọc)
        val faceWidth = canvasWidth * 0.7f
        val faceHeight = canvasHeight * 0.55f
        val faceLeft = (canvasWidth - faceWidth) / 2

        // Cố tình đẩy khung mặt lên cao một chút
        // để ô vuông ở chính giữa màn hình sẽ rơi trúng vùng "má" thay vì vùng "mũi/miệng"
        val faceTop = (canvasHeight - faceHeight) / 2 - 40.dp.toPx()

        // Đục lỗ ô vuông
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(boxLeft, boxTop),
            size = Size(targetBoxSizePx, targetBoxSizePx),
            blendMode = BlendMode.Clear
        )

        // Vẽ viền ô vuông
        drawRect(
            color = Color.White,
            topLeft = Offset(boxLeft, boxTop),
            size = Size(targetBoxSizePx, targetBoxSizePx),
            style = Stroke(width = 2.dp.toPx())
        )

        // Vẽ khung Oval nét đứt hướng dẫn khuôn mặt
        drawOval(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(faceLeft, faceTop),
            size = Size(faceWidth, faceHeight),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
            )
        )
    }
}