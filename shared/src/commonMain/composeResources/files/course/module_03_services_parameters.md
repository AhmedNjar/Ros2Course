## Module 3 — Services & Parameters

### Learning Objectives
- Write service servers and clients in Python and C++
- Declare, get, and set node parameters
- Use the parameter CLI

### 3.1 Services vs Topics

| | Topic | Service |
|-|-------|---------|
| Pattern | Publish / Subscribe | Request / Response |
| Direction | One-way | Two-way |
| Timing | Async, continuous | Sync, on-demand |
| Use case | Sensor streams, commands | Configuration, one-off actions |

### 3.2 Custom Service Definition

Create `srv/AddTwoInts.srv` in your interfaces package:
```bash
mkdir -p my_robot_interfaces/srv
```

```
int64 a
int64 b
---
int64 sum
```

Register it in `CMakeLists.txt`:
```cmake
rosidl_generate_interfaces(${PROJECT_NAME}
  "msg/HardwareStatus.msg"
  "srv/AddTwoInts.srv"
)
```

### 3.3 Python Service Server

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from my_robot_interfaces.srv import AddTwoInts


class AddTwoIntsServer(Node):
    def __init__(self):
        super().__init__("add_two_ints_server")
        self.server_ = self.create_service(
            AddTwoInts, "add_two_ints", self.callback_add_two_ints)
        self.get_logger().info("Add Two Ints server started")

    def callback_add_two_ints(self, request, response):
        response.sum = request.a + request.b
        self.get_logger().info(f"{request.a} + {request.b} = {response.sum}")
        return response


def main(args=None):
    rclpy.init(args=args)
    rclpy.spin(AddTwoIntsServer())
    rclpy.shutdown()
```

### 3.4 Python Service Client

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from my_robot_interfaces.srv import AddTwoInts
from functools import partial


class AddTwoIntsClient(Node):
    def __init__(self):
        super().__init__("add_two_ints_client")
        self.call_add_two_ints(3, 4)

    def call_add_two_ints(self, a, b):
        client = self.create_client(AddTwoInts, "add_two_ints")
        while not client.wait_for_service(timeout_sec=1.0):
            self.get_logger().warn("Waiting for server...")

        request = AddTwoInts.Request()
        request.a = a
        request.b = b
        future = client.call_async(request)
        future.add_done_callback(partial(self.callback_call, a=a, b=b))

    def callback_call(self, future, a, b):
        response = future.result()
        self.get_logger().info(f"{a} + {b} = {response.sum}")


def main(args=None):
    rclpy.init(args=args)
    rclpy.spin(AddTwoIntsClient())
    rclpy.shutdown()
```

### 3.5 C++ Service Server

```cpp
#include <cinttypes>
#include <functional>
#include <memory>

#include "rclcpp/rclcpp.hpp"
#include "my_robot_interfaces/srv/add_two_ints.hpp"

class AddTwoIntsServer : public rclcpp::Node {
public:
    AddTwoIntsServer() : Node("add_two_ints_server") {
        server_ = create_service<my_robot_interfaces::srv::AddTwoInts>(
            "add_two_ints",
            std::bind(&AddTwoIntsServer::callbackAddTwoInts, this,
                      std::placeholders::_1, std::placeholders::_2));
    }

private:
    void callbackAddTwoInts(
        const my_robot_interfaces::srv::AddTwoInts::Request::SharedPtr req,
        const my_robot_interfaces::srv::AddTwoInts::Response::SharedPtr res) {
        res->sum = req->a + req->b;
        RCLCPP_INFO(
            get_logger(), "%" PRId64 " + %" PRId64 " = %" PRId64,
            req->a, req->b, res->sum);
    }

    rclcpp::Service<my_robot_interfaces::srv::AddTwoInts>::SharedPtr server_;
};

int main(int argc, char** argv) {
    rclcpp::init(argc, argv);
    rclcpp::spin(std::make_shared<AddTwoIntsServer>());
    rclcpp::shutdown();
    return 0;
}
```

### 3.6 Service CLI Commands

```bash
ros2 service list                                    # list all services
ros2 service type /add_two_ints                      # get type
ros2 service call /add_two_ints my_robot_interfaces/srv/AddTwoInts "{a: 3, b: 4}"
```

### 3.7 Parameters

Parameters are named values stored per-node. Use them to make nodes configurable without recompilation.

**Python — declare and use parameters:**

```python
class MyNode(Node):
    def __init__(self):
        super().__init__("my_node")
        self.declare_parameter("robot_name", "my_robot")
        self.declare_parameter("move_speed", 1.5)
        name = self.get_parameter("robot_name").value
        speed = self.get_parameter("move_speed").value
        self.get_logger().info(f"Robot: {name}, Speed: {speed}")
```

**C++ — declare and use parameters:**

```cpp
MyNode() : Node("my_node") {
    declare_parameter("robot_name", "my_robot");
    declare_parameter("move_speed", 1.5);
    auto name = get_parameter("robot_name").as_string();
    auto speed = get_parameter("move_speed").as_double();
    RCLCPP_INFO(get_logger(), "Robot: %s, Speed: %.1f", name.c_str(), speed);
}
```

**Parameter CLI commands:**

```bash
ros2 param list /my_node
ros2 param get /my_node robot_name
ros2 param set /my_node robot_name "robo_007"
ros2 param dump /my_node                    # dump to YAML
ros2 param load /my_node params.yaml       # load from YAML
```

**Parameter file `params.yaml`:**

```yaml
my_node:
  ros__parameters:
    robot_name: "robo_007"
    move_speed: 2.0
```

Load at launch:
```bash
ros2 run my_pkg my_node --ros-args --params-file params.yaml
```

### Activity 3
> Create a `led_panel` service server that maintains a list of 3 LEDs (on/off). Accept a `SetLed` service call with `int64 led_number` and `bool state`, returning `bool success` and `string message`. Test it from the CLI.

---

