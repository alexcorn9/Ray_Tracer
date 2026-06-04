import java.awt.Color;

// This class is used by the ray tracer.
public abstract class Light {

    // Light color and strength.
    private Color color;
    private double intensity;

    // Create a light.
    public Light(Color color, double intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public Color getColor()      { return color; }
    public double getIntensity() { return intensity; }

    // Direction from a point to the light.
    public abstract Vector3D getDirectionFrom(Vector3D point);

    // Distance used for shadow checks.
    public abstract double getMaxShadowDistance(Vector3D point);

    // Light strength at a point.
    public abstract double getIntensityAt(Vector3D point);
}
