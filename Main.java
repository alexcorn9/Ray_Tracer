import java.awt.Color;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        Camera camera = new Camera(new Vector3D(0, 0, 0), 800, 800);
        Scene scene = new Scene(camera);

        scene.addObject(new Sphere(new Vector3D(-0.3, 0, -3), 0.3, Color.RED));
        scene.addObject(new Sphere(new Vector3D(0.4, 0, -3), 0.2, Color.BLUE));

        Raytracer raytracer = new Raytracer();
        BufferedImage image = raytracer.render(scene);
        raytracer.saveImage(image, "spheres.png");
    }
}