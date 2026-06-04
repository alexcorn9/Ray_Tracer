// This class is part of the ray tracer.
public class Ray {
// Ray start and direction.
    private Vector3D origin;
    private Vector3D direction;

// Create a ray.
    public Ray(Vector3D origin, Vector3D direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

// Return the ray origin.
    public Vector3D getOrigin() { return origin; }
// Return the ray direction.
    public Vector3D getDirection() { return direction; }

// Get a point on the ray.
    public Vector3D getPoint(double t) {
        return origin.add(direction.multiply(t));
    }
}
