import java.awt.Color;

public class Sphere extends Object3D {
    private double radius;

    public Sphere(Vector3D position, double radius, Color color) {
        super(position, color);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public Intersection getIntersection(Ray ray) {
        Vector3D oc = ray.getOrigin().subtract(getPosition());

        double a = ray.getDirection().dotProduct(ray.getDirection());
        double b = 2.0 * ray.getDirection().dotProduct(oc);
        double c = oc.dotProduct(oc) - (radius * radius);

        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return null;
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);

        double t1 = (-b - sqrtDiscriminant) / (2.0 * a);
        double t2 = (-b + sqrtDiscriminant) / (2.0 * a);

        double t = -1;

        if (t1 > 0 && t2 > 0) {
            t = Math.min(t1, t2);
        } else if (t1 > 0) {
            t = t1;
        } else if (t2 > 0) {
            t = t2;
        } else {
            return null;
        }

        Vector3D hitPoint = ray.getPoint(t);
        return new Intersection(t, hitPoint, this);
    }
}