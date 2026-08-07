## Module 13 — Executors & Components

### Learning Objectives
- Understand how `rclpy.spin` works internally
- Use single-threaded and multi-threaded executors
- Compose multiple nodes into one executable
- Create a ROS 2 component loadable at runtime

### 13.1 How spin() Works

`rclpy.spin(node)` is equivalent to:

```python
executor = SingleThreadedExecutor()
executor.add_node(node)
executor.spin()
```

The executor's event loop checks for pending callbacks (timer, topic, service, action) and calls them one at a time.

### 13.2 Single-Threaded vs Multi-Threaded

| Executor | Callbacks | Use When |
|----------|-----------|----------|
| `SingleThreadedExecutor` | Sequential | No blocking callbacks |
| `MultiThreadedExecutor` | Parallel | Long-running callbacks |

### 13.3 Multi-Threaded Executor

```python
from rclpy.executors import MultiThreadedExecutor
from rclpy.callback_groups import ReentrantCallbackGroup, MutuallyExclusiveCallbackGroup

class MyNode(Node):
    def __init__(self):
        super().__init__("my_node")
        # This group allows callbacks to run in parallel
        reentrant_group = ReentrantCallbackGroup()
        # This group ensures mutual exclusion
        exclusive_group = MutuallyExclusiveCallbackGroup()

        self.sub_ = self.create_subscription(
            String, "/topic", self.callback, 10,
            callback_group=reentrant_group)
        self.timer_ = self.create_timer(
            1.0, self.timer_callback,
            callback_group=exclusive_group)

def main(args=None):
    rclpy.init(args=args)
    node = MyNode()
    executor = MultiThreadedExecutor(num_threads=4)
    executor.add_node(node)
    executor.spin()
    rclpy.shutdown()
```

### 13.4 Multiple Nodes in One Executable

```python
def main(args=None):
    rclpy.init(args=args)

    node1 = RobotNewsStation()
    node2 = SmartphoneNode()

    executor = MultiThreadedExecutor()
    executor.add_node(node1)
    executor.add_node(node2)

    try:
        executor.spin()
    finally:
        executor.shutdown()
        rclpy.shutdown()
```

### 13.5 ROS 2 Components (C++)

Components allow loading nodes into a container at runtime without recompiling.

Create `src/my_component.cpp`:

```cpp
#include <chrono>
#include <memory>

#include "rclcpp/rclcpp.hpp"
#include "rclcpp_components/register_node_macro.hpp"
#include "std_msgs/msg/string.hpp"

namespace my_cpp_pkg {

class MyComponent : public rclcpp::Node {
public:
    explicit MyComponent(const rclcpp::NodeOptions& options)
        : Node("my_component", options) {
        pub_ = create_publisher<std_msgs::msg::String>("/output", 10);
        timer_ = create_wall_timer(
            std::chrono::seconds(1),
            [this]() {
                auto msg = std_msgs::msg::String();
                msg.data = "From component";
                pub_->publish(msg);
            });
    }

private:
    rclcpp::Publisher<std_msgs::msg::String>::SharedPtr pub_;
    rclcpp::TimerBase::SharedPtr timer_;
};

}  // namespace my_cpp_pkg

RCLCPP_COMPONENTS_REGISTER_NODE(my_cpp_pkg::MyComponent)
```

`CMakeLists.txt`:
```cmake
find_package(rclcpp_components REQUIRED)
find_package(std_msgs REQUIRED)

add_library(my_component SHARED src/my_component.cpp)
ament_target_dependencies(my_component rclcpp rclcpp_components std_msgs)
rclcpp_components_register_nodes(my_component "my_cpp_pkg::MyComponent")

install(TARGETS my_component
  ARCHIVE DESTINATION lib
  LIBRARY DESTINATION lib
  RUNTIME DESTINATION bin)
```

Add matching dependencies to `package.xml`:
```xml
<depend>rclcpp_components</depend>
<depend>std_msgs</depend>
```

Load at runtime:
```bash
ros2 run rclcpp_components component_container &
ros2 component load /ComponentManager my_cpp_pkg my_cpp_pkg::MyComponent
ros2 component list
ros2 component unload /ComponentManager 1
```

Load in launch file:
```python
from launch_ros.actions import ComposableNodeContainer, LoadComposableNodes
from launch_ros.descriptions import ComposableNode

container = ComposableNodeContainer(
    name="my_container",
    namespace="",
    package="rclcpp_components",
    executable="component_container",
    composable_node_descriptions=[
        ComposableNode(
            package="my_cpp_pkg",
            plugin="my_cpp_pkg::MyComponent",
            name="my_component",
        ),
    ],
)
```

### Activity 13
> Write two C++ components: `NumberPublisher` (publishes an integer every second) and `NumberCounter` (subscribes and accumulates). Load both into a single component container using a launch file. Verify only one process is running with `ps aux | grep ros2`.

---

