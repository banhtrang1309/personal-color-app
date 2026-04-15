package com.example.personal_color_app.ui.screen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
}