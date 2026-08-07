package com.openamr.ros2course.features.course.data.repository

import com.openamr.ros2course.features.course.data.datasource.CourseLocalDataSource
import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.domain.entity.ModuleContent
import com.openamr.ros2course.features.course.domain.repository.CourseRepository

class CourseRepositoryImpl(
    private val localDataSource: CourseLocalDataSource,
) : CourseRepository {
    override fun getModules(): List<CourseModule> = localDataSource.getModules()

    override suspend fun getModuleContent(module: CourseModule): ModuleContent =
        localDataSource.getModuleContent(module)
}
