## Module 5 — ROS 2 Tools & Debugging

### Learning Objectives
- Use essential CLI and GUI tools
- Inspect the live node graph
- Record and replay data with rosbag2

### 5.1 Node Tools

```bash
ros2 node list                      # list running nodes
ros2 node info /my_node             # publishers, subscribers, services, params
```

### 5.2 Topic Tools

```bash
ros2 topic list -t                  # list with types
ros2 topic echo /chatter            # print messages
ros2 topic pub --once /chatter std_msgs/msg/String "data: 'hello'"
ros2 topic hz /chatter              # measure rate
ros2 topic bw /chatter              # measure bandwidth
ros2 interface show std_msgs/msg/String   # show message definition
```

### 5.3 Service Tools

```bash
ros2 service list -t
ros2 service call /add_two_ints \
    my_robot_interfaces/srv/AddTwoInts "{a: 5, b: 7}"
ros2 interface show my_robot_interfaces/srv/AddTwoInts
```

### 5.4 Parameter Tools

```bash
ros2 param list
ros2 param get /my_node my_param
ros2 param set /my_node my_param "new_value"
ros2 param dump /my_node > params.yaml
```

### 5.5 rqt — GUI Tool Suite

```bash
rqt                  # full GUI
rqt_graph            # node/topic graph visualization
rqt_plot             # plot numeric topics in real time
rqt_console          # filter and view log messages
```

### 5.6 rosbag2 — Record & Replay

```bash
# Record all topics
ros2 bag record -a -o my_recording

# Record specific topics
ros2 bag record /robot_news /odom -o my_recording

# Play back
ros2 bag play my_recording

# Inspect
ros2 bag info my_recording
```

### 5.7 Logging Levels

```python
self.get_logger().debug("Debug message")
self.get_logger().info("Info message")
self.get_logger().warn("Warning message")
self.get_logger().error("Error message")
self.get_logger().fatal("Fatal message")
```

Set level at runtime:
```bash
ros2 service call /my_node/set_logger_levels rcl_interfaces/srv/SetLoggerLevels \
    "{levels: [{name: 'my_node', level: 10}]}"
# 10=DEBUG 20=INFO 30=WARN 40=ERROR 50=FATAL
```

### Activity 5
> Start the publisher/subscriber nodes from Module 2. Use `ros2 topic hz`, `ros2 topic bw`, and `rqt_graph` to inspect the system. Record 10 seconds of data with rosbag2, then replay it and verify you see the same messages.

---

