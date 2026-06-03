import java.awt.Color;

// Abstract base class for all light types.
// Extend this to add SpotLight, AreaLight, etc. without touching the renderer.
public abstract class Light {

    private Color color;
    private double intensity;

    public Light(Color color, double intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public Color getColor()      { return color; }
    public double getIntensity() { return intensity; }

    // Direction FROM surface point TOWARD the light source (normalized).
    public abstract Vector3D getDirectionFrom(Vector3D point);

    // Maximum distance the shadow ray must travel to reach this light.
    // Directional lights return Double.MAX_VALUE.
    public abstract double getMaxShadowDistance(Vector3D point);

    // Effective intensity at a given surface point (accounts for falloff).
    public abstract double getIntensityAt(Vector3D point);
}