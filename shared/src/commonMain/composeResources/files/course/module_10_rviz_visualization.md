## Module 10 — RViz & Visualization

### Learning Objectives
- Navigate the RViz GUI
- Add and configure displays
- Save and reload RViz configurations in launch files
- Use interactive markers

### 10.1 Launching RViz

```bash
rviz2                                   # bare RViz
rviz2 -d my_config.rviz                 # load a saved config
```

### 10.2 Common Displays

| Display | Topic | Use |
|---------|-------|-----|
| RobotModel | `/robot_description` | Show URDF |
| TF | — | Show all frames |
| LaserScan | `/scan` | Show LiDAR hits |
| PointCloud2 | `/points` | Show 3D point cloud |
| Image | `/camera/image_raw` | Camera feed |
| Odometry | `/odom` | Show pose with covariance |
| Path | `/path` | Show planned/executed path |
| Map | `/map` | Show occupancy grid |
| Marker | `/visualization_marker` | Custom shapes |

### 10.3 Save RViz Config in Launch File

```python
import os
from launch import LaunchDescription
from ament_index_python.packages import get_package_share_directory
from launch_ros.actions import Node

def generate_launch_description():
    pkg = get_package_share_directory("my_robot_bringup")
    rviz_config = os.path.join(pkg, "rviz", "robot.rviz")

    rviz_node = Node(
        package="rviz2",
        executable="rviz2",
        arguments=["-d", rviz_config],
        output="screen",
    )

    return LaunchDescription([rviz_node])
```

Install the rviz directory:
```cmake
install(DIRECTORY rviz
  DESTINATION share/${PROJECT_NAME}/)
```

### 10.4 Visualization Markers (Python)

Add `visualization_msgs` to your package dependencies before using markers.

```python
from visualization_msgs.msg import Marker

def publish_sphere_marker(self, x, y, z):
    m = Marker()
    m.header.frame_id = "base_link"
    m.header.stamp = self.get_clock().now().to_msg()
    m.ns = "obstacles"
    m.id = 0
    m.type = Marker.SPHERE
    m.action = Marker.ADD
    m.pose.position.x = x
    m.pose.position.y = y
    m.pose.position.z = z
    m.scale.x = m.scale.y = m.scale.z = 0.3
    m.color.r = 1.0
    m.color.g = 0.0
    m.color.b = 0.0
    m.color.a = 1.0
    self.marker_pub_.publish(m)
```

### Activity 10
> Configure an RViz session showing RobotModel, TF, LaserScan, and Odometry for your Gazebo robot. Save the config to `my_robot_bringup/rviz/robot.rviz` and load it automatically from your launch file.

---

