import java.awt.Color;

// Abstract base class for all light types.
public abstract class Light {

    private Color color;
    private double intensity;

    public Light(Color color, double intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public Color getColor()      { return color; }
    public double getIntensity() { return intensity; }

    public abstract Vector3D getDirectionFrom(Vector3D point);

    // Directional lights return Double.MAX_VALUE.
    public abstract double getMaxShadowDistance(Vector3D point);

    public abstract double getIntensityAt(Vector3D point);
}