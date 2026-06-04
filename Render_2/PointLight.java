import java.awt.Color;

public class PointLight extends Light {

    private Vector3D position;
    private double falloffPower; // Typically 2.0 for physically correct falloff

    // Default d^2 falloff
    public PointLight(Vector3D position, Color color, double intensity) {
        this(position, color, intensity, 2.0);
    }

    public PointLight(Vector3D position, Color color, double intensity, double falloffPower) {
        super(color, intensity);
        this.position = position;
        this.falloffPower = falloffPower;
    }

    public Vector3D getPosition() { return position; }

    @Override
    public Vector3D getDirectionFrom(Vector3D point) {
        return position.subtract(point).normalize();
    }

    @Override
    public double getMaxShadowDistance(Vector3D point) {
        return position.subtract(point).magnitude();
    }

    @Override
    public double getIntensityAt(Vector3D point) {
        double d = position.subtract(point).magnitude();
        if (d < 1e-6) return getIntensity();
        return getIntensity() / Math.pow(d, falloffPower);
    }
}