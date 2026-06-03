import java.awt.Color;

// Base class for all scene objects.
// Every object carries a Material that controls Blinn-Phong + reflection + refraction.
public abstract class Object3D {

    private Material material;

    // Convenience constructor: wrap a plain Color in a default matte material.
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

    // Surface normal at a given point (fallback when intersection carries no normal).
    public abstract Vector3D getNormal(Vector3D point);
}