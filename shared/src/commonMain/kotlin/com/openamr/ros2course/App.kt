package com.openamr.ros2course

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.openamr.ros2course.core.theme.CourseAppTheme
import com.openamr.ros2course.features.course.presentation.list.ModuleListScreen

@Composable
fun App() {
    CourseAppTheme {
        Navigator(ModuleListScreen)
    }
}
