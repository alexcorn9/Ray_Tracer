import java.awt.Color;

public class Sphere extends Object3D {

    private Vector3D center;
    private double radius;

    // Convenience constructor with plain color (matte)
    public Sphere(Vector3D center, double radius, Color color) {
        super(color);
        this.center = center;
        this.radius = radius;
    }

    // Full material constructor
    public Sphere(Vector3D center, double radius, Material material) {
        super(material);
        this.center = center;
        this.radius = radius;
    }

    public Vector3D getCenter() { return center; }
    public double   getRadius() { return radius; }

    @Override
    public Intersection getIntersection(Ray ray) {
        Vector3D oc = ray.getOrigin().subtract(center);

        double a = ray.getDirection().dotProduct(ray.getDirection());
        double b = 2.0 * ray.getDirection().dotProduct(oc);
        double c = oc.dotProduct(oc) - radius * radius;

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return null;

        double sqrt = Math.sqrt(discriminant);
        double t1   = (-b - sqrt) / (2.0 * a);
        double t2   = (-b + sqrt) / (2.0 * a);

        double t;
        if      (t1 > 0 && t2 > 0) t = Math.min(t1, t2);
        else if (t1 > 0)            t = t1;
        else if (t2 > 0)            t = t2;
        else                        return null;

        Vector3D point  = ray.getPoint(t);
        Vector3D normal = getNormal(point);
        return new Intersection(t, point, this, normal);
    }

    @Override
    public Vector3D getNormal(Vector3D point) {
        return point.subtract(center).normalize();
    }
}