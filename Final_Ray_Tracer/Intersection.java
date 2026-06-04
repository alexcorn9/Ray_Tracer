// This class is part of the ray tracer.
public class Intersection {

// Information about a ray hit.
    private double distance;
    private Vector3D position;
    private Object3D object;
    private Vector3D normal;

// Create a hit record.
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

// Return the hit distance.
    public double getDistance() {
        return distance;
    }

// Return the hit position.
    public Vector3D getPosition() {
        return position;
    }

// Return the hit object.
    public Object3D getObject() {
        return object;
    }

// Return the hit normal.
    public Vector3D getNormal() {
        return normal;
    }
}
