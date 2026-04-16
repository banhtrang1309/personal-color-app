package com.banhtrang.personal_color_app.ui.screen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")       // Thêm màn hình chính
    object Camera : Screen("camera")   // Thêm màn hình quét camera
    object Result : Screen("result/{hex}") // Nhận mã màu HEX từ Camera truyền sang
}