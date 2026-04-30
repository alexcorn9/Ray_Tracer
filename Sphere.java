import java.awt.Color;

public class Sphere extends Object3D {
    private double radius; // Sphere radius

    public Sphere(Vector3D position, double radius, Color color) {
        super(position, color);
        this.radius = radius;
    }

    public double getRadius() { return radius; } // Return radius

    @Override
    public Intersection getIntersection(Ray ray) {
        Vector3D oc = ray.getOrigin().subtract(getPosition()); // Vector from center to ray

        double a = ray.getDirection().dotProduct(ray.getDirection());
        double b = 2.0 * ray.getDirection().dotProduct(oc);
        double c = oc.dotProduct(oc) - radius * radius;

        double discriminant = b * b - 4 * a * c; // Quadratic formula

        if (discriminant < 0) return null; // No intersection

        double sqrt = Math.sqrt(discriminant);

        double t1 = (-b - sqrt) / (2.0 * a);
        double t2 = (-b + sqrt) / (2.0 * a);

        double t;

        if (t1 > 0 && t2 > 0) t = Math.min(t1, t2);
        else if (t1 > 0) t = t1;
        else if (t2 > 0) t = t2;
        else return null;

        return new Intersection(t, ray.getPoint(t), this); // Return hit
    }
}