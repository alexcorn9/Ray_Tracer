import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Camera camera; // Scene camera
    private List<Object3D> objects; // List of objects
    private List<Light> lights; // List of lights

    public Scene(Camera camera) {
        this.camera = camera;
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    public Camera getCamera() { return camera; } // Return camera
    public List<Object3D> getObjects() { return objects; } // Return objects
    public List<Light> getLights() { return lights; } // Return lights

    public void addObject(Object3D object) { objects.add(object); } // Add object
    public void addLight(Light light) { lights.add(light); } // Add light
}
