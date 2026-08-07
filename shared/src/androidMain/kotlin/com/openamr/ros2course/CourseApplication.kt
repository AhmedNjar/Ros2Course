package com.openamr.ros2course

import android.app.Application
import com.openamr.ros2course.core.di.initKoin

class CourseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
