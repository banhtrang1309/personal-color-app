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
import com.example.personal_color_app.ui.theme.PersonalcolorappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalcolorappTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route // Màn hình hiện đầu tiên
                ) {
                    composable(Screen.Login.route) {
                        LoginScreen(onNavigateToRegister = {
                            navController.navigate(Screen.Register.route){
                                launchSingleTop = true
                            }
                        })
                    }
                    composable(Screen.Register.route) {
                        RegisterScreen(onNavigateToLogin = {
                            // Quay lại màn trước đó thay vì tạo mới để tối ưu bộ nhớ
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}