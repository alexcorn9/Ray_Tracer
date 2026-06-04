import java.awt.Color;

// This class is part of the ray tracer.
public class PointLight extends Light {

// Light position and falloff.
    private Vector3D position;
    private double falloffPower;

// Create a normal point light.
    public PointLight(Vector3D position, Color color, double intensity) {
        this(position, color, intensity, 2.0);
    }

// Create a point light with custom falloff.
    public PointLight(Vector3D position, Color color, double intensity, double falloffPower) {
        super(color, intensity);
        this.position = position;
        this.falloffPower = falloffPower;
    }

// Return the light position.
    public Vector3D getPosition() { return position; }

    @Override
// Direction from a point to the light.
    public Vector3D getDirectionFrom(Vector3D point) {
        return position.subtract(point).normalize();
    }

    @Override
// Distance to this light.
    public double getMaxShadowDistance(Vector3D point) {
        return position.subtract(point).magnitude();
    }

    @Override
// Calculate falloff intensity.
    public double getIntensityAt(Vector3D point) {
        double d = position.subtract(point).magnitude();
        if (d < 1e-6) return getIntensity();
        return getIntensity() / Math.pow(d, falloffPower);
    }
}
