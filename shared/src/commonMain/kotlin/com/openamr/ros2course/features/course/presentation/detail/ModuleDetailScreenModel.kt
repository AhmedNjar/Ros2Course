package com.openamr.ros2course.features.course.presentation.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.domain.entity.ModuleContent
import com.openamr.ros2course.features.course.domain.usecase.GetModuleContentUseCase
import com.openamr.ros2course.features.course.domain.usecase.GetModulesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ModuleDetailUiState(
    val modules: List<CourseModule> = emptyList(),
    val contentCache: Map<String, ModuleContent> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ModuleDetailScreenModel(
    private val initialModuleId: String,
    private val getModules: GetModulesUseCase,
    private val getModuleContent: GetModuleContentUseCase,
) : ScreenModel {

    private val _uiState = MutableStateFlow(ModuleDetailUiState())
    val uiState: StateFlow<ModuleDetailUiState> = _uiState.asStateFlow()

    init {
        loadModules()
    }

    private fun loadModules() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val allModules = getModules()
            _uiState.update { it.copy(modules = allModules, isLoading = false) }
            
            // Prefetch initial module content
            val initial = allModules.find { it.id == initialModuleId }
            if (initial != null) {
                loadContent(initial)
            }
        }
    }

    fun loadContent(module: CourseModule) {
        if (_uiState.value.contentCache.containsKey(module.id)) return

        screenModelScope.launch {
            runCatching { getModuleContent(module) }
                .onSuccess { content ->
                    _uiState.update { state ->
                        state.copy(contentCache = state.contentCache + (module.id to content))
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message ?: "Failed to load content") }
                }
        }
    }
}
