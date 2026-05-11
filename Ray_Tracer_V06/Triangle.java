import java.awt.Color;

public class Triangle extends Object3D {

    private Vector3D v0, v1, v2; // Triangle vertices

    // Vertex normals for phong interpolation
    private Vector3D n0, n1, n2;

    // True if triangle uses smooth shading
    private boolean hasVertexNormals;

    // Flat shading constructor
    public Triangle(
        Vector3D v0,
        Vector3D v1,
        Vector3D v2,
        Color color
    ) {

        super(v0, color);

        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        // Use same normal for all vertices
        Vector3D flatNormal = getFlatNormal();

        this.n0 = flatNormal;
        this.n1 = flatNormal;
        this.n2 = flatNormal;

        this.hasVertexNormals = false;
    }

    // Phong shading constructor
    public Triangle(
        Vector3D v0,
        Vector3D v1,
        Vector3D v2,
        Vector3D n0,
        Vector3D n1,
        Vector3D n2,
        Color color
    ) {

        super(v0, color);

        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        // Save normalized vertex normals
        this.n0 = n0.normalize();
        this.n1 = n1.normalize();
        this.n2 = n2.normalize();

        this.hasVertexNormals = true;
    }

    @Override
    public Intersection getIntersection(Ray ray) {

        double eps = 1e-6; // Small precision value

        // Triangle edges
        Vector3D edge1 = v1.subtract(v0);
        Vector3D edge2 = v2.subtract(v0);

        // Begin Möller-Trumbore algorithm
        Vector3D h =
            ray.getDirection().crossProduct(edge2);

        double a = edge1.dotProduct(h);

        // Ray parallel to triangle
        if (a > -eps && a < eps) {
            return null;
        }

        double f = 1.0 / a;

        Vector3D s =
            ray.getOrigin().subtract(v0);

        // First barycentric coordinate
        double u = f * s.dotProduct(h);

        if (u < 0 || u > 1) {
            return null;
        }

        Vector3D q = s.crossProduct(edge1);

        // Second barycentric coordinate
        double v =
            f * ray.getDirection().dotProduct(q);

        if (v < 0 || u + v > 1) {
            return null;
        }

        // Ray distance
        double t = f * edge2.dotProduct(q);

        // Valid hit
        if (t > eps) {

            Vector3D point = ray.getPoint(t);

            // Interpolate phong normal
            Vector3D normal =
                interpolateNormal(u, v);

            return new Intersection(
                t,
                point,
                this,
                normal
            );
        }

        return null;
    }

    @Override
    public Vector3D getNormal(Vector3D point) {

        // Use interpolated normal if available
        if (hasVertexNormals) {
            return interpolateNormal(point);
        }

        // Otherwise use flat normal
        return getFlatNormal();
    }

    // Interpolate normal using barycentric coordinates
    private Vector3D interpolateNormal(
        double u,
        double v
    ) {

        // Third barycentric coordinate
        double w = 1.0 - u - v;

        // Weighted average of normals
        Vector3D normal =
            n0.multiply(w)
            .add(n1.multiply(u))
            .add(n2.multiply(v))
            .normalize();

        // Flip normal towards camera
        if (normal.getZ() < 0) {
            normal = normal.multiply(-1);
        }

        return normal;
    }

    // Compute barycentric coordinates from point
    private Vector3D interpolateNormal(
        Vector3D point
    ) {

        Vector3D edge1 = v1.subtract(v0);
        Vector3D edge2 = v2.subtract(v0);

        Vector3D vp = point.subtract(v0);

        double d00 = edge1.dotProduct(edge1);
        double d01 = edge1.dotProduct(edge2);
        double d11 = edge2.dotProduct(edge2);

        double d20 = vp.dotProduct(edge1);
        double d21 = vp.dotProduct(edge2);

        double denom =
            d00 * d11 - d01 * d01;

        // Avoid division by zero
        if (denom == 0) {
            return getFlatNormal();
        }

        double v =
            (d11 * d20 - d01 * d21) / denom;

        double w =
            (d00 * d21 - d01 * d20) / denom;

        return interpolateNormal(v, w);
    }

    // Compute flat triangle normal
    private Vector3D getFlatNormal() {

        Vector3D edge1 = v1.subtract(v0);
        Vector3D edge2 = v2.subtract(v0);

        Vector3D normal =
            edge1.crossProduct(edge2).normalize();

        // Flip normal if needed
        if (normal.getZ() < 0) {
            normal = normal.multiply(-1);
        }

        return normal;
    }
}
