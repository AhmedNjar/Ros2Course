package com.openamr.ros2course

import androidx.compose.ui.window.ComposeUIViewController
import com.openamr.ros2course.core.di.initKoin
import platform.UIKit.UIViewController

private var koinStarted = false

fun MainViewController(): UIViewController {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }
    return ComposeUIViewController { App() }
}
