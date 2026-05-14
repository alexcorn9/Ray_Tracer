public class Ray {
    private Vector3D origin; // Ray starting point
    private Vector3D direction; // Ray direction

    public Ray(Vector3D origin, Vector3D direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public Vector3D getOrigin() { return origin; } // Return origin
    public Vector3D getDirection() { return direction; } // Return direction

    public Vector3D getPoint(double t) { // Get point along ray
        return origin.add(direction.multiply(t));
    }
}