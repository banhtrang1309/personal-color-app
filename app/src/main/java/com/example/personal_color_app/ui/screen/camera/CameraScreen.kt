package com.example.personal_color_app.ui.screen.camera

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.personal_color_app.utils.ColorUtils

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    // 1. CHÌA KHÓA FIX LỖI: Khởi tạo PreviewView 1 lần duy nhất
    val previewView = remember { PreviewView(context) }

    var detectedHexColor by remember { mutableStateOf<String?>(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
    var isFaceMode by remember { mutableStateOf(true) }

    // 2. CHÌA KHÓA FIX LỖI: Dùng LaunchedEffect để lắng nghe khi biến 'lensFacing' thay đổi
    LaunchedEffect(lensFacing) {
        val executor = ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Kết nối PreviewView với Camera
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Chọn camera trước hoặc sau dựa trên biến trạng thái
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                // Ngắt kết nối camera cũ trước khi bật camera mới
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraScreen", "Lỗi khi đổi camera", e)
            }
        }, executor)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 3. AndroidView giờ chỉ có nhiệm vụ hiển thị previewView đã được cấu hình
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Các lớp Overlay và Nút bấm giữ nguyên không đổi
        if (isFaceMode) {
            FaceScannerOverlay()
            Text(
                text = "Căn chỉnh khuôn mặt vào khung\nvà đặt vùng MÁ vào ô vuông",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center).padding(top = 220.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            WristScannerOverlay()
            Text(
                text = "Căn chỉnh cổ tay vào khung\nvà vùng phẳng nhất vào ô vuông",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center).padding(top = 180.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Cụm Nút điều khiển (Top)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { isFaceMode = !isFaceMode }) {
                Text(if (isFaceMode) "Đổi sang Cổ tay" else "Đổi sang Khuôn mặt", color = Color.White)
            }

            TextButton(onClick = {
                // Đảo ngược trạng thái Camera
                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    CameraSelector.LENS_FACING_BACK
                } else {
                    CameraSelector.LENS_FACING_FRONT
                }
            }) {
                Text("Xoay Camera", color = Color.White)
            }
        }

        // Kết quả & Nút Chụp (Bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            detectedHexColor?.let { hex ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(color = Color(android.graphics.Color.parseColor(hex)), shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Màu của bạn: $hex",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f)).padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(onClick = {
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            super.onCaptureSuccess(image)
                            val bitmap = image.toBitmap()
                            detectedHexColor = ColorUtils.getAverageColorFromCenter(bitmap)
                            image.close()
                        }
                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraScreen", "Chụp thất bại: ${exception.message}")
                        }
                    }
                )
            }) {
                Text("Quét Màu")
            }
        }
    }
}