package com.openamr.ros2course.features.course.domain.usecase

import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.domain.entity.ModuleContent
import com.openamr.ros2course.features.course.domain.repository.CourseRepository

class GetModuleContentUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(module: CourseModule): ModuleContent = repository.getModuleContent(module)
}
