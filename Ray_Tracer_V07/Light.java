import java.awt.Color;

public class Light {

    // Directional light constant
    public static final int DIRECTIONAL = 0;

    // Point light constant
    public static final int POINT = 1;

    // Current light type
    private int type;

    // Direction for directional lights
    private Vector3D direction;

    // Position for point lights
    private Vector3D position;

    // RGB light color
    private Color color;

    // Base light intensity
    private double intensity;

    // Falloff exponent
    // d^1, d^2, d^3...
    private double falloffPower;

    // Directional light constructor
    public Light(
        Vector3D direction,
        Color color,
        double intensity
    ) {

        // Save light type
        this.type = DIRECTIONAL;

        // Normalize direction vector
        this.direction =
            direction.normalize();

        // Directional lights have no position
        this.position = null;

        // Save light color
        this.color = color;

        // Save light intensity
        this.intensity = intensity;

        // No falloff for directional lights
        this.falloffPower = 0;
    }

    // Point light constructor
    // Uses default d^2 falloff
    public Light(
        Vector3D position,
        Color color,
        double intensity,
        boolean isPointLight
    ) {

        // Call full constructor
        this(
            position,
            color,
            intensity,
            isPointLight,
            2.0
        );
    }

    // Point light constructor with custom falloff
    public Light(
        Vector3D position,
        Color color,
        double intensity,
        boolean isPointLight,
        double falloffPower
    ) {

        // Save light type
        this.type = POINT;

        // Save point light position
        this.position = position;

        // Point lights have no direction
        this.direction = null;

        // Save RGB color
        this.color = color;

        // Save intensity
        this.intensity = intensity;

        // Save falloff exponent
        this.falloffPower = falloffPower;
    }

    // Return light type
    public int getType() {
        return type;
    }

    // Return directional light direction
    public Vector3D getDirection() {
        return direction;
    }

    // Return point light position
    public Vector3D getPosition() {
        return position;
    }

    // Return RGB light color
    public Color getColor() {
        return color;
    }

    // Return light intensity
    public double getIntensity() {
        return intensity;
    }

    // Return falloff exponent
    public double getFalloffPower() {
        return falloffPower;
    }
}