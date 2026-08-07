## Module 9 — Xacro & Advanced URDF

### Learning Objectives
- Refactor URDF with Xacro properties and macros
- Use Xacro includes for modular robot descriptions
- Understand best practices for maintainable robot descriptions

### 9.1 Why Xacro?

Raw URDF repeats values and is hard to maintain. Xacro is a macro language that extends XML:
- **Properties** — named constants
- **Macros** — reusable blocks (like functions)
- **Math expressions** — `${pi/2}`, `${0.1 * 2}`
- **Conditionals** — `<xacro:if value="${use_sim}"/>`
- **Include** — split into multiple files

### 9.2 Rename URDF to Xacro

Rename `my_robot.urdf` → `my_robot.urdf.xacro` and add the XML namespace:

```xml
<?xml version="1.0"?>
<robot name="my_robot" xmlns:xacro="http://www.ros.org/wiki/xacro">
```

### 9.3 Properties

```xml
<xacro:property name="base_length" value="0.6"/>
<xacro:property name="base_width"  value="0.4"/>
<xacro:property name="base_height" value="0.2"/>
<xacro:property name="wheel_radius" value="0.1"/>
<xacro:property name="wheel_length" value="0.05"/>

<link name="base_link">
  <visual>
    <geometry>
      <box size="${base_length} ${base_width} ${base_height}"/>
    </geometry>
  </visual>
</link>
```

### 9.4 Math Expressions

```xml
<xacro:property name="pi" value="3.14159265"/>

<origin xyz="0 0 0" rpy="${pi/2} 0 0"/>
<origin xyz="${-base_length/2 + 0.1} 0 0"/>
```

### 9.5 Macros — Reusable Links

```xml
<!-- Define the wheel macro -->
<xacro:macro name="wheel_link" params="name">
  <link name="${name}">
    <visual>
      <geometry>
        <cylinder radius="${wheel_radius}" length="${wheel_length}"/>
      </geometry>
      <origin xyz="0 0 0" rpy="${pi/2} 0 0"/>
      <material name="dark_grey"><color rgba="0.3 0.3 0.3 1"/></material>
    </visual>
    <collision>
      <geometry>
        <cylinder radius="${wheel_radius}" length="${wheel_length}"/>
      </geometry>
      <origin xyz="0 0 0" rpy="${pi/2} 0 0"/>
    </collision>
    <inertial>
      <mass value="0.5"/>
      <inertia ixx="0.00058" ixy="0" ixz="0"
               iyy="0.00058" iyz="0" izz="0.00125"/>
    </inertial>
  </link>
</xacro:macro>

<!-- Use the macro -->
<xacro:wheel_link name="left_wheel"/>
<xacro:wheel_link name="right_wheel"/>
```

### 9.6 Macros with Joint

```xml
<xacro:macro name="wheel_joint" params="name parent x_offset y_offset">
  <joint name="${name}_joint" type="continuous">
    <parent link="${parent}"/>
    <child link="${name}"/>
    <origin xyz="${x_offset} ${y_offset} 0" rpy="0 0 0"/>
    <axis xyz="0 1 0"/>
  </joint>
</xacro:macro>

<xacro:wheel_joint name="left_wheel"  parent="base_link"
                   x_offset="-0.15" y_offset="0.225"/>
<xacro:wheel_joint name="right_wheel" parent="base_link"
                   x_offset="-0.15" y_offset="-0.225"/>
```

### 9.7 Include Files

Split into multiple files for organization:
```xml
<!-- my_robot.urdf.xacro — main file -->
<xacro:include filename="$(find my_robot_description)/urdf/base.xacro"/>
<xacro:include filename="$(find my_robot_description)/urdf/wheels.xacro"/>
<xacro:include filename="$(find my_robot_description)/urdf/sensors.xacro"/>
<xacro:include filename="$(find my_robot_description)/urdf/gazebo.xacro"/>
```

### 9.8 Conditional Gazebo/Sim Tags

Use Xacro arguments to toggle Gz plugins so the same Xacro works with and without simulation:

```xml
<xacro:arg name="use_sim" default="false"/>
<xacro:property name="use_sim_" value="$(arg use_sim)"/>

<xacro:if value="${use_sim_}">
  <!-- Only included when use_sim:=true -->
  <gazebo>
    <plugin filename="gz-sim-diff-drive-system"
            name="gz::sim::systems::DiffDrive">
      <left_joint>base_left_wheel_joint</left_joint>
      <right_joint>base_right_wheel_joint</right_joint>
      <wheel_separation>${base_width + wheel_length}</wheel_separation>
      <wheel_radius>${wheel_radius}</wheel_radius>
      <topic>cmd_vel</topic>
    </plugin>
  </gazebo>
</xacro:if>
```

Pass the argument when processing:
```bash
xacro my_robot.urdf.xacro use_sim:=true > my_robot_sim.urdf
```

Process Xacro file:
```bash
xacro my_robot.urdf.xacro > my_robot.urdf          # one-shot
xacro my_robot.urdf.xacro use_sim:=true > out.urdf  # with args
```

### Activity 9
> Refactor your Module 7 URDF into Xacro. Use properties for all dimensions, a `wheel` macro to avoid repetition, and separate files for base/wheels/sensors/gazebo. Verify the generated URDF is identical to the original.

---

