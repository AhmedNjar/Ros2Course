package com.openamr.ros2course.core.di

import org.koin.core.context.startKoin

/** Called exactly once per process, before the first Composable is shown. */
fun initKoin() {
    startKoin {
        modules(appModules)
    }
}
