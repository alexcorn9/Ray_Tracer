import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Camera camera; // Scene camera
    private List<Object3D> objects; // List of objects

    public Scene(Camera camera) {
        this.camera = camera;
        this.objects = new ArrayList<>();
    }

    public Camera getCamera() { return camera; } // Return camera
    public List<Object3D> getObjects() { return objects; } // Return objects

    public void addObject(Object3D object) { objects.add(object); } // Add object
}
