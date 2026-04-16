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
import com.banhtrang.personal_color_app.ui.screen.history.HistoryScreen // ĐÃ THÊM IMPORT NÀY
import com.banhtrang.personal_color_app.ui.theme.PersonalcolorappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalcolorappTheme {
                val navController = rememberNavController()

                // Khởi tạo "Bộ não" ngay tại cổng vào của App
                val authRepository = remember { AuthRepository() }

                // Xét duyệt vé: Đã có tài khoản chưa?
                val startScreen = if (authRepository.isUserLoggedIn()) {
                    Screen.Home.route
                } else {
                    Screen.Login.route
                }

                NavHost(
                    navController = navController,
                    startDestination = startScreen
                ) {
                    // 1. Màn Đăng Nhập
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register.route) { launchSingleTop = true }
                            },
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                            }
                        )
                    }

                    // 2. Màn Đăng Ký
                    composable(Screen.Register.route) {
                        RegisterScreen(
                            onNavigateToLogin = { navController.popBackStack() },
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                            }
                        )
                    }

                    // 3. Màn Trang Chủ
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToCamera = {
                                navController.navigate(Screen.Camera.route) { launchSingleTop = true }
                            },
                            // ĐÃ THÊM: Truyền lệnh điều hướng tới Lịch sử
                            onNavigateToHistory = {
                                navController.navigate("history") { launchSingleTop = true }
                            },
                            onLogout = {
                                navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                            }
                        )
                    }

                    // 4. Màn Camera
                    composable(Screen.Camera.route) {
                        CameraScreen(
                            onColorScanned = { faceHex, wristHex ->
                                val cleanFace = faceHex.removePrefix("#")
                                val cleanWrist = wristHex.removePrefix("#")
                                navController.navigate("result/$cleanFace/$cleanWrist") {
                                    popUpTo(Screen.Camera.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 5. Màn Kết Quả AI
                    composable("result/{faceHex}/{wristHex}") { backStackEntry ->
                        val faceHex = backStackEntry.arguments?.getString("faceHex") ?: "FFFFFF"
                        val wristHex = backStackEntry.arguments?.getString("wristHex") ?: "FFFFFF"

                        ResultScreen(
                            faceHexCode = "#$faceHex",
                            wristHexCode = "#$wristHex",
                            onNavigateBack = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 6. MÀN HÌNH LỊCH SỬ (ĐÃ THÊM)
                    composable("history") {
                        HistoryScreen(
                            onNavigateBack = {
                                navController.popBackStack() // Quay lại màn hình trước đó (Home)
                            }
                        )
                    }
                }
            }
        }
    }
}