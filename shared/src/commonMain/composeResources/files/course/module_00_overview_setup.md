## 1. Course Overview

### What You Will Build
By the end of this course you will have built a **complete simulated mobile robot** that:
- Moves autonomously using velocity commands
- Publishes sensor data (LiDAR, camera)
- Responds to service calls and action goals
- Is fully visualized in RViz
- Runs inside a custom Gazebo world

### Course Structure
| Module | Topic | Language | Estimated Time |
|--------|-------|----------|----------------|
| 1 | ROS 2 Fundamentals & Nodes | Python + C++ | 3 h |
| 2 | Topics & Communication | Python + C++ | 4 h |
| 3 | Services & Parameters | Python + C++ | 3 h |
| 4 | Launch Files & Workspaces | XML + Python | 3 h |
| 5 | ROS 2 Tools | CLI | 2 h |
| 6 | TF2 & Transforms | Python + C++ | 3 h |
| 7 | URDF & Robot Modeling | XML/URDF | 4 h |
| 8 | Gazebo Simulation | Gazebo | 4 h |
| 9 | Xacro & Advanced URDF | Xacro | 3 h |
| 10 | RViz & Visualization | RViz | 2 h |
| 11 | ROS 2 Actions | Python + C++ | 4 h |
| 12 | Lifecycle Nodes | Python + C++ | 3 h |
| 13 | Executors & Components | C++ | 3 h |
| Final | Complete Robot Project | Mixed | 5 h |

---

## 2. Environment Setup

### 2.1 Install Ubuntu 24.04
Use a native install, VM (VirtualBox/VMware), or WSL2 on Windows.

> Ubuntu 24.04 Noble Numbat is the required OS for ROS 2 Jazzy. Ubuntu 22.04 is **not** supported.

### 2.2 Install ROS 2 Jazzy

```bash
# Set locale
sudo apt update && sudo apt install locales
sudo locale-gen en_US en_US.UTF-8
sudo update-locale LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8

# Enable the Ubuntu Universe repository
sudo apt install software-properties-common
sudo add-apt-repository universe

# Add ROS 2 apt repository
sudo apt install curl
sudo curl -sSL https://raw.githubusercontent.com/ros/rosdistro/master/ros.key \
  -o /usr/share/keyrings/ros-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/ros-archive-keyring.gpg] \
  http://packages.ros.org/ros2/ubuntu $(. /etc/os-release && echo $UBUNTU_CODENAME) main" \
  | sudo tee /etc/apt/sources.list.d/ros2.list

# Install ROS 2 Jazzy
sudo apt update && sudo apt upgrade
sudo apt install ros-jazzy-desktop
sudo apt install ros-dev-tools

# Source ROS 2 in every shell (add to ~/.bashrc)
echo "source /opt/ros/jazzy/setup.bash" >> ~/.bashrc
source ~/.bashrc
```

### 2.3 Verify Installation

```bash
ros2 doctor --report    # should report ROS_DISTRO=jazzy
ros2 run demo_nodes_py talker &
ros2 run demo_nodes_py listener
```

### 2.4 Install Gazebo Harmonic & Additional Packages

ROS 2 Jazzy officially pairs with **Gazebo Harmonic** (the new-generation Gazebo, formerly Ignition). It is a separate install from ROS 2.

```bash
# Install Gazebo Harmonic
sudo apt install ros-jazzy-ros-gz

# Additional ROS 2 tools
sudo apt install ros-jazzy-joint-state-publisher-gui
sudo apt install ros-jazzy-xacro
sudo apt install ros-jazzy-tf2-tools
sudo apt install ros-jazzy-tf-transformations
sudo apt install python3-colcon-common-extensions
sudo apt install python3-transforms3d        # needed for tf_transformations
```

> **Gazebo name change:** Starting from Gazebo Fortress the "Ignition" name was dropped. What was "Ignition Gazebo" is now just "Gazebo" (or "gz"). The classic `gazebo` simulator is end-of-life and is **not** used in this course.

### 2.5 Create Your Course Workspace

```bash
mkdir -p ~/ros2_course_ws/src
cd ~/ros2_course_ws
colcon build
source install/setup.bash
echo "source ~/ros2_course_ws/install/setup.bash" >> ~/.bashrc
```

---

