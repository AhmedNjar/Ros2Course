## Module 6 — TF2 & Transforms

### Learning Objectives
- Understand what TF is and why robots need it
- Broadcast and listen to static and dynamic transforms
- Visualize TF frames in RViz

### 6.1 What is TF2?

TF2 (Transform Library v2) tracks coordinate frames over time. For a robot, you need to know:
- Where is my base relative to the world? (`odom` → `base_link`)
- Where is my camera relative to my base? (`base_link` → `camera_link`)
- Where is an obstacle in the world frame?

```
world
  └── odom
        └── base_link
              ├── laser_link
              ├── camera_link
              └── left_wheel_link
                  right_wheel_link
```

### 6.2 Static Transform — Python

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from geometry_msgs.msg import TransformStamped
from tf2_ros import StaticTransformBroadcaster
import tf_transformations


class StaticFramePublisher(Node):
    def __init__(self):
        super().__init__("static_frame_publisher")
        self.broadcaster_ = StaticTransformBroadcaster(self)
        self.send_static_transform()

    def send_static_transform(self):
        t = TransformStamped()
        t.header.stamp = self.get_clock().now().to_msg()
        t.header.frame_id = "base_link"
        t.child_frame_id = "laser_link"
        t.transform.translation.x = 0.2
        t.transform.translation.y = 0.0
        t.transform.translation.z = 0.1
        q = tf_transformations.quaternion_from_euler(0, 0, 0)
        t.transform.rotation.x = q[0]
        t.transform.rotation.y = q[1]
        t.transform.rotation.z = q[2]
        t.transform.rotation.w = q[3]
        self.broadcaster_.sendTransform(t)


def main(args=None):
    rclpy.init(args=args)
    node = StaticFramePublisher()
    rclpy.spin(node)
    rclpy.shutdown()
```

### 6.3 Static Transform — CLI

```bash
# Publish a static transform from CLI (great for testing)
ros2 run tf2_ros static_transform_publisher \
    --x 0.2 --y 0.0 --z 0.1 \
    --roll 0 --pitch 0 --yaw 0 \
    --frame-id base_link --child-frame-id laser_link
```

### 6.4 Dynamic Transform Broadcaster

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from geometry_msgs.msg import TransformStamped
from tf2_ros import TransformBroadcaster
import tf_transformations


class DynamicFramePublisher(Node):
    def __init__(self):
        super().__init__("dynamic_frame_publisher")
        self.broadcaster_ = TransformBroadcaster(self)
        self.timer_ = self.create_timer(0.1, self.broadcast_transform)
        self.angle_ = 0.0

    def broadcast_transform(self):
        t = TransformStamped()
        t.header.stamp = self.get_clock().now().to_msg()
        t.header.frame_id = "odom"
        t.child_frame_id = "base_link"
        t.transform.translation.x = 1.0
        t.transform.translation.y = 0.0
        t.transform.translation.z = 0.0
        self.angle_ += 0.01
        q = tf_transformations.quaternion_from_euler(0, 0, self.angle_)
        t.transform.rotation.x, t.transform.rotation.y = q[0], q[1]
        t.transform.rotation.z, t.transform.rotation.w = q[2], q[3]
        self.broadcaster_.sendTransform(t)


def main(args=None):
    rclpy.init(args=args)
    node = DynamicFramePublisher()
    rclpy.spin(node)
    rclpy.shutdown()
```

### 6.5 TF Listener

```python
#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from tf2_ros import Buffer, TransformListener

class TFListenerNode(Node):
    def __init__(self):
        super().__init__("tf_listener")
        self.tf_buffer_ = Buffer()
        self.tf_listener_ = TransformListener(self.tf_buffer_, self)
        self.timer_ = self.create_timer(0.5, self.lookup_transform)

    def lookup_transform(self):
        try:
            t = self.tf_buffer_.lookup_transform(
                "base_link", "laser_link", rclpy.time.Time())
            self.get_logger().info(
                f"laser_link is at x={t.transform.translation.x:.2f} "
                f"relative to base_link")
        except Exception as e:
            self.get_logger().warn(str(e))


def main(args=None):
    rclpy.init(args=args)
    node = TFListenerNode()
    rclpy.spin(node)
    rclpy.shutdown()
```

### 6.6 TF CLI Tools

```bash
ros2 run tf2_tools view_frames            # generate frames.pdf
ros2 run tf2_ros tf2_echo base_link laser_link   # live transform output
```

### Activity 6
> Create a robot with three frames: `base_link`, `camera_link` (0.15 m in front, 0.2 m up), and `lidar_link` (0.0 m forward, 0.25 m up). Broadcast all transforms and verify the tree with `view_frames` and RViz.

---

