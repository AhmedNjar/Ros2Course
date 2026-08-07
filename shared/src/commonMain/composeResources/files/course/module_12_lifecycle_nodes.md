## Module 12 — Lifecycle Nodes

### Learning Objectives
- Understand the lifecycle node state machine
- Implement lifecycle callbacks
- Create an initialization sequence using lifecycle nodes
- Manage nodes with the lifecycle CLI

### 12.1 Why Lifecycle Nodes?

Standard nodes start all their work in `__init__`. Lifecycle nodes have explicit states so you can:
- **Configure** hardware/resources before activating
- **Activate** only when everything is ready
- **Deactivate** cleanly without destroying the node
- **Shutdown** with proper cleanup

### 12.2 Lifecycle State Machine

```
Unconfigured
     │  configure()
     ▼
  Inactive
     │  activate()          │  cleanup()
     ▼                      │
  Active ──────────────────►│
     │  deactivate()        │
     ▼                      │
  Inactive ─────────────────┘
     │  shutdown()
     ▼
 Finalized
```

### 12.3 Python Lifecycle Node

```python
#!/usr/bin/env python3
import rclpy
from rclpy.lifecycle import LifecycleNode, State, TransitionCallbackReturn
from std_msgs.msg import String


class CameraDriverNode(LifecycleNode):
    def __init__(self):
        super().__init__("camera_driver")

    def on_configure(self, state: State) -> TransitionCallbackReturn:
        self.get_logger().info("Configuring camera...")
        self.pub_ = self.create_lifecycle_publisher(String, "/camera/status", 10)
        self.timer_ = self.create_timer(1.0, self.publish_status)
        self.timer_.cancel()        # timer inactive until activated
        return TransitionCallbackReturn.SUCCESS

    def on_activate(self, state: State) -> TransitionCallbackReturn:
        self.get_logger().info("Activating camera...")
        self.timer_.reset()         # start publishing
        return super().on_activate(state)

    def on_deactivate(self, state: State) -> TransitionCallbackReturn:
        self.get_logger().info("Deactivating camera...")
        self.timer_.cancel()
        return super().on_deactivate(state)

    def on_cleanup(self, state: State) -> TransitionCallbackReturn:
        self.get_logger().info("Cleaning up camera...")
        self.destroy_timer(self.timer_)
        self.destroy_publisher(self.pub_)
        return TransitionCallbackReturn.SUCCESS

    def on_shutdown(self, state: State) -> TransitionCallbackReturn:
        self.get_logger().info("Shutting down camera...")
        return TransitionCallbackReturn.SUCCESS

    def publish_status(self):
        msg = String()
        msg.data = "Camera OK"
        self.pub_.publish(msg)


def main(args=None):
    rclpy.init(args=args)
    node = CameraDriverNode()
    rclpy.spin(node)
    rclpy.shutdown()
```

### 12.4 Lifecycle CLI

```bash
ros2 lifecycle list /camera_driver           # list available transitions
ros2 lifecycle get /camera_driver            # get current state
ros2 lifecycle set /camera_driver configure
ros2 lifecycle set /camera_driver activate
ros2 lifecycle set /camera_driver deactivate
ros2 lifecycle set /camera_driver cleanup
ros2 lifecycle set /camera_driver shutdown
```

### 12.5 Lifecycle Manager (Auto-configure)

Use the `nav2_lifecycle_manager` or write your own:

```python
from lifecycle_msgs.srv import ChangeState
from lifecycle_msgs.msg import Transition

class LifecycleManager(Node):
    def __init__(self):
        super().__init__("lifecycle_manager")
        self.change_state_client_ = self.create_client(
            ChangeState, "/camera_driver/change_state")
        self.startup_sequence()

    def change_state(self, transition_id):
        req = ChangeState.Request()
        req.transition.id = transition_id
        self.change_state_client_.call_async(req)

    def startup_sequence(self):
        self.change_state(Transition.TRANSITION_CONFIGURE)
        # wait, then:
        self.change_state(Transition.TRANSITION_ACTIVATE)
```

### Activity 12
> Convert the `robot_news_station` node from Module 2 into a lifecycle node. In `on_configure` create the publisher, in `on_activate` start the timer, in `on_deactivate` stop the timer. Test the full configure → activate → deactivate → cleanup sequence from the CLI.

---

