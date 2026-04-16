package com.banhtrang.personal_color_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banhtrang.personal_color_app.data.AuthRepository
import com.banhtrang.personal_color_app.ui.screen.Screen
import com.banhtrang.personal_color_app.ui.screen.auth.LoginScreen
import com.banhtrang.personal_color_app.ui.screen.auth.RegisterScreen
import com.banhtrang.personal_color_app.ui.screen.home.HomeScreen
import com.banhtrang.personal_color_app.ui.screen.camera.CameraScreen
import com.banhtrang.personal_color_app.ui.screen.result.ResultScreen
import com.banhtrang.personal_color_app.ui.theme.PersonalcolorappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalcolorappTheme {
                val navController = rememberNavController()
                // 1. Khởi tạo "Bộ não" ngay tại cổng vào của App
                val authRepository = remember { AuthRepository() }

                // 2. Xét duyệt vé: Đã có tài khoản chưa?
                val startScreen = if (authRepository.isUserLoggedIn()) {
                    Screen.Home.route    // Có rồi -> Vào thẳng nhà
                } else {
                    Screen.Login.route   // Chưa có -> Mời ra cửa đăng nhập
                }
                NavHost(
                    navController = navController,
                    startDestination = startScreen // Mặc định mở app lên là Login
                ) {
                    // 1. Màn Đăng Nhập
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register.route) {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Màn Đăng Ký
                    composable(Screen.Register.route) {
                        RegisterScreen(
                            onNavigateToLogin = {
                                navController.popBackStack()
                            },
                            // THÊM ĐOẠN NÀY ĐỂ SỬA LỖI: Cấp đường tới Home cho Google Sign-in
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Màn Trang Chủ
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToCamera = {
                                navController.navigate(Screen.Camera.route) {
                                    launchSingleTop = true
                                }
                            },
                            onLogout = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 4. Màn Camera
                    composable(Screen.Camera.route) {
                        CameraScreen(
                            onColorScanned = { faceHex, wristHex ->
                                // Bỏ dấu '#' của CẢ 2 mã màu
                                val cleanFace = faceHex.removePrefix("#")
                                val cleanWrist = wristHex.removePrefix("#")

                                // Truyền cả 2 vào link URL
                                navController.navigate("result/$cleanFace/$cleanWrist") {
                                    popUpTo(Screen.Camera.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 5. Màn Kết Quả AI (Nhận 2 mã màu)
                    composable("result/{faceHex}/{wristHex}") { backStackEntry ->
                        val faceHex = backStackEntry.arguments?.getString("faceHex") ?: "FFFFFF"
                        val wristHex = backStackEntry.arguments?.getString("wristHex") ?: "FFFFFF"

                        ResultScreen(
                            faceHexCode = "#$faceHex",   // Truyền xuống ResultScreen
                            wristHexCode = "#$wristHex", // Truyền xuống ResultScreen
                            onNavigateBack = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}