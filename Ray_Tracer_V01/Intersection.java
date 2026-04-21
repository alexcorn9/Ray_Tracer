public class Intersection {
    private double distance;
    private Vector3D position;
    private Object3D object;

    public Intersection(double distance, Vector3D position, Object3D object) {
        this.distance = distance;
        this.position = position;
        this.object = object;
    }

    public double getDistance() {
        return distance;
    }

    public Vector3D getPosition() {
        return position;
    }

    public Object3D getObject() {
        return object;
    }
}
