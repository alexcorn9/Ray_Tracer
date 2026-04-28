public class Intersection {
    private double distance; // Distance from ray origin to hit point
    private Vector3D position; // Point where ray hits object
    private Object3D object; // Object that was hit

    public Intersection(double distance, Vector3D position, Object3D object) {
        this.distance = distance;
        this.position = position;
        this.object = object;
    }

    public double getDistance() { return distance; } // Return distance t
    public Vector3D getPosition() { return position; } // Return hit point
    public Object3D getObject() { return object; } // Return hit object
}