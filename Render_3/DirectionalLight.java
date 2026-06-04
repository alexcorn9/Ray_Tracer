import java.awt.Color;

// This class is part of the ray tracer.
public class DirectionalLight extends Light {

// Direction of this light.
    private Vector3D direction;

// Create a directional light.
    public DirectionalLight(Vector3D direction, Color color, double intensity) {
        super(color, intensity);
        this.direction = direction.normalize();
    }

    @Override
// Get light direction for shading.
    public Vector3D getDirectionFrom(Vector3D point) {
        return direction.multiply(-1).normalize();
    }

    @Override
// Directional light has no distance limit.
    public double getMaxShadowDistance(Vector3D point) {
        return Double.MAX_VALUE;
    }

    @Override
// Return the same intensity everywhere.
    public double getIntensityAt(Vector3D point) {
        return getIntensity();
    }
}
