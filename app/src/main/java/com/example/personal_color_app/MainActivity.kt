package com.example.personal_color_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personal_color_app.ui.screen.Screen
import com.example.personal_color_app.ui.screen.auth.LoginScreen
import com.example.personal_color_app.ui.screen.auth.RegisterScreen
import com.example.personal_color_app.ui.screen.home.HomeScreen
import com.example.personal_color_app.ui.screen.camera.CameraScreen
import com.example.personal_color_app.ui.theme.PersonalcolorappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalcolorappTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route // Mặc định mở app lên là Login
                ) {
                    // 1. Màn Đăng Nhập
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register.route) {
                                    launchSingleTop = true
                                }
                            },
                            // Thêm lệnh chuyển sang Home khi đăng nhập thành công
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) {
                                    // Xóa màn Login khỏi lịch sử để không bấm Back lùi lại được
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Màn Đăng Ký
                    composable(Screen.Register.route) {
                        RegisterScreen(onNavigateToLogin = {
                            // Quay lại màn trước đó thay vì tạo mới để tối ưu bộ nhớ
                            navController.popBackStack()
                        })
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
                                // Đăng xuất thì quay về Login VÀ xóa sạch toàn bộ lịch sử
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 4. Màn Camera
                    composable(Screen.Camera.route) {
                        CameraScreen()
                    }
                }
            }
        }
    }
}