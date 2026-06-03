// Optical camera — position and clipping planes only.
// Resolution does NOT belong here; use RenderSettings for that.
public class Camera {

    private Vector3D position; // Camera position in world space
    private double near;       // Near clipping plane distance
    private double far;        // Far  clipping plane distance

    public Camera(Vector3D position) {
        this(position, 0.1, 100.0);
    }

    public Camera(Vector3D position, double near, double far) {
        this.position = position;
        this.near     = near;
        this.far      = far;
    }

    public Vector3D getPosition() { return position; }
    public double   getNear()     { return near; }
    public double   getFar()      { return far; }
}