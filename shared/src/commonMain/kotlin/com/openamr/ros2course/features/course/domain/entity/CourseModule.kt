package com.openamr.ros2course.features.course.domain.entity

/**
 * One entry in the course. Modules 1-13 are teaching modules with the
 * Objectives/Content/Activity structure ([hasTabs] = true); the overview,
 * final project, best-practices, and cheat-sheet entries are reference-style
 * pages rendered as a single scrollable document.
 */
data class CourseModule(
    val id: String,
    val order: Int,
    val title: String,
    val languages: String,
    val estimatedHours: Double?,
    val hasTabs: Boolean,
    val resourceFileName: String,
)
