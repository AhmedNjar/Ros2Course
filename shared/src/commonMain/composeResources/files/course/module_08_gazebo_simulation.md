## Module 8 — Gazebo Simulation

### Learning Objectives
- Launch a robot in Gazebo Harmonic (`gz sim`)
- Add Gz-compatible plugins to the URDF via `<gazebo>` tags
- Use the differential drive plugin for wheel control
- Bridge Gz topics to ROS 2 with `ros_gz_bridge`
- Spawn a robot in a custom world

### 8.1 Gazebo Harmonic Overview

ROS 2 Jazzy uses **Gazebo Harmonic** (the new-generation Gazebo). Key differences from the old "Gazebo Classic":

| Classic Gazebo | Gazebo Harmonic (gz) |
|----------------|----------------------|
| `gazebo` command | `gz sim` command |
| `libgazebo_ros_*.so` plugins | `gz-sim-*-system` plugins |
| Topics auto-bridged to ROS | Topics bridged via `ros_gz_bridge` |
| `gazebo_ros` spawn package | `ros_gz_sim` spawn package |
| SDF v1.7 | SDF v1.10 |

Gazebo Harmonic provides:
- Rigid body physics (DART, Bullet)
- Sensor simulation (LiDAR, camera, IMU, GPS)
- Plugin system for hardware emulation

### 8.2 Gz Plugins in the URDF

Gazebo Harmonic plugins are declared inside `<gazebo>` tags in the URDF. The plugin filenames follow the pattern `gz-sim-<name>-system`.

**Differential Drive Plugin:**

```xml
<gazebo>
  <plugin filename="gz-sim-diff-drive-system"
          name="gz::sim::systems::DiffDrive">
    <left_joint>base_left_wheel_joint</left_joint>
    <right_joint>base_right_wheel_joint</right_joint>
    <wheel_separation>0.45</wheel_separation>
    <wheel_radius>0.1</wheel_radius>
    <max_linear_acceleration>1.0</max_linear_acceleration>
    <max_angular_acceleration>2.0</max_angular_acceleration>
    <topic>cmd_vel</topic>
    <odom_topic>odom</odom_topic>
    <frame_id>odom</frame_id>
    <child_frame_id>base_link</child_frame_id>
    <odom_publish_frequency>30</odom_publish_frequency>
  </plugin>
</gazebo>
```

**Joint State Publisher Plugin** (needed to publish `/joint_states` from Gz):

```xml
<gazebo>
  <plugin filename="gz-sim-joint-state-publisher-system"
          name="gz::sim::systems::JointStatePublisher">
    <topic>joint_states</topic>
    <joint_name>base_left_wheel_joint</joint_name>
    <joint_name>base_right_wheel_joint</joint_name>
  </plugin>
</gazebo>
```

### 8.3 Add a LiDAR Sensor

URDF link and joint (same as before):

```xml
<link name="laser_link">
  <visual>
    <geometry><cylinder radius="0.05" length="0.04"/></geometry>
    <material name="black"><color rgba="0 0 0 1"/></material>
  </visual>
  <collision>
    <geometry><cylinder radius="0.05" length="0.04"/></geometry>
  </collision>
  <inertial>
    <mass value="0.1"/>
    <inertia ixx="0.000025" ixy="0" ixz="0"
             iyy="0.000025" iyz="0" izz="0.0000125"/>
  </inertial>
</link>

<joint name="base_laser_joint" type="fixed">
  <parent link="base_link"/>
  <child link="laser_link"/>
  <origin xyz="0.2 0 0.2" rpy="0 0 0"/>
</joint>
```

Gazebo Harmonic sensor definition (SDF v1.10 style inside `<gazebo reference>`):

```xml
<gazebo reference="laser_link">
  <sensor name="laser" type="gpu_lidar">
    <pose>0 0 0 0 0 0</pose>
    <topic>scan</topic>
    <update_rate>10</update_rate>
    <gz_frame_id>laser_link</gz_frame_id>
    <lidar>
      <scan>
        <horizontal>
          <samples>360</samples>
          <resolution>1</resolution>
          <min_angle>-3.14159</min_angle>
          <max_angle>3.14159</max_angle>
        </horizontal>
      </scan>
      <range>
        <min>0.3</min>
        <max>12.0</max>
        <resolution>0.01</resolution>
      </range>
    </lidar>
    <visualize>true</visualize>
  </sensor>
</gazebo>

<!-- Enable sensor rendering -->
<gazebo>
  <plugin filename="gz-sim-sensors-system"
          name="gz::sim::systems::Sensors">
    <render_engine>ogre2</render_engine>
  </plugin>
</gazebo>
```

### 8.4 Create a Custom World

Create `worlds/my_world.sdf` (SDF v1.10 for Gazebo Harmonic):

```xml
<?xml version="1.0" ?>
<sdf version="1.10">
  <world name="my_world">

    <!-- Physics -->
    <plugin filename="gz-sim-physics-system"
            name="gz::sim::systems::Physics"/>
    <plugin filename="gz-sim-scene-broadcaster-system"
            name="gz::sim::systems::SceneBroadcaster"/>
    <plugin filename="gz-sim-user-commands-system"
            name="gz::sim::systems::UserCommands"/>

    <!-- Lighting -->
    <light name="sun" type="directional">
      <cast_shadows>true</cast_shadows>
      <pose>0 0 10 0 0 0</pose>
      <diffuse>1 1 1 1</diffuse>
      <specular>0.5 0.5 0.5 1</specular>
      <direction>-0.5 0.1 -0.9</direction>
    </light>

    <!-- Ground plane -->
    <model name="ground_plane">
      <static>true</static>
      <link name="link">
        <collision name="collision">
          <geometry><plane><normal>0 0 1</normal></plane></geometry>
        </collision>
        <visual name="visual">
          <geometry><plane><normal>0 0 1</normal><size>100 100</size></plane></geometry>
          <material>
            <ambient>0.8 0.8 0.8 1</ambient>
            <diffuse>0.8 0.8 0.8 1</diffuse>
          </material>
        </visual>
      </link>
    </model>

    <!-- Box obstacle -->
    <model name="box_obstacle">
      <static>true</static>
      <pose>2 0 0.5 0 0 0</pose>
      <link name="link">
        <collision name="collision">
          <geometry><box><size>0.5 0.5 1.0</size></box></geometry>
        </collision>
        <visual name="visual">
          <geometry><box><size>0.5 0.5 1.0</size></box></geometry>
          <material><ambient>1 0 0 1</ambient><diffuse>1 0 0 1</diffuse></material>
        </visual>
      </link>
    </model>

  </world>
</sdf>
```

### 8.5 Bridging Gz Topics to ROS 2

Gazebo Harmonic and ROS 2 run in **separate middleware domains**. The `ros_gz_bridge` node translates between them.

```bash
# Run a one-off bridge from the terminal
ros2 run ros_gz_bridge parameter_bridge \
    /cmd_vel@geometry_msgs/msg/Twist]gz.msgs.Twist \
    /odom@nav_msgs/msg/Odometry[gz.msgs.Odometry \
    /scan@sensor_msgs/msg/LaserScan[gz.msgs.LaserScan \
    /joint_states@sensor_msgs/msg/JointState[gz.msgs.Model
```

Bridge direction syntax: `@ROS_TYPE@gz.msgs.GzType` (bidirectional), `@ROS_TYPE[gz.msgs.GzType` (Gz→ROS only), `@ROS_TYPE]gz.msgs.GzType` (ROS→Gz only).

### 8.6 Launch File — Robot in Gazebo Harmonic

```python
import os
from launch import LaunchDescription
from launch_ros.actions import Node
from launch.actions import IncludeLaunchDescription
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch.substitutions import Command
from launch_ros.parameter_descriptions import ParameterValue
from ament_index_python.packages import get_package_share_directory


def generate_launch_description():
    pkg = get_package_share_directory("my_robot_description")
    xacro_file = os.path.join(pkg, "urdf", "my_robot.urdf.xacro")
    world_file = os.path.join(pkg, "worlds", "my_world.sdf")

    robot_description = ParameterValue(
        Command(["xacro ", xacro_file]), value_type=str)

    # Start Gazebo Harmonic
    gz_sim = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(
            os.path.join(get_package_share_directory("ros_gz_sim"),
                         "launch", "gz_sim.launch.py")),
        launch_arguments={"gz_args": f"-r {world_file}"}.items(),
    )

    # Robot State Publisher
    robot_state_publisher = Node(
        package="robot_state_publisher",
        executable="robot_state_publisher",
        parameters=[{"robot_description": robot_description,
                     "use_sim_time": True}],
    )

    # Spawn robot in Gazebo
    spawn_robot = Node(
        package="ros_gz_sim",
        executable="create",
        arguments=["-name", "my_robot", "-topic", "robot_description"],
        output="screen",
    )

    # Bridge Gz ↔ ROS 2 topics
    bridge = Node(
        package="ros_gz_bridge",
        executable="parameter_bridge",
        arguments=[
            "/cmd_vel@geometry_msgs/msg/Twist]gz.msgs.Twist",
            "/odom@nav_msgs/msg/Odometry[gz.msgs.Odometry",
            "/scan@sensor_msgs/msg/LaserScan[gz.msgs.LaserScan",
            "/joint_states@sensor_msgs/msg/JointState[gz.msgs.Model",
            "/clock@rosgraph_msgs/msg/Clock[gz.msgs.Clock",
        ],
        parameters=[{"use_sim_time": True}],
    )

    return LaunchDescription([
        gz_sim,
        robot_state_publisher,
        spawn_robot,
        bridge,
    ])
```

### Activity 8
> Add a camera sensor (`type="camera"`) to your robot pointing forward. Extend the `ros_gz_bridge` to also bridge `/camera/image_raw`. Launch the robot in Gazebo Harmonic, drive it with `ros2 topic pub /cmd_vel geometry_msgs/msg/Twist "{linear: {x: 0.3}}"`, and verify the LiDAR scan and camera image appear in RViz.

---

