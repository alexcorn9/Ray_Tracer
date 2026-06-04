import java.awt.Color;

// Base class for all scene objects.
public abstract class Object3D {

    private Material material;

    public Object3D(Color color) {
        this.material = Material.matte(color);
    }

    // Full material constructor.
    public Object3D(Material material) {
        this.material = material;
    }

    public Material getMaterial() { return material; }
    public Color    getColor()    { return material.getColor(); }

    // Compute intersection with a ray. Returns null if no hit.
    public abstract Intersection getIntersection(Ray ray);

    public abstract Vector3D getNormal(Vector3D point);
}