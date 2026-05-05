import java.awt.Color;

public class Triangle extends Object3D {
    private Vector3D v0, v1, v2; // Triangle vertices

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Color color) {
        super(v0, color); // Use v0 as reference position
        this.v0 = v0; // First vertex
        this.v1 = v1; // Second vertex
        this.v2 = v2; // Third vertex
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        double eps = 1e-6; // Small value to avoid precision errors

        Vector3D edge1 = v1.subtract(v0); // First triangle edge
        Vector3D edge2 = v2.subtract(v0); // Second triangle edge

        Vector3D h = ray.getDirection().crossProduct(edge2); // Cross product for determinant
        double a = edge1.dotProduct(h); // Determinant value

        if (a > -eps && a < eps) return null; // Ray is parallel to triangle

        double f = 1.0 / a; // Inverse determinant
        Vector3D s = ray.getOrigin().subtract(v0); // Vector from vertex to ray origin
        double u = f * s.dotProduct(h); // First barycentric coordinate

        if (u < 0 || u > 1) return null; // Outside triangle

        Vector3D q = s.crossProduct(edge1); // Cross product for second test
        double v = f * ray.getDirection().dotProduct(q); // Second barycentric coordinate

        if (v < 0 || u + v > 1) return null; // Outside triangle

        double t = f * edge2.dotProduct(q); // Distance along ray

        if (t > eps) return new Intersection(t, ray.getPoint(t), this); // Valid intersection

        return null; // No valid hit
    }

    @Override
    public Vector3D getNormal(Vector3D point) {
        Vector3D edge1 = v1.subtract(v0); // First triangle edge
        Vector3D edge2 = v2.subtract(v0); // Second triangle edge

        Vector3D normal = edge1.crossProduct(edge2).normalize(); // Compute normal with cross product

        if (normal.getZ() < 0) { // If normal points away from camera
            normal = normal.multiply(-1); // Flip normal direction
        }

        return normal; // Return triangle normal
    }
}