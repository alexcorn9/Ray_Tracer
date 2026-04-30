import java.awt.Color;

public abstract class Object3D {
    private Vector3D position; // Object position
    private Color color; // Object color

    public Object3D(Vector3D position, Color color) {
        this.position = position;
        this.color = color;
    }

    public Vector3D getPosition() { return position; } // Return position
    public Color getColor() { return color; } // Return color

    public abstract Intersection getIntersection(Ray ray); // Compute ray intersection
}