import java.awt.Color;

// This class is used by the ray tracer.
public class Sphere extends Object3D {

    // Sphere position and size.
    private Vector3D center;
    private double radius;

    // Create a matte sphere.
    public Sphere(Vector3D center, double radius, Color color) {
        super(color);
        this.center = center;
        this.radius = radius;
    }

    // Create a sphere with a material.
    public Sphere(Vector3D center, double radius, Material material) {
        super(material);
        this.center = center;
        this.radius = radius;
    }

    public Vector3D getCenter() { return center; }
    public double   getRadius() { return radius; }

    @Override
    // Check if a ray hits the sphere.
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
    // Sphere normal at a point.
    public Vector3D getNormal(Vector3D point) {
        return point.subtract(center).normalize();
    }
}
