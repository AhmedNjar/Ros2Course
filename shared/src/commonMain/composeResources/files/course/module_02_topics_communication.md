## Module 2 — Topics & Communication

### Learning Objectives
- Publish and subscribe to topics in Python and C++
- Use standard message types
- Inspect topics with CLI tools

### 2.1 Publisher/Subscriber Architecture

```
Publisher Node ──[/topic_name: MsgType]──► Subscriber Node
               ──[/topic_name: MsgType]──► Subscriber Node 2
```

- Many publishers → one topic → many subscribers
- Decoupled: publisher doesn't know who subscribes
- Asynchronous: non-blocking

### 2.2 Python Publisher

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from std_msgs.msg import String


class RobotNewsStation(Node):
    def __init__(self):
        super().__init__("robot_news_station")
        self.pub_ = self.create_publisher(String, "/robot_news", 10)
        self.timer_ = self.create_timer(0.5, self.publish_news)
        self.get_logger().info("Robot News Station started")

    def publish_news(self):
        msg = String()
        msg.data = "Breaking news from " + self.get_name()
        self.pub_.publish(msg)


def main(args=None):
    rclpy.init(args=args)
    node = RobotNewsStation()
    rclpy.spin(node)
    rclpy.shutdown()
```

### 2.3 Python Subscriber

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from std_msgs.msg import String


class SmartphoneNode(Node):
    def __init__(self):
        super().__init__("smartphone")
        self.sub_ = self.create_subscription(
            String, "/robot_news", self.callback_robot_news, 10)

    def callback_robot_news(self, msg: String):
        self.get_logger().info(f"Received: {msg.data}")


def main(args=None):
    rclpy.init(args=args)
    node = SmartphoneNode()
    rclpy.spin(node)
    rclpy.shutdown()
```

Add both Python nodes to the existing `console_scripts` list in `setup.py`, and add `<depend>std_msgs</depend>` to `package.xml`:

```python
entry_points={
    "console_scripts": [
        "robot_news_station = my_py_pkg.robot_news_station:main",
        "smartphone = my_py_pkg.smartphone:main",
    ],
},
```

### 2.4 C++ Publisher

```cpp
#include <chrono>
#include <functional>
#include <memory>
#include <string>

#include "rclcpp/rclcpp.hpp"
#include "std_msgs/msg/string.hpp"

class RobotNewsStation : public rclcpp::Node {
public:
    RobotNewsStation() : Node("robot_news_station") {
        pub_ = create_publisher<std_msgs::msg::String>("/robot_news", 10);
        timer_ = create_wall_timer(
            std::chrono::milliseconds(500),
            std::bind(&RobotNewsStation::publishNews, this));
    }

private:
    void publishNews() {
        auto msg = std_msgs::msg::String();
        msg.data = "News from " + std::string(get_name());
        pub_->publish(msg);
    }

    rclcpp::Publisher<std_msgs::msg::String>::SharedPtr pub_;
    rclcpp::TimerBase::SharedPtr timer_;
};

int main(int argc, char** argv) {
    rclcpp::init(argc, argv);
    rclcpp::spin(std::make_shared<RobotNewsStation>());
    rclcpp::shutdown();
}
```

### 2.5 C++ Subscriber

```cpp
#include <functional>
#include <memory>

#include "rclcpp/rclcpp.hpp"
#include "std_msgs/msg/string.hpp"

class SmartphoneNode : public rclcpp::Node {
public:
    SmartphoneNode() : Node("smartphone") {
        sub_ = create_subscription<std_msgs::msg::String>(
            "/robot_news", 10,
            std::bind(&SmartphoneNode::callbackRobotNews, this,
                      std::placeholders::_1));
    }

private:
    void callbackRobotNews(const std_msgs::msg::String::SharedPtr msg) {
        RCLCPP_INFO(get_logger(), "Received: %s", msg->data.c_str());
    }

    rclcpp::Subscription<std_msgs::msg::String>::SharedPtr sub_;
};

int main(int argc, char** argv) {
    rclcpp::init(argc, argv);
    rclcpp::spin(std::make_shared<SmartphoneNode>());
    rclcpp::shutdown();
    return 0;
}
```

For the C++ package, add `<depend>std_msgs</depend>` to `package.xml`, then add these lines to `CMakeLists.txt`:

```cmake
find_package(std_msgs REQUIRED)

add_executable(robot_news_station src/robot_news_station.cpp)
ament_target_dependencies(robot_news_station rclcpp std_msgs)

add_executable(smartphone src/smartphone.cpp)
ament_target_dependencies(smartphone rclcpp std_msgs)

install(TARGETS
  robot_news_station
  smartphone
  DESTINATION lib/${PROJECT_NAME})
```

### 2.6 Topic CLI Commands

```bash
ros2 topic list                          # list all active topics
ros2 topic info /robot_news              # publisher/subscriber count & type
ros2 topic echo /robot_news              # print messages in terminal
ros2 topic hz /robot_news                # measure publish rate
ros2 topic bw /robot_news                # measure bandwidth
ros2 topic pub /robot_news std_msgs/msg/String "data: 'hello'"
```

### 2.7 Common Message Types

| Package | Message | Use |
|---------|---------|-----|
| `std_msgs` | `String`, `Int32`, `Float64`, `Bool` | Simple data |
| `geometry_msgs` | `Twist`, `Pose`, `Point`, `Quaternion` | Robot motion |
| `sensor_msgs` | `LaserScan`, `Image`, `Imu`, `JointState` | Sensor data |
| `nav_msgs` | `Odometry`, `Path`, `OccupancyGrid` | Navigation |

### 2.8 Custom Message (Interface)

Create a package for interfaces:
```bash
ros2 pkg create my_robot_interfaces --build-type ament_cmake
mkdir -p my_robot_interfaces/msg
```

Create `msg/HardwareStatus.msg`:
```
int64 temperature
bool are_motors_ready
string debug_message
```

Update `CMakeLists.txt`:
```cmake
find_package(rosidl_default_generators REQUIRED)
rosidl_generate_interfaces(${PROJECT_NAME}
  "msg/HardwareStatus.msg"
)
```

Add to `package.xml`:
```xml
<buildtool_depend>rosidl_default_generators</buildtool_depend>
<exec_depend>rosidl_default_runtime</exec_depend>
<member_of_group>rosidl_interface_packages</member_of_group>
```

### Activity 2
> Build a number publisher that sends integers from 1 to 100 on `/number` at 1 Hz, and a counter subscriber that accumulates the sum and publishes it on `/number_count`. Run both nodes and verify the count is increasing correctly.

---

