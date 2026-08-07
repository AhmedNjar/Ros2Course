## Best Practices Reference

### Naming Conventions

```
Node names:    snake_case       (robot_state_publisher)
Topic names:   /snake_case      (/joint_states)
Service names: /snake_case      (/set_led_state)
Action names:  /snake_case      (/navigate_to_point)
Package names: snake_case       (my_robot_description)
Classes:       PascalCase       (RobotStatePublisher)
```

### Node Design Principles

1. **One node = one responsibility** — don't bundle unrelated logic
2. **Parameterize** magic numbers — never hardcode frame names, topic names, or rates
3. **Use QoS profiles** — choose reliability/durability appropriate to sensor vs command data
4. **Handle missing dependencies** — check `wait_for_service` before calling
5. **Log at the right level** — `debug` for per-cycle data, `info` for state changes
6. **Avoid blocking in callbacks** — keep callbacks fast; use actions for long tasks

### Interface Design Principles

1. **Messages** — use standard types when they fit (`geometry_msgs/Twist`)
2. **Custom messages** — only when standard types don't fit your needs
3. **Services** — short, synchronous, guaranteed-response operations
4. **Actions** — anything that takes more than ~1 second or needs feedback

### Build & Package Hygiene

```bash
# Always build only what changed
colcon build --packages-select my_pkg

# Symlink install for Python development
colcon build --symlink-install

# Check interface generation
ros2 interface list | grep my_robot

# Verify no missing dependencies
rosdep install --from-paths src --ignore-src -r -y
```

---

