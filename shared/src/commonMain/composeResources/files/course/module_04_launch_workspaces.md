## Module 4 — Launch Files & Workspaces

### Learning Objectives
- Write XML and Python launch files
- Pass arguments and remap topics in launch files
- Organize a multi-package workspace

### 4.1 Why Launch Files?

A launch file starts multiple nodes with a single command and lets you:
- Configure parameters inline
- Remap topic names
- Set namespace isolation
- Conditionally include other launch files

### 4.2 XML Launch File

Create `my_py_pkg/launch/my_first_launch.xml`:

```xml
<launch>
    <!-- Start the robot news station -->
    <node pkg="my_py_pkg" exec="robot_news_station" name="robot_news_station">
        <param name="robot_name" value="R2D2" />
    </node>

    <!-- Start the smartphone subscriber -->
    <node pkg="my_py_pkg" exec="smartphone" name="smartphone" />
</launch>
```

### 4.3 Python Launch File

Create `my_py_pkg/launch/my_first_launch.py`:

```python
from launch import LaunchDescription
from launch_ros.actions import Node
from launch.actions import DeclareLaunchArgument
from launch.substitutions import LaunchConfiguration


def generate_launch_description():
    robot_name_arg = DeclareLaunchArgument(
        "robot_name",
        default_value="my_robot",
        description="Name of the robot"
    )

    robot_news_station_node = Node(
        package="my_py_pkg",
        executable="robot_news_station",
        name="robot_news_station",
        parameters=[{"robot_name": LaunchConfiguration("robot_name")}],
        remappings=[("/robot_news", "/robot_news_v2")],
    )

    smartphone_node = Node(
        package="my_py_pkg",
        executable="smartphone",
        name="smartphone",
        remappings=[("/robot_news", "/robot_news_v2")],
    )

    return LaunchDescription([
        robot_name_arg,
        robot_news_station_node,
        smartphone_node,
    ])
```

### 4.4 Install Launch Files

Add to `setup.py` (Python packages):
```python
import os
from glob import glob

data_files=[
    ("share/ament_index/resource_index/packages",
     ["resource/" + package_name]),
    (os.path.join("share", package_name), ["package.xml"]),
    (os.path.join("share", package_name, "launch"),
     glob("launch/*.py") + glob("launch/*.xml")),
],
```

For CMake packages, add to `CMakeLists.txt`:
```cmake
install(DIRECTORY launch
  DESTINATION share/${PROJECT_NAME}/)
```

### 4.5 Run a Launch File

```bash
ros2 launch my_py_pkg my_first_launch.py
ros2 launch my_py_pkg my_first_launch.py robot_name:=R2D2

# XML
ros2 launch my_py_pkg my_first_launch.xml
```

### 4.6 Workspace Organization Best Practices

```
ros2_course_ws/
├── src/
│   ├── my_robot_interfaces/   ← all custom msg/srv/action definitions
│   ├── my_py_pkg/             ← Python nodes
│   ├── my_cpp_pkg/            ← C++ nodes
│   └── my_robot_bringup/      ← launch files and configs for the full system
├── install/
├── build/
└── log/
```

Rebuild only changed packages:
```bash
colcon build --packages-select my_py_pkg
colcon build --symlink-install   # Python: edits take effect without rebuild
```

### Activity 4
> Write a Python launch file that starts three nodes: `robot_news_station`, `smartphone`, and `add_two_ints_server`. Pass `robot_name` as a launch argument with default `"CourseBot"`. Run it and verify all three nodes appear in `ros2 node list`.

---

