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

    // Tạo đối tượng ImageCapture để chịu trách nhiệm chụp ảnh
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Biến lưu trữ kết quả màu sắc để hiển thị lên UI
    var detectedHexColor by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Lớp dưới cùng: Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // Tạm thời dùng Cam sau (sau này quét mặt thì đổi thành Cam trước)
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        // Ràng buộc cả Preview và ImageCapture vào Vòng đời
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        Log.e("CameraScreen", "Binding failed", e)
                    }
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Lớp ở giữa: Khung Overlay đục lỗ chuyên nghiệp
        WristScannerOverlay()
        // Thêm text hướng dẫn người dùng
        Text(
            text = "Căn chỉnh cổ tay vào khung nét đứt\nvà phần da phẳng nhất vào ô vuông giữa",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 180.dp), // Đẩy chữ xuống dưới khung một chút
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // 3. Lớp trên cùng: Nút bấm và Kết quả
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hiển thị màu đã quét được
            detectedHexColor?.let { hex ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                color = Color(android.graphics.Color.parseColor(hex)),
                                shape = CircleShape
                            )
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

            // Nút Chụp
            Button(onClick = {
                // Xử lý chụp ảnh trong bộ nhớ (In-memory)
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            super.onCaptureSuccess(image)
                            // Đổi ImageProxy thành Bitmap (có sẵn ở CameraX 1.3+)
                            val bitmap = image.toBitmap()

                            // Gọi hàm phân tích màu
                            detectedHexColor = ColorUtils.getAverageColorFromCenter(bitmap)

                            // QUAN TRỌNG: Phải đóng image sau khi dùng xong để tránh tràn RAM
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