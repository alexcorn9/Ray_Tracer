import java.awt.Color;

// This class is used by the ray tracer.
public abstract class Object3D {

    // Material used by this object.
    private Material material;

    // Create an object from a simple color.
    public Object3D(Color color) {
        this.material = Material.matte(color);
    }

    // Create an object from a material.
    public Object3D(Material material) {
        this.material = material;
    }

    public Material getMaterial() { return material; }
    public Color    getColor()    { return material.getColor(); }

    // Each object checks ray hits differently.
    public abstract Intersection getIntersection(Ray ray);

    // Each object provides its own normal.
    public abstract Vector3D getNormal(Vector3D point);
}
