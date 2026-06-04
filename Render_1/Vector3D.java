// This class is used by the ray tracer.
public class Vector3D {
    // Vector coordinates.
    private double x, y, z;

    // Create a new vector.
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    // Add two vectors.
    public Vector3D add(Vector3D v) { return new Vector3D(x + v.x, y + v.y, z + v.z); }
    // Subtract two vectors.
    public Vector3D subtract(Vector3D v) { return new Vector3D(x - v.x, y - v.y, z - v.z); }
    // Scale the vector.
    public Vector3D multiply(double s) { return new Vector3D(x * s, y * s, z * s); }

    // Dot product between vectors.
    public double dotProduct(Vector3D v) { return x * v.x + y * v.y + z * v.z; }

    // Cross product for normals.
    public Vector3D crossProduct(Vector3D v) {
        return new Vector3D(
            y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x
        );
    }

    // Length of the vector.
    public double magnitude() { return Math.sqrt(x * x + y * y + z * z); }

    // Convert to unit length.
    public Vector3D normalize() {
        double mag = magnitude();
        if (mag == 0) return new Vector3D(0, 0, 0);
        return new Vector3D(x / mag, y / mag, z / mag);
    }
}
