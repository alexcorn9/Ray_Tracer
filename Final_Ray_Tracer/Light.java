import java.awt.Color;

// This class is part of the ray tracer.
public abstract class Light {

// Light color and intensity.
    private Color color;
    private double intensity;

// Create a light.
    public Light(Color color, double intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public Color getColor()      { return color; }
    public double getIntensity() { return intensity; }

// Get direction from a point to the light.
    public abstract Vector3D getDirectionFrom(Vector3D point);

// Get the max shadow distance.
    public abstract double getMaxShadowDistance(Vector3D point);

// Get light intensity at a point.
    public abstract double getIntensityAt(Vector3D point);
}
