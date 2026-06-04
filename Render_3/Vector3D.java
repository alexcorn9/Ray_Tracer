// This class is part of the ray tracer.
public class Vector3D {
// Vector coordinates.
    private double x, y, z;

// Create a vector.
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

// Return the x value.
    public double getX() { return x; }
// Return the y value.
    public double getY() { return y; }
// Return the z value.
    public double getZ() { return z; }

// Add another vector.
    public Vector3D add(Vector3D v) { return new Vector3D(x + v.x, y + v.y, z + v.z); }
// Subtract another vector.
    public Vector3D subtract(Vector3D v) { return new Vector3D(x - v.x, y - v.y, z - v.z); }
// Multiply by a number.
    public Vector3D multiply(double s) { return new Vector3D(x * s, y * s, z * s); }

// Calculate the dot product.
    public double dotProduct(Vector3D v) { return x * v.x + y * v.y + z * v.z; }

// Calculate the cross product.
    public Vector3D crossProduct(Vector3D v) {
        return new Vector3D(
            y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x
        );
    }

// Calculate vector length.
    public double magnitude() { return Math.sqrt(x * x + y * y + z * z); }

// Normalize the vector.
    public Vector3D normalize() {
        double mag = magnitude();
        if (mag == 0) return new Vector3D(0, 0, 0);
        return new Vector3D(x / mag, y / mag, z / mag);
    }
}
