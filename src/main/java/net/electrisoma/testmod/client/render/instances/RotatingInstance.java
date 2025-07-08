package net.electrisoma.testmod.client.render.instances;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.ColoredLitOverlayInstance;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RotatingInstance extends ColoredLitOverlayInstance {
    public static final float SPEED_MULTIPLIER = 6f;

    public byte rotationAxisX;
    public byte rotationAxisY;
    public byte rotationAxisZ;

    public float x, y, z;

    public float rotationalSpeed;
    public float rotationOffset;

    public final Quaternionf rotation = new Quaternionf();

    private float currentAngle = 0f;

    public RotatingInstance(InstanceType<? extends RotatingInstance> type, InstanceHandle handle) {
        super(type, handle);
    }

    // Setup rotation parameters with speed and axis
    public RotatingInstance setup(float speed, Axis axis) {
        return setRotationAxis(axis)
                .setRotationalSpeed(speed * SPEED_MULTIPLIER)
                .setRotationOffset(0f);
    }

    public RotatingInstance setup(float speed, Axis axis, float offset) {
        return setRotationAxis(axis)
                .setRotationalSpeed(speed * SPEED_MULTIPLIER)
                .setRotationOffset(offset);
    }

    // Rotate instance to face a given axis (positive direction)
    public RotatingInstance rotateToFace(Axis axis) {
        Direction orientation = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        return rotateToFace(axis);
    }

    // Rotate instance from one direction to face another
    public RotatingInstance rotateToFace(Direction from, Direction orientation) {
        return rotateTo(from.getStepX(), from.getStepY(), from.getStepZ(),
                orientation.getStepX(), orientation.getStepY(), orientation.getStepZ());
    }

    public RotatingInstance rotateToFace(float stepX, float stepY, float stepZ) {
        return rotateTo(0, 1, 0, stepX, stepY, stepZ);
    }

    // Replacement for rotation.rotateTo(...) since JOML Quaternionf has no such method
    public RotatingInstance rotateTo(float fromX, float fromY, float fromZ,
                                     float toX, float toY, float toZ) {
        Vector3f from = new Vector3f(fromX, fromY, fromZ).normalize();
        Vector3f to = new Vector3f(toX, toY, toZ).normalize();

        float dot = from.dot(to);

        if (dot >= 1.0f) {
            // No rotation needed
            rotation.identity();
        } else if (dot <= -1.0f) {
            // 180 degree rotation around any perpendicular axis
            Vector3f axis = new Vector3f(1, 0, 0).cross(from);
            if (axis.lengthSquared() < 1e-6) {
                axis.set(0, 1, 0).cross(from);
            }
            axis.normalize();
            rotation.identity().rotateAxis((float) Math.PI, axis.x(), axis.y(), axis.z());
        } else {
            Vector3f axis = from.cross(to);
            float angle = (float) Math.acos(dot);
            rotation.identity().rotateAxis(angle, axis.x(), axis.y(), axis.z());
        }
        return this;
    }

    // Set rotation axis from Minecraft Axis
    public RotatingInstance setRotationAxis(Axis axis) {
        Direction orientation = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        return setRotationAxis(orientation.step());
    }

    // Set rotation axis from Vector3f
    public RotatingInstance setRotationAxis(Vector3f axis) {
        return setRotationAxis(axis.x(), axis.y(), axis.z());
    }

    // Set rotation axis with float components normalized between -1 and 1
    public RotatingInstance setRotationAxis(float rotationAxisX, float rotationAxisY, float rotationAxisZ) {
        this.rotationAxisX = (byte) (rotationAxisX * 127);
        this.rotationAxisY = (byte) (rotationAxisY * 127);
        this.rotationAxisZ = (byte) (rotationAxisZ * 127);
        return this;
    }

    // Position setters
    public RotatingInstance setPosition(Vec3i pos) {
        return setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public RotatingInstance setPosition(Vector3f pos) {
        return setPosition(pos.x(), pos.y(), pos.z());
    }

    public RotatingInstance setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    // Increment position
    public RotatingInstance nudge(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    // Set color from packed RGB int
    public RotatingInstance setColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        colorRgb(rgb);
        return this;
    }

    // Set rotational speed in degrees per second
    public RotatingInstance setRotationalSpeed(float speed) {
        this.rotationalSpeed = speed;
        return this;
    }

    // Set rotation offset in degrees
    public RotatingInstance setRotationOffset(float offset) {
        this.rotationOffset = offset;
        return this;
    }

    // Update rotation based on elapsed time
    public void updateRotation(float deltaSeconds) {
        currentAngle += rotationalSpeed * deltaSeconds;
        currentAngle %= 360f;

        rotation.identity()
                .rotateAxis((float) Math.toRadians(rotationOffset),
                        rotationAxisX / 127f, rotationAxisY / 127f, rotationAxisZ / 127f)
                .rotateAxis((float) Math.toRadians(currentAngle),
                        rotationAxisX / 127f, rotationAxisY / 127f, rotationAxisZ / 127f);
    }
}
