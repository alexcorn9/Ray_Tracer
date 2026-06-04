// This class is used by the ray tracer.
public class Ray {
    // Ray origin and direction.
    private Vector3D origin;
    private Vector3D direction;

    // Create a normalized ray.
    public Ray(Vector3D origin, Vector3D direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public Vector3D getOrigin() { return origin; }
    public Vector3D getDirection() { return direction; }

    // Get a point along the ray.
    public Vector3D getPoint(double t) {
        return origin.add(direction.multiply(t));
    }
}
