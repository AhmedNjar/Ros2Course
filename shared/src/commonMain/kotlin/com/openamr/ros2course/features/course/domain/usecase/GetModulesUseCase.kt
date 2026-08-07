package com.openamr.ros2course.features.course.domain.usecase

import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.domain.repository.CourseRepository

class GetModulesUseCase(private val repository: CourseRepository) {
    operator fun invoke(): List<CourseModule> = repository.getModules().sortedBy { it.order }
}
