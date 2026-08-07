## Quick Reference Cheat Sheet

### Node Lifecycle

```bash
ros2 run <pkg> <exec>                           # run a node
ros2 run <pkg> <exec> --ros-args -r /old:=/new  # remap
ros2 run <pkg> <exec> --ros-args -p name:=value # set param
ros2 run <pkg> <exec> --ros-args --params-file f.yaml
ros2 launch <pkg> <launch_file> key:=value
```

### Introspection

```bash
ros2 node list / info <name>
ros2 topic list / echo / hz / pub
ros2 service list / call
ros2 action list / send_goal
ros2 param list / get / set / dump
ros2 interface show <type>
ros2 bag record / play / info
rqt_graph
```

### TF

```bash
ros2 run tf2_tools view_frames
ros2 run tf2_ros tf2_echo <parent> <child>
ros2 run tf2_ros static_transform_publisher ...
```

### Lifecycle

```bash
ros2 lifecycle get / list / set <node> <transition>
```

### Components

```bash
ros2 component list
ros2 component load /Container <pkg> <plugin>
ros2 component unload /Container <id>
```

### Gazebo Harmonic (gz)

```bash
gz sim                          # open Gazebo with empty world
gz sim -r my_world.sdf          # open and run a world
gz sim --help

gz topic list                   # list Gz topics (separate from ROS)
gz topic echo /scan             # echo a Gz topic

# Bridge a topic between Gz and ROS 2
ros2 run ros_gz_bridge parameter_bridge \
    /scan@sensor_msgs/msg/LaserScan[gz.msgs.LaserScan

# Spawn model into running Gz session
ros2 run ros_gz_sim create -name my_robot -topic robot_description
```

---

## Resources

| Resource | Link |
|----------|------|
| ROS 2 Jazzy Docs | https://docs.ros.org/en/jazzy |
| ROS 2 Jazzy Tutorials | https://docs.ros.org/en/jazzy/Tutorials.html |
| Gazebo Harmonic Docs | https://gazebosim.org/docs/harmonic |
| ros_gz Bridge Guide | https://github.com/gazebosim/ros_gz/tree/jazzy |
| ROS 2 Design | https://design.ros2.org |
| ROS Discourse | https://discourse.ros.org |
| Robotics Stack Exchange | https://robotics.stackexchange.com |
| URDF Tutorials | https://docs.ros.org/en/jazzy/Tutorials/Intermediate/URDF |

---

*Course version 1.0 — ROS 2 Jazzy Jalisco / Gazebo Harmonic*
