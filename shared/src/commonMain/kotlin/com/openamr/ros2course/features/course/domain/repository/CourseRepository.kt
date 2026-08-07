package com.openamr.ros2course.features.course.domain.repository

import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.domain.entity.ModuleContent

interface CourseRepository {
    fun getModules(): List<CourseModule>
    suspend fun getModuleContent(module: CourseModule): ModuleContent
}
