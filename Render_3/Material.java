import java.awt.Color;

// This class is part of the ray tracer.
public class Material {

// Material color and light values.
    private Color color;
    private double ambient;
    private double diffuse;
    private double specular;
    private double shininess;
    private double reflectivity;
    private double transparency;
    private double ior;

// Create a custom material.
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

// Create a matte material.
    public static Material matte(Color color) {
        return new Material(color, 0.05, 0.9, 0.0, 1.0, 0.0, 0.0, 1.0);
    }

// Create a shiny material.
    public static Material shiny(Color color, double specular, double shininess) {
        return new Material(color, 0.05, 0.8, specular, shininess, 0.0, 0.0, 1.0);
    }

// Create a mirror material.
    public static Material mirror(Color color, double reflectivity) {
        return new Material(color, 0.05, 0.1, 0.9, 128.0, reflectivity, 0.0, 1.0);
    }

// Create a glass material.
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
