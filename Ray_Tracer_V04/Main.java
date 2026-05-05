import java.awt.Color;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {

        // Create camera (position, width, height, near, far)
        Camera camera = new Camera(new Vector3D(0, 0, 0), 800, 800, 0.1, 100);

        // Create scene and assign camera
        Scene scene = new Scene(camera);

        // Add directional light
        scene.addLight(new Light(
            new Vector3D(0, 0, -1), // Light direction (towards objects)
            Color.WHITE,
            1.0
        ));

        // Create simple test scene

        // Add red sphere
        scene.addObject(new Sphere(
            new Vector3D(-0.3, 0, -3),
            0.3,
            Color.RED
        ));

        // Add blue sphere
        scene.addObject(new Sphere(
            new Vector3D(0.4, 0, -3),
            0.2,
            Color.BLUE
        ));

        // Add green triangle
        scene.addObject(new Triangle(
            new Vector3D(-0.4, -0.9, -2),
            new Vector3D(0.4, -0.9, -2),
            new Vector3D(0.0, -0.25, -2),
            Color.GREEN
        ));

        // Create raytracer
        Raytracer raytracer = new Raytracer();

        // Render the scene (create image)
        BufferedImage image = raytracer.render(scene);

        // Save image as PNG
        raytracer.saveImage(image, "raytracer.png");
    }
}
