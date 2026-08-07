## Module 7 — URDF & Robot Modeling

### Learning Objectives
- Understand what URDF is and how it represents a robot
- Write links with visual, collision, and inertia
- Write joints (fixed, revolute, continuous, prismatic)
- View your robot in RViz with the Robot State Publisher

### 7.1 What is URDF?

URDF (Unified Robot Description Format) is an XML format for describing robot geometry, kinematics, and dynamics.

```
robot
├── link: base_link
├── link: left_wheel
├── link: right_wheel
├── joint: base_left_wheel_joint  (connects base_link → left_wheel)
└── joint: base_right_wheel_joint (connects base_link → right_wheel)
```

### 7.2 Basic URDF Structure

Create `my_robot_description/urdf/my_robot.urdf`:

```xml
<?xml version="1.0"?>
<robot name="my_robot">

  <!-- ===== BASE LINK ===== -->
  <link name="base_link">
    <visual>
      <geometry>
        <box size="0.6 0.4 0.2"/>
      </geometry>
      <origin xyz="0 0 0.1" rpy="0 0 0"/>
      <material name="blue">
        <color rgba="0 0 0.8 1"/>
      </material>
    </visual>
    <collision>
      <geometry>
        <box size="0.6 0.4 0.2"/>
      </geometry>
      <origin xyz="0 0 0.1" rpy="0 0 0"/>
    </collision>
    <inertial>
      <mass value="5.0"/>
      <origin xyz="0 0 0.1" rpy="0 0 0"/>
      <inertia ixx="0.0458" ixy="0" ixz="0"
               iyy="0.0875" iyz="0" izz="0.1208"/>
    </inertial>
  </link>

  <!-- ===== LEFT WHEEL ===== -->
  <link name="left_wheel">
    <visual>
      <geometry>
        <cylinder radius="0.1" length="0.05"/>
      </geometry>
      <origin xyz="0 0 0" rpy="1.5707 0 0"/>
      <material name="dark_grey">
        <color rgba="0.3 0.3 0.3 1"/>
      </material>
    </visual>
    <collision>
      <geometry>
        <cylinder radius="0.1" length="0.05"/>
      </geometry>
      <origin xyz="0 0 0" rpy="1.5707 0 0"/>
    </collision>
    <inertial>
      <mass value="0.5"/>
      <origin xyz="0 0 0" rpy="0 0 0"/>
      <inertia ixx="0.00058" ixy="0" ixz="0"
               iyy="0.00058" iyz="0" izz="0.00125"/>
    </inertial>
  </link>

  <!-- ===== BASE → LEFT WHEEL JOINT ===== -->
  <joint name="base_left_wheel_joint" type="continuous">
    <parent link="base_link"/>
    <child link="left_wheel"/>
    <origin xyz="-0.15 0.225 0" rpy="0 0 0"/>
    <axis xyz="0 1 0"/>
  </joint>

</robot>
```

### 7.3 Joint Types

| Type | Description | Example |
|------|-------------|---------|
| `fixed` | No movement | Camera mount |
| `continuous` | Rotates freely (no limits) | Wheel |
| `revolute` | Rotates within limits | Robotic arm joint |
| `prismatic` | Slides linearly | Elevator lift |
| `floating` | 6 DOF | Free-floating body |

### 7.4 Computing Inertia Values

For a **box** (mass m, dimensions l×w×h):
```
ixx = m/12 * (h² + w²)
iyy = m/12 * (h² + l²)
izz = m/12 * (l² + w²)
```

For a **cylinder** (mass m, radius r, length l):
```
ixx = iyy = m/12 * (3r² + l²)
izz = m/2 * r²
```

### 7.5 Robot State Publisher

The `robot_state_publisher` node:
1. Reads the URDF from the `/robot_description` parameter
2. Listens to `/joint_states` for moving joints
3. Broadcasts the full TF tree

```python
# In a launch file:
from launch_ros.parameter_descriptions import ParameterValue
from launch.substitutions import Command

robot_description = ParameterValue(
    Command(["xacro ", urdf_file_path]),
    value_type=str
)

robot_state_publisher_node = Node(
    package="robot_state_publisher",
    executable="robot_state_publisher",
    parameters=[{"robot_description": robot_description}]
)
```

### 7.6 Verify in RViz

```bash
ros2 launch my_robot_description display.launch.py
```

In RViz: add **RobotModel** display, set **Fixed Frame** to `base_link`, and add **TF** display.

### Activity 7
> Model a differential drive robot with: base box (0.5×0.3×0.15 m), two driven wheels (r=0.1 m), and one caster sphere (r=0.05 m) at the front. Add proper collision and inertia to all links. Visualize it in RViz.

---

