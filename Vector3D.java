public class Vector3D {
    private double x, y, z; // Coordinates

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; } // Get x
    public double getY() { return y; } // Get y
    public double getZ() { return z; } // Get z

    public Vector3D add(Vector3D v) { return new Vector3D(x + v.x, y + v.y, z + v.z); } // Add vectors
    public Vector3D subtract(Vector3D v) { return new Vector3D(x - v.x, y - v.y, z - v.z); } // Subtract vectors
    public Vector3D multiply(double s) { return new Vector3D(x * s, y * s, z * s); } // Multiply by scalar

    public double dotProduct(Vector3D v) { return x * v.x + y * v.y + z * v.z; } // Dot product

    public Vector3D crossProduct(Vector3D v) { // Cross product
        return new Vector3D(
            y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x
        );
    }

    public double magnitude() { return Math.sqrt(x * x + y * y + z * z); } // Vector length

    public Vector3D normalize() { // Normalize vector
        double mag = magnitude();
        if (mag == 0) return new Vector3D(0, 0, 0);
        return new Vector3D(x / mag, y / mag, z / mag);
    }
}