import java.awt.Color;

// Infinitely distant light — constant direction and intensity everywhere.
public class DirectionalLight extends Light {

    // Direction the light travels (toward scene); stored normalized.
    private Vector3D direction;

    public DirectionalLight(Vector3D direction, Color color, double intensity) {
        super(color, intensity);
        this.direction = direction.normalize();
    }

    // The illumination comes from the opposite of the travel direction.
    @Override
    public Vector3D getDirectionFrom(Vector3D point) {
        return direction.multiply(-1).normalize();
    }

    @Override
    public double getMaxShadowDistance(Vector3D point) {
        return Double.MAX_VALUE;
    }

    @Override
    public double getIntensityAt(Vector3D point) {
        return getIntensity();
    }
}