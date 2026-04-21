import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Camera camera;
    private List<Object3D> objects;

    public Scene(Camera camera) {
        this.camera = camera;
        this.objects = new ArrayList<>();
    }

    public Camera getCamera() {
        return camera;
    }

    public List<Object3D> getObjects() {
        return objects;
    }

    public void addObject(Object3D object) {
        objects.add(object);
    }
}