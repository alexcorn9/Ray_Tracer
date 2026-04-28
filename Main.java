import java.awt.Color;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {

        Camera camera = new Camera(new Vector3D(0, 0, 0), 800, 800, 0.1, 100); // Create camera
        Scene scene = new Scene(camera); // Create scene

        scene.addObject(new Sphere(new Vector3D(-0.3, 0, -3), 0.3, Color.RED)); // Add red sphere
        scene.addObject(new Sphere(new Vector3D(0.4, 0, -3), 0.2, Color.BLUE)); // Add blue sphere

        scene.addObject(new Triangle( // Add triangle below spheres
            new Vector3D(-0.4, -0.9, -2),
            new Vector3D(0.4, -0.9, -2),
            new Vector3D(0.0, -0.25, -2),
            Color.GREEN
        ));

        Raytracer raytracer = new Raytracer(); // Create raytracer
        BufferedImage image = raytracer.render(scene); // Render scene
        raytracer.saveImage(image, "raytracer.png"); // Save image
    }
}