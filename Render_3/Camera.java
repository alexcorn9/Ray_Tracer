// This class is part of the ray tracer.
public class Camera {

// Camera position and clipping range.
    private Vector3D position;
    private double near;
    private double far;

// Create a camera with default clipping.
    public Camera(Vector3D position) {
        this(position, 0.1, 100.0);
    }

// Create a camera with custom clipping.
    public Camera(Vector3D position, double near, double far) {
        this.position = position;
        this.near     = near;
        this.far      = far;
    }

// Return camera position.
    public Vector3D getPosition() { return position; }
    public double   getNear()     { return near; }
    public double   getFar()      { return far; }
}
