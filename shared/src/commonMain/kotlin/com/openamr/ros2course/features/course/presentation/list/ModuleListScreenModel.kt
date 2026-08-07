package com.openamr.ros2course.features.course.presentation.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.domain.usecase.GetModulesUseCase

class ModuleListScreenModel(
    getModules: GetModulesUseCase,
) : ScreenModel {
    val modules: List<CourseModule> = getModules()
}
