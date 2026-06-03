import java.util.ArrayList;
import java.util.List;

public class Scene {

    private Camera         camera;
    private RenderSettings settings;
    private List<Object3D> objects;
    private List<Light>    lights;

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
}