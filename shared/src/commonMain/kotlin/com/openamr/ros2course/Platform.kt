package com.openamr.ros2course

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform