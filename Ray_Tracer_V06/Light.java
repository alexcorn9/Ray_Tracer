import java.awt.Color;

public class Light {

    // Light type constants
    public static final int DIRECTIONAL = 0;
    public static final int POINT = 1;

    private int type; // Type of light

    private Vector3D direction; // Direction for directional light
    private Vector3D position; // Position for point light

    private Color color; // Light color
    private double intensity; // Light intensity

    // Directional light constructor
    public Light(
        Vector3D direction,
        Color color,
        double intensity
    ) {

        this.type = DIRECTIONAL;

        // Normalize direction vector
        this.direction = direction.normalize();

        this.position = null;

        this.color = color;
        this.intensity = intensity;
    }

    // Point light constructor
    public Light(
        Vector3D position,
        Color color,
        double intensity,
        boolean isPointLight
    ) {

        this.type = POINT;

        this.position = position;

        this.direction = null;

        this.color = color;
        this.intensity = intensity;
    }

    // Return light type
    public int getType() {
        return type;
    }

    // Return directional light direction
    public Vector3D getDirection() {
        return direction;
    }

    // Return point light position
    public Vector3D getPosition() {
        return position;
    }

    // Return light color
    public Color getColor() {
        return color;
    }

    // Return light intensity
    public double getIntensity() {
        return intensity;
    }
}
