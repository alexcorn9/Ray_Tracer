import java.awt.Color;

public class DirectionalLight extends Light {

    private Vector3D direction;

    public DirectionalLight(Vector3D direction, Color color, double intensity) {
        super(color, intensity);
        this.direction = direction.normalize();
    }

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