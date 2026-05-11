import java.awt.Color;
import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {

        // Create camera
        Camera camera = new Camera(
            new Vector3D(0, 0, 0),
            800,
            800,
            0.1,
            100
        );

        // Create scene
        Scene scene =
            new Scene(camera);

        // Directional white light
        scene.addLight(
            new Light(
                new Vector3D(
                    0.0,
                    0.0,
                    1.0
                ),
                Color.WHITE,
                1.1
            )
        );

        // Directional red light
        scene.addLight(
            new Light(
                new Vector3D(
                    0.0,
                    -1.0,
                    0.0
                ),
                Color.RED,
                1.1
            )
        );

        // Point light
        scene.addLight(
            new Light(
                new Vector3D(
                    0.0,
                    1.0,
                    0.0
                ),
                Color.WHITE,
                0.9,
                true
            )
        );

        // Load OBJ model
        ObjReader.load(
            "cube.obj",
            scene,
            Color.WHITE,
            new Vector3D(
                1.0,
                -0.2,
                -6
            ),
            1.0
        );

        // Add red sphere
        scene.addObject(
            new Sphere(
                new Vector3D(
                    -1.2,
                    0.4,
                    -4
                ),
                0.5,
                Color.RED
            )
        );

        // Add blue sphere
        scene.addObject(
            new Sphere(
                new Vector3D(
                    -0.2,
                    0.2,
                    -3.5
                ),
                0.3,
                Color.BLUE
            )
        );

        // Add green triangle
        scene.addObject(
            new Triangle(
                new Vector3D(
                    -1.0,
                    -1.2,
                    -4
                ),

                new Vector3D(
                    0.0,
                    -1.2,
                    -4
                ),

                new Vector3D(
                    -0.5,
                    -0.3,
                    -4
                ),

                Color.GREEN
            )
        );

        // Create raytracer
        Raytracer raytracer =
            new Raytracer();

        // Render scene
        BufferedImage image =
            raytracer.render(scene);

        // Save final image
        raytracer.saveImage(
            image,
            "raytracer_v06.png"
        );
    }
}
