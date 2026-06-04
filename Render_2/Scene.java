import java.util.ArrayList;
import java.util.List;

public class Scene {

    private Camera         camera;
    private RenderSettings settings;
    private List<Object3D> objects;
    private List<Light>    lights;

    // BVH root — built once via buildBVH(), used by Raytracer for fast traversal.
    private BVHNode bvh = null;

    // Constructor with explicit render settings
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

    public void addObject(Object3D object) { objects.add(object); }
    public void addLight(Light light)      { lights.add(light); }

    /**
     * Build the BVH acceleration structure over all current scene objects.
     * Call this once in Main after all objects have been added, before rendering.
     * If not called, Raytracer falls back to the original O(n) brute-force traversal.
     */
    public void buildBVH() {
        if (!objects.isEmpty()) {
            bvh = BVHNode.build(objects);
            System.out.println("BVH built over " + objects.size() + " objects.");
        }
    }

    /** Returns the BVH root, or null if buildBVH() was never called. */
    public BVHNode getBVH() { return bvh; }
}
