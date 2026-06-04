// This class is used by the ray tracer.
public class Intersection {

    // Hit information.
    private double distance;
    private Vector3D position;
    private Object3D object;
    private Vector3D normal;

    // Create an intersection record.
    public Intersection(
        double distance,
        Vector3D position,
        Object3D object
    ) {

        this(distance, position, object, null);
    }

    public Intersection(
        double distance,
        Vector3D position,
        Object3D object,
        Vector3D normal
    ) {

        this.distance = distance;
        this.position = position;
        this.object = object;
        this.normal = normal;
    }

    // Distance to the hit.
    public double getDistance() {
        return distance;
    }

    // Position of the hit.
    public Vector3D getPosition() {
        return position;
    }

    // Object that was hit.
    public Object3D getObject() {
        return object;
    }

    // Normal at the hit.
    public Vector3D getNormal() {
        return normal;
    }
}
