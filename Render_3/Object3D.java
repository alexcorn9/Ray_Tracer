import java.awt.Color;

// This class is part of the ray tracer.
public abstract class Object3D {

// Material used by the object.
    private Material material;

// Create an object from a color.
    public Object3D(Color color) {
        this.material = Material.matte(color);
    }

// Create an object from a material.
    public Object3D(Material material) {
        this.material = material;
    }

// Return the object material.
    public Material getMaterial() { return material; }
// Return the object color.
    public Color    getColor()    { return material.getColor(); }

// Check if a ray hits the object.
    public abstract Intersection getIntersection(Ray ray);

// Get the object normal.
    public abstract Vector3D getNormal(Vector3D point);
}
