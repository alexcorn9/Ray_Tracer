import java.awt.Color;

public class Triangle extends Object3D {
    private Vector3D v0, v1, v2; // Triangle vertices

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Color color) {
        super(v0, color);
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        double eps = 1e-6;

        Vector3D edge1 = v1.subtract(v0);
        Vector3D edge2 = v2.subtract(v0);

        Vector3D h = ray.getDirection().crossProduct(edge2);
        double a = edge1.dotProduct(h);

        if (a > -eps && a < eps) return null;

        double f = 1.0 / a;
        Vector3D s = ray.getOrigin().subtract(v0);
        double u = f * s.dotProduct(h);

        if (u < 0 || u > 1) return null;

        Vector3D q = s.crossProduct(edge1);
        double v = f * ray.getDirection().dotProduct(q);

        if (v < 0 || u + v > 1) return null;

        double t = f * edge2.dotProduct(q);

        if (t > eps) return new Intersection(t, ray.getPoint(t), this);

        return null;
    }
}