import java.awt.Color;

// This class is used by the ray tracer.
public class DirectionalLight extends Light {

    // Direction of the light.
    private Vector3D direction;

    // Create a directional light.
    public DirectionalLight(Vector3D direction, Color color, double intensity) {
        super(color, intensity);
        this.direction = direction.normalize();
    }

    @Override
    // Direction used for shading.
    public Vector3D getDirectionFrom(Vector3D point) {
        return direction.multiply(-1).normalize();
    }

    @Override
    // Directional light has no fixed distance.
    public double getMaxShadowDistance(Vector3D point) {
        return Double.MAX_VALUE;
    }

    @Override
    // Directional light keeps constant strength.
    public double getIntensityAt(Vector3D point) {
        return getIntensity();
    }
}
