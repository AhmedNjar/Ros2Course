package com.openamr.ros2course

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.openamr.ros2course.core.di.initKoin

fun main() {
    initKoin()
    application {
        Window(onCloseRequest = ::exitApplication, title = "ROS 2 Course") {
            App()
        }
    }
}
