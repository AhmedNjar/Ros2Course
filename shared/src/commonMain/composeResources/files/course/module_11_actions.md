## Module 11 — ROS 2 Actions

### Learning Objectives
- Understand the action pattern and when to use it
- Define a custom action
- Write an action server and client in Python and C++
- Implement goal policies (accept/reject, cancel)

### 11.1 Actions vs Services

| | Service | Action |
|-|---------|--------|
| Pattern | Request/Response | Goal/Feedback/Result |
| Duration | Instant | Long-running |
| Feedback | None | Continuous |
| Cancel | No | Yes |
| Use case | Get sensor value | Navigate to goal |

### 11.2 Action Definition

Create `action/NavigateToPoint.action` in your interfaces package:
```bash
mkdir -p my_robot_interfaces/action
```

```
# Goal
float64 x
float64 y
float64 tolerance
---
# Result
bool success
string message
float64 final_distance
---
# Feedback
float64 current_distance
float64 estimated_time_remaining
```

Update `CMakeLists.txt`:
```cmake
rosidl_generate_interfaces(${PROJECT_NAME}
  "msg/HardwareStatus.msg"
  "srv/AddTwoInts.srv"
  "action/NavigateToPoint.action"
)
```

### 11.3 Python Action Server

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from rclpy.action import ActionServer, GoalResponse, CancelResponse
from my_robot_interfaces.action import NavigateToPoint
import math
import time


class NavigateToPointServer(Node):
    def __init__(self):
        super().__init__("navigate_to_point_server")
        self.action_server_ = ActionServer(
            self,
            NavigateToPoint,
            "navigate_to_point",
            goal_callback=self.goal_callback,
            cancel_callback=self.cancel_callback,
            execute_callback=self.execute_callback,
        )
        self.current_x_ = 0.0
        self.current_y_ = 0.0

    def goal_callback(self, goal_request):
        self.get_logger().info(
            f"Received goal: ({goal_request.x}, {goal_request.y})")
        return GoalResponse.ACCEPT

    def cancel_callback(self, goal_handle):
        self.get_logger().info("Goal cancel requested")
        return CancelResponse.ACCEPT

    def execute_callback(self, goal_handle):
        self.get_logger().info("Executing goal...")
        goal = goal_handle.request
        feedback_msg = NavigateToPoint.Feedback()

        while True:
            if goal_handle.is_cancel_requested:
                goal_handle.canceled()
                result = NavigateToPoint.Result()
                result.success = False
                result.message = "Goal cancelled"
                return result

            dx = goal.x - self.current_x_
            dy = goal.y - self.current_y_
            dist = math.sqrt(dx**2 + dy**2)

            if dist <= goal.tolerance:
                break

            # Move towards goal (simplified)
            step = min(0.1, dist)
            self.current_x_ += step * dx / dist
            self.current_y_ += step * dy / dist

            feedback_msg.current_distance = dist
            feedback_msg.estimated_time_remaining = dist / 0.1
            goal_handle.publish_feedback(feedback_msg)
            time.sleep(0.1)

        goal_handle.succeed()
        result = NavigateToPoint.Result()
        result.success = True
        result.message = "Reached goal"
        result.final_distance = math.sqrt(
            (goal.x - self.current_x_)**2 + (goal.y - self.current_y_)**2)
        return result


def main(args=None):
    rclpy.init(args=args)
    node = NavigateToPointServer()
    rclpy.spin(node)
    rclpy.shutdown()
```

### 11.4 Python Action Client

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from rclpy.action import ActionClient
from my_robot_interfaces.action import NavigateToPoint


class NavigateToPointClient(Node):
    def __init__(self):
        super().__init__("navigate_to_point_client")
        self.client_ = ActionClient(self, NavigateToPoint, "navigate_to_point")

    def send_goal(self, x, y, tolerance=0.1):
        self.client_.wait_for_server()
        goal = NavigateToPoint.Goal()
        goal.x = x
        goal.y = y
        goal.tolerance = tolerance

        self.get_logger().info(f"Sending goal: ({x}, {y})")
        future = self.client_.send_goal_async(
            goal, feedback_callback=self.feedback_callback)
        future.add_done_callback(self.goal_response_callback)

    def goal_response_callback(self, future):
        handle = future.result()
        if not handle.accepted:
            self.get_logger().warn("Goal rejected!")
            return
        self.get_logger().info("Goal accepted")
        result_future = handle.get_result_async()
        result_future.add_done_callback(self.result_callback)

    def feedback_callback(self, feedback_msg):
        fb = feedback_msg.feedback
        self.get_logger().info(
            f"Distance remaining: {fb.current_distance:.2f} m")

    def result_callback(self, future):
        result = future.result().result
        self.get_logger().info(
            f"Result: success={result.success}, message='{result.message}'")


def main(args=None):
    rclpy.init(args=args)
    node = NavigateToPointClient()
    node.send_goal(3.0, 2.0)
    rclpy.spin(node)
    rclpy.shutdown()
```

### 11.5 Action CLI Commands

```bash
ros2 action list
ros2 action info /navigate_to_point
ros2 action send_goal /navigate_to_point \
    my_robot_interfaces/action/NavigateToPoint \
    "{x: 3.0, y: 2.0, tolerance: 0.1}"

# With feedback:
ros2 action send_goal --feedback /navigate_to_point \
    my_robot_interfaces/action/NavigateToPoint \
    "{x: 3.0, y: 2.0, tolerance: 0.1}"
```

### Activity 11
> Implement a `CountUntil` action: goal has `int64 count_until` and `float64 period`, feedback has `int64 current_count`, result has `int64 reached_count`. The server counts up every `period` seconds, and supports cancellation. Write both Python and C++ versions.

---

