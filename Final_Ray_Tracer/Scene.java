import java.util.ArrayList;
import java.util.List;

// This class is part of the ray tracer.
public class Scene {

// Main scene data.
    private Camera         camera;
    private RenderSettings settings;
    private List<Object3D> objects;
    private List<Light>    lights;

// BVH root for faster rendering.
    private BVHNode bvh = null;

// Create an empty scene.
    public Scene(Camera camera, RenderSettings settings) {
        this.camera   = camera;
        this.settings = settings;
        this.objects  = new ArrayList<>();
        this.lights   = new ArrayList<>();
    }

    public Camera         getCamera()   { return camera; }
    public RenderSettings getSettings() { return settings; }
    public List<Object3D> getObjects()  { return objects; }
    public List<Light>    getLights()   { return lights; }

// Add an object to the scene.
    public void addObject(Object3D object) { objects.add(object); }
// Add a light to the scene.
    public void addLight(Light light)      { lights.add(light); }

// Build the BVH tree.
    public void buildBVH() {
        if (!objects.isEmpty()) {
            bvh = BVHNode.build(objects);
            System.out.println("BVH built over " + objects.size() + " objects.");
        }
    }

// Return the BVH root.
    public BVHNode getBVH() { return bvh; }
}
