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

        // Main point light
        scene.addLight(
            new Light(
                new Vector3D(
                    -2.0,
                    2.5,
                    -2.0
                ),
                Color.WHITE,
                24.0,
                true,
                2.0
            )
        );

        // Very soft directional light
        scene.addLight(
            new Light(
                new Vector3D(
                    -0.4,
                    -0.6,
                    1.0
                ),
                Color.WHITE,
                0.18
            )
        );

        // Load cube OBJ
        // Smaller and farther so its 3D shape is cleaner
        ObjReader.load(
            "cube.obj",
            scene,
            Color.WHITE,
            new Vector3D(
                1.15,
                0.0,
                -5.0
            ),
            0.55
        );

        // Add red sphere
        scene.addObject(
            new Sphere(
                new Vector3D(
                    -1.45,
                    0.45,
                    -4.0
                ),
                0.5,
                Color.RED
            )
        );

        // Add blue sphere
        scene.addObject(
            new Sphere(
                new Vector3D(
                    -0.45,
                    0.25,
                    -3.7
                ),
                0.32,
                Color.BLUE
            )
        );
        // Add floor platform
        // Made with two large triangles
        scene.addObject(
            new Triangle(
                new Vector3D(-3.5, -1.35, -3.0),
                new Vector3D( 3.5, -1.35, -3.0),
                new Vector3D( 3.5, -1.35, -8.0),
                Color.GRAY
            )
        );

        scene.addObject(
            new Triangle(
                new Vector3D(-3.5, -1.35, -3.0),
                new Vector3D( 3.5, -1.35, -8.0),
                new Vector3D(-3.5, -1.35, -8.0),
                Color.GRAY
            )
        );

        // Add 3D green triangular prism
        // Vertices use different Z values so perspective shows depth
        scene.addObject(
            new Triangle(
                new Vector3D(
                    -0.95,
                    -1.15,
                    -3.8
                ),

                new Vector3D(
                    0.05,
                    -1.15,
                    -4.4
                ),

                new Vector3D(
                    -0.45,
                    -0.25,
                    -4.0
                ),

                Color.GREEN,

                // Prism depth
                0.65
            )
        );

        // Create raytracer
        Raytracer raytracer =
            new Raytracer();

        // Render scene
        BufferedImage image =
            raytracer.render(scene);

        // Save image
        raytracer.saveImage(
            image,
            "raytracer_v07.png"
        );
    }
}