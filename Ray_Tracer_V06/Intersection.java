public class Intersection {

    private double distance; // Distance from ray origin to hit point
    private Vector3D position; // Point where ray hits object
    private Object3D object; // Object that was hit
    private Vector3D normal; // Interpolated normal for phong shading

    // Constructor without custom normal
    public Intersection(
        double distance,
        Vector3D position,
        Object3D object
    ) {

        this(distance, position, object, null);
    }

    // Constructor with interpolated normal
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

    public double getDistance() {
        return distance;
    } // Return distance t

    public Vector3D getPosition() {
        return position;
    } // Return hit point

    public Object3D getObject() {
        return object;
    } // Return hit object

    public Vector3D getNormal() {
        return normal;
    } // Return interpolated normal
}
