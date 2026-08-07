package com.openamr.ros2course.features.course

import com.openamr.ros2course.features.course.data.datasource.CourseLocalDataSource
import com.openamr.ros2course.features.course.data.repository.CourseRepositoryImpl
import com.openamr.ros2course.features.course.domain.repository.CourseRepository
import com.openamr.ros2course.features.course.domain.usecase.GetModuleContentUseCase
import com.openamr.ros2course.features.course.domain.usecase.GetModulesUseCase
import com.openamr.ros2course.features.course.presentation.detail.ModuleDetailScreenModel
import com.openamr.ros2course.features.course.presentation.list.ModuleListScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val courseFeatureModule = module {
    single { CourseLocalDataSource() }
    single<CourseRepository> { CourseRepositoryImpl(get()) }
    factoryOf(::GetModulesUseCase)
    factoryOf(::GetModuleContentUseCase)
    factoryOf(::ModuleListScreenModel)
    // moduleId is a runtime nav argument, not a DI-resolvable dependency, so this
    // factory takes it as a Koin parameter (see ModuleDetailScreen's koinScreenModel call).
    factory { params -> ModuleDetailScreenModel(params.get(), get(), get()) }
}
