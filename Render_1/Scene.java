import java.util.ArrayList;
import java.util.List;

// This class is used by the ray tracer.
public class Scene {

    // Scene data.
    private Camera         camera;
    private RenderSettings settings;
    private List<Object3D> objects;
    private List<Light>    lights;

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
}
