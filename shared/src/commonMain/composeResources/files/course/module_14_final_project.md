## Final Project

### The Course Robot: DiffBot

Build a complete simulated differential drive robot from scratch that:

1. **URDF/Xacro** — full robot description with base, wheels, caster, LiDAR, camera
2. **Gazebo** — spawns in a custom world with obstacles, physics & sensors
3. **TF tree** — correct `odom` → `base_link` → sensor frames
4. **Navigation** — responds to `/cmd_vel`, publishes `/odom`
5. **Action server** — `NavigateToPoint` driving the robot to 2D goals
6. **Lifecycle** — sensor nodes managed through configure/activate lifecycle
7. **Launch** — single launch file brings up everything (Gazebo + RViz + all nodes)
8. **Parameters** — robot dimensions and speeds configurable via YAML

### Project Structure

```
ros2_course_ws/src/
├── my_robot_interfaces/          # msg, srv, action definitions
├── my_robot_description/         # URDF/Xacro, worlds, meshes
│   ├── urdf/
│   │   ├── my_robot.urdf.xacro
│   │   ├── base.xacro
│   │   ├── wheels.xacro
│   │   ├── sensors.xacro
│   │   └── gazebo.xacro
│   └── worlds/
│       └── course_world.sdf
├── my_robot_navigation/          # action server, velocity controller
└── my_robot_bringup/             # launch files, configs, rviz
    ├── launch/
    │   ├── robot_sim.launch.py
    │   └── robot_real.launch.py
    ├── rviz/
    │   └── robot.rviz
    └── config/
        └── robot_params.yaml
```

### Project Checklist

- [ ] Robot spawns in Gazebo Harmonic (`gz sim`) without falling through the ground
- [ ] LiDAR scan visible in RViz (bridged via `ros_gz_bridge`)
- [ ] Camera image stream active on `/camera/image_raw`
- [ ] `ros2 topic pub /cmd_vel` drives the robot
- [ ] `/odom` updates while robot moves
- [ ] TF tree is complete (`view_frames` shows no broken links)
- [ ] `NavigateToPoint` action server accepts, executes with feedback, and returns result
- [ ] Lifecycle camera driver goes through full state machine
- [ ] `use_sim_time: true` set for all nodes when running in simulation
- [ ] Single launch file starts the complete system (Gazebo + bridge + RSP + RViz)

---

