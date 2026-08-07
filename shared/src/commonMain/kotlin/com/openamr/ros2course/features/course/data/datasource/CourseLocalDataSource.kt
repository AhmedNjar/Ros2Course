package com.openamr.ros2course.features.course.data.datasource

import com.openamr.ros2course.core.markdown.MarkdownSectionSplitter
import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.domain.entity.ModuleContent
import com.openamr.ros2course.shared.resources.Res

/**
 * Course content is bundled as Markdown files under
 * `commonMain/composeResources/files/course/`, split directly from
 * openamr-platform-sw's ROS2_COMPLETE_COURSE.md (one file per `##` section).
 * Metadata below (title, languages, time) mirrors that doc's own
 * "Course Structure" table, so it only needs updating if the source doc's
 * table of contents changes.
 */
class CourseLocalDataSource {

    fun getModules(): List<CourseModule> = modules

    suspend fun getModuleContent(module: CourseModule): ModuleContent {
        val bytes = Res.readBytes("files/course/${module.resourceFileName}")
        val raw = bytes.decodeToString()

        if (module.hasTabs) {
            val split = MarkdownSectionSplitter.splitStructured(raw)
            if (split != null) {
                val (overview, content, activity) = split
                return ModuleContent.Structured(overview, content, activity)
            }
        }
        return ModuleContent.SingleSection(raw.trim())
    }

    private companion object {
        val modules = listOf(
            CourseModule(
                id = "overview_setup", order = 0, title = "Course Overview & Environment Setup",
                languages = "Ubuntu + ROS 2 Jazzy", estimatedHours = null, hasTabs = false,
                resourceFileName = "module_00_overview_setup.md",
            ),
            CourseModule(
                id = "module_01", order = 1, title = "ROS 2 Fundamentals",
                languages = "Python + C++", estimatedHours = 3.0, hasTabs = true,
                resourceFileName = "module_01_fundamentals.md",
            ),
            CourseModule(
                id = "module_02", order = 2, title = "Topics & Communication",
                languages = "Python + C++", estimatedHours = 4.0, hasTabs = true,
                resourceFileName = "module_02_topics_communication.md",
            ),
            CourseModule(
                id = "module_03", order = 3, title = "Services & Parameters",
                languages = "Python + C++", estimatedHours = 3.0, hasTabs = true,
                resourceFileName = "module_03_services_parameters.md",
            ),
            CourseModule(
                id = "module_04", order = 4, title = "Launch Files & Workspaces",
                languages = "XML + Python", estimatedHours = 3.0, hasTabs = true,
                resourceFileName = "module_04_launch_workspaces.md",
            ),
            CourseModule(
                id = "module_05", order = 5, title = "ROS 2 Tools & Debugging",
                languages = "CLI", estimatedHours = 2.0, hasTabs = true,
                resourceFileName = "module_05_tools_debugging.md",
            ),
            CourseModule(
                id = "module_06", order = 6, title = "TF2 & Transforms",
                languages = "Python + C++", estimatedHours = 3.0, hasTabs = true,
                resourceFileName = "module_06_tf2_transforms.md",
            ),
            CourseModule(
                id = "module_07", order = 7, title = "URDF & Robot Modeling",
                languages = "XML/URDF", estimatedHours = 4.0, hasTabs = true,
                resourceFileName = "module_07_urdf_modeling.md",
            ),
            CourseModule(
                id = "module_08", order = 8, title = "Gazebo Simulation",
                languages = "Gazebo Harmonic", estimatedHours = 4.0, hasTabs = true,
                resourceFileName = "module_08_gazebo_simulation.md",
            ),
            CourseModule(
                id = "module_09", order = 9, title = "Xacro & Advanced URDF",
                languages = "Xacro", estimatedHours = 3.0, hasTabs = true,
                resourceFileName = "module_09_xacro_advanced_urdf.md",
            ),
            CourseModule(
                id = "module_10", order = 10, title = "RViz & Visualization",
                languages = "RViz", estimatedHours = 2.0, hasTabs = true,
                resourceFileName = "module_10_rviz_visualization.md",
            ),
            CourseModule(
                id = "module_11", order = 11, title = "ROS 2 Actions",
                languages = "Python + C++", estimatedHours = 4.0, hasTabs = true,
                resourceFileName = "module_11_actions.md",
            ),
            CourseModule(
                id = "module_12", order = 12, title = "Lifecycle Nodes",
                languages = "Python + C++", estimatedHours = 3.0, hasTabs = true,
                resourceFileName = "module_12_lifecycle_nodes.md",
            ),
            CourseModule(
                id = "module_13", order = 13, title = "Executors & Components",
                languages = "C++", estimatedHours = 3.0, hasTabs = true,
                resourceFileName = "module_13_executors_components.md",
            ),
            CourseModule(
                id = "final_project", order = 14, title = "Final Project — Complete Robot",
                languages = "Mixed", estimatedHours = 5.0, hasTabs = false,
                resourceFileName = "module_14_final_project.md",
            ),
            CourseModule(
                id = "best_practices", order = 15, title = "Best Practices Reference",
                languages = "Reference", estimatedHours = null, hasTabs = false,
                resourceFileName = "module_15_best_practices.md",
            ),
            CourseModule(
                id = "cheat_sheet", order = 16, title = "Quick Reference Cheat Sheet",
                languages = "Reference", estimatedHours = null, hasTabs = false,
                resourceFileName = "module_16_cheat_sheet.md",
            ),
        )
    }
}
