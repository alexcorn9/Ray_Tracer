import java.awt.Color;

// Blinn-Phong material with optional reflection and refraction.
// Attach to any Object3D to control its optical properties.
public class Material {

    private Color color;
    private double ambient;      // Ambient coefficient  [0..1]
    private double diffuse;      // Diffuse coefficient  [0..1]
    private double specular;     // Specular coefficient [0..1]
    private double shininess;    // Blinn-Phong shininess exponent (e.g. 32, 64, 128)
    private double reflectivity; // Mirror reflectivity  [0..1], 0 = no reflection
    private double transparency; // Transparency         [0..1], 0 = opaque
    private double ior;          // Index of Refraction  (e.g. 1.5 for glass, 1.33 water)

    // Full constructor
    public Material(
        Color color,
        double ambient,
        double diffuse,
        double specular,
        double shininess,
        double reflectivity,
        double transparency,
        double ior
    ) {
        this.color        = color;
        this.ambient      = ambient;
        this.diffuse      = diffuse;
        this.specular     = specular;
        this.shininess    = shininess;
        this.reflectivity = reflectivity;
        this.transparency = transparency;
        this.ior          = ior;
    }

    // Matte opaque material (no specular, no reflection, no refraction)
    public static Material matte(Color color) {
        return new Material(color, 0.05, 0.9, 0.0, 1.0, 0.0, 0.0, 1.0);
    }

    // Shiny opaque material
    public static Material shiny(Color color, double specular, double shininess) {
        return new Material(color, 0.05, 0.8, specular, shininess, 0.0, 0.0, 1.0);
    }

    // Mirror material
    public static Material mirror(Color color, double reflectivity) {
        return new Material(color, 0.05, 0.1, 0.9, 128.0, reflectivity, 0.0, 1.0);
    }

    // Glass material
    public static Material glass(Color color, double ior) {
        return new Material(color, 0.0, 0.0, 0.9, 128.0, 0.1, 0.9, ior);
    }

    public Color   getColor()        { return color; }
    public double  getAmbient()      { return ambient; }
    public double  getDiffuse()      { return diffuse; }
    public double  getSpecular()     { return specular; }
    public double  getShininess()    { return shininess; }
    public double  getReflectivity() { return reflectivity; }
    public double  getTransparency() { return transparency; }
    public double  getIor()          { return ior; }
}