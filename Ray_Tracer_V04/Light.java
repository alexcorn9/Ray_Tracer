import java.awt.Color;

public class Light {
    private Vector3D direction; // Light direction
    private Color color; // Light color
    private double intensity; // Light intensity

    public Light(Vector3D direction, Color color, double intensity) {
        this.direction = direction.normalize();
        this.color = color;
        this.intensity = intensity;
    }

    public Vector3D getDirection() { return direction; } // Return light direction
    public Color getColor() { return color; } // Return light color
    public double getIntensity() { return intensity; } // Return light intensity
}
