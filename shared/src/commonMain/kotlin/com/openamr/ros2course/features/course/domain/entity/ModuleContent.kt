package com.openamr.ros2course.features.course.domain.entity

/** The parsed body of a module, ready to hand to a Markdown renderer per tab. */
sealed interface ModuleContent {
    data class Structured(
        val overview: String,
        val content: String,
        val activity: String,
    ) : ModuleContent

    data class SingleSection(val content: String) : ModuleContent
}
