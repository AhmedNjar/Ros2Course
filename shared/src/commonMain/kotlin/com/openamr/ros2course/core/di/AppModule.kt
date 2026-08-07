package com.openamr.ros2course.core.di

import com.openamr.ros2course.features.course.courseFeatureModule
import org.koin.core.module.Module

val appModules: List<Module> = listOf(
    courseFeatureModule,
)
