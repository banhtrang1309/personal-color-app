package com.banhtrang.personal_color_app.ui.screen.camera

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.banhtrang.personal_color_app.utils.ColorUtils

@Composable
fun CameraScreen(onColorScanned: (faceHex: String, wristHex: String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // --- 1. XỬ LÝ XIN QUYỀN CAMERA ---
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("Vui lòng cấp quyền Camera để quét màu!", color = Color.White)
        }
        return
    }

    // --- 2. KHỞI TẠO CAMERA ---
    var isProcessing by remember { mutableStateOf(false) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var faceHexColor by remember { mutableStateOf<String?>(null) }
    var wristHexColor by remember { mutableStateOf<String?>(null) }

    // ĐÃ SỬA: Mặc định là Camera Sau
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isFaceMode by remember { mutableStateOf(true) }

    LaunchedEffect(previewView, lensFacing) {
        val pv = previewView ?: return@LaunchedEffect
        val executor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(pv.surfaceProvider)
            }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
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
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView = it }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Khuôn mặt hoặc Cổ tay
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
                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    CameraSelector.LENS_FACING_BACK
                } else {
                    CameraSelector.LENS_FACING_FRONT
                }
            }) {
                Text("Xoay Camera", color = Color.White)
            }
        }

        // CỤM KẾT QUẢ VÀ NÚT CHỤP (Bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hiện mã màu khuôn mặt nếu đang ở mode Cổ tay VÀ đã quét mặt trước đó
            if (!isFaceMode && faceHexColor != null) {
                ColorResultBadge(title = "Màu da mặt đã quét", hex = faceHexColor!!)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Hiện mã màu cổ tay nếu đang ở mode Mặt VÀ đã quét cổ tay trước đó
            if (isFaceMode && wristHexColor != null) {
                ColorResultBadge(title = "Màu cổ tay đã quét", hex = wristHexColor!!)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nút bấm chụp ảnh
            Button(
                enabled = !isProcessing,
                onClick = {
                    isProcessing = true

                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                super.onCaptureSuccess(image)
                                val bitmap = image.toBitmap()
                                val hex = ColorUtils.getAverageColorFromCenter(bitmap) ?: "#FFFFFF"

                                // ĐÃ SỬA: Logic quét tự do
                                if (isFaceMode) {
                                    faceHexColor = hex
                                    // Nếu chưa quét tay thì tự nhảy sang mode quét tay
                                    if (wristHexColor == null) isFaceMode = false
                                } else {
                                    wristHexColor = hex
                                    // Nếu chưa quét mặt thì tự nhảy sang mode quét mặt
                                    if (faceHexColor == null) isFaceMode = true
                                }

                                // Nếu đã có đủ 2 dữ liệu thì gửi đi!
                                if (faceHexColor != null && wristHexColor != null) {
                                    onColorScanned(faceHexColor!!, wristHexColor!!)
                                }

                                image.close()
                                isProcessing = false
                            }
                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraScreen", "Chụp thất bại: ${exception.message}")
                                isProcessing = false
                            }
                        }
                    )
                }
            ) {
                // ĐÃ SỬA: Logic hiển thị chữ trên nút thông minh hơn
                val isStep2 = (isFaceMode && wristHexColor != null) || (!isFaceMode && faceHexColor != null)
                val stepText = if (isStep2) "(2/2)" else "(1/2)"

                Text(
                    text = if (isProcessing) "Đang phân tích..."
                    else if (isFaceMode) "Quét Khuôn Mặt $stepText"
                    else "Quét Cổ Tay $stepText",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

// Hàm UI phụ để tái sử dụng việc hiển thị cái thẻ màu
@Composable
fun ColorResultBadge(title: String, hex: String) {
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
            text = "$title: $hex",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f)).padding(4.dp)
        )
    }
}