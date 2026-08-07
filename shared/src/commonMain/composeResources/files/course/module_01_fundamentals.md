## Module 1 — ROS 2 Fundamentals

### Learning Objectives
- Understand the ROS 2 architecture (DDS, nodes, graph)
- Create a ROS 2 package in Python and C++
- Write, build, and run your first node

### 1.1 What is ROS 2?

ROS 2 is a middleware framework for robot software. It provides:
- **Communication** between processes (nodes) via topics, services, and actions
- **Hardware abstraction** through a driver ecosystem
- **Tools** for visualization, debugging, and simulation
- **Build system** (colcon + ament) for managing packages

Key differences from ROS 1:
| ROS 1 | ROS 2 |
|-------|-------|
| roscore required | No master process |
| TCP/IP custom | DDS (Data Distribution Service) |
| Python 2/3 mixed | Python 3.12 (Jazzy) |
| No security | SROS2 security |
| Ubuntu 20.04 max | Ubuntu 24.04 (Jazzy) |

### 1.2 ROS 2 Graph Concepts

```
┌──────────┐   topic /cmd_vel   ┌──────────────┐
│ Teleop   ├──────────────────► │ Robot Driver │
│ Node     │                    │ Node         │
└──────────┘                    └──────┬───────┘
                                       │ topic /odom
                                       ▼
                                ┌──────────────┐
                                │ Nav Stack    │
                                │ Node         │
                                └──────────────┘
```

- **Node**: a single executable that performs one logical task
- **Topic**: named bus for streaming data (publisher → subscriber)
- **Service**: synchronous request/response call
- **Action**: asynchronous goal with feedback and result
- **Parameter**: configurable value stored per-node

### 1.3 Create a Python Package

```bash
cd ~/ros2_course_ws/src
ros2 pkg create --build-type ament_python my_py_pkg --dependencies rclpy
```

Package structure:
```
my_py_pkg/
├── my_py_pkg/
│   ├── __init__.py
│   └── my_first_node.py   ← your code goes here
├── package.xml
├── resource/
│   └── my_py_pkg
├── setup.cfg
└── setup.py
```

### 1.4 Your First Python Node

Create `my_py_pkg/my_py_pkg/my_first_node.py`:

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node


class MyFirstNode(Node):
    def __init__(self):
        super().__init__("my_first_node")
        self.get_logger().info("Hello from my first ROS 2 node!")
        self.counter = 0
        self.create_timer(1.0, self.timer_callback)

    def timer_callback(self):
        self.counter += 1
        self.get_logger().info(f"Timer fired {self.counter} times")


def main(args=None):
    rclpy.init(args=args)
    node = MyFirstNode()
    rclpy.spin(node)
    rclpy.shutdown()


if __name__ == "__main__":
    main()
```

Register the entry point in `setup.py`:

```python
entry_points={
    "console_scripts": [
        "my_first_node = my_py_pkg.my_first_node:main",
    ],
},
```

### 1.5 Your First C++ Package

```bash
cd ~/ros2_course_ws/src
ros2 pkg create --build-type ament_cmake my_cpp_pkg --dependencies rclcpp
```

Create `my_cpp_pkg/src/my_first_node.cpp`:

```cpp
#include <chrono>
#include <functional>
#include <memory>

#include "rclcpp/rclcpp.hpp"

class MyFirstNode : public rclcpp::Node {
public:
    MyFirstNode() : Node("my_first_node"), counter_(0) {
        RCLCPP_INFO(get_logger(), "Hello from my first C++ ROS 2 node!");
        timer_ = create_wall_timer(
            std::chrono::seconds(1),
            std::bind(&MyFirstNode::timerCallback, this));
    }

private:
    void timerCallback() {
        counter_++;
        RCLCPP_INFO(get_logger(), "Timer fired %d times", counter_);
    }

    rclcpp::TimerBase::SharedPtr timer_;
    int counter_;
};

int main(int argc, char** argv) {
    rclcpp::init(argc, argv);
    auto node = std::make_shared<MyFirstNode>();
    rclcpp::spin(node);
    rclcpp::shutdown();
    return 0;
}
```

Update `CMakeLists.txt`:

```cmake
add_executable(my_first_node src/my_first_node.cpp)
ament_target_dependencies(my_first_node rclcpp)
install(TARGETS my_first_node DESTINATION lib/${PROJECT_NAME})
```

### 1.6 Build and Run

```bash
cd ~/ros2_course_ws
colcon build --packages-select my_py_pkg my_cpp_pkg
source install/setup.bash

ros2 run my_py_pkg my_first_node
# In another terminal:
ros2 run my_cpp_pkg my_first_node
```

### Activity 1
> Create a node called `robot_news_station` that publishes a "news ticker" string every 0.5 seconds using a timer. The string should include the node name and a counter. Build and run it, verify the output with `ros2 node list` and `ros2 node info /robot_news_station`.

---

