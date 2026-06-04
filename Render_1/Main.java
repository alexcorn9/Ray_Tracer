import java.awt.Color;
import java.awt.image.BufferedImage;

// This class is used by the ray tracer.
public class Main {

    public static void main(String[] args) {

        // Create the camera for this render.
        Camera camera = new Camera(
            new Vector3D(0, 0.3, 0.5),
            0.1,
            100.0
        );

        // Set the final image size and file name.
        RenderSettings settings = new RenderSettings(
            4096,
            2160,
            2,
            "Discovery.png"
        );

        // Create the scene that stores lights and objects.
        Scene scene = new Scene(camera, settings);

        // Add a point light to the temple.
        scene.addLight(new PointLight(
            new Vector3D(-2.0, 3.5, -3.5),
            new Color(255, 210, 140),
            90.0,
            2.0
        ));

        scene.addLight(new PointLight(
            new Vector3D(2.5, 2.8, -4.5),
            new Color(255, 200, 130),
            55.0,
            2.0
        ));

        scene.addLight(new PointLight(
            new Vector3D(0.0, 1.2, -1.5),
            new Color(255, 220, 160),
            18.0,
            2.0
        ));

        scene.addLight(new PointLight(
            new Vector3D(0.3, 0.6, -2.9),
            new Color(255, 180, 100),
            4.0,
            2.0
        ));

        // Add a soft directional light from above.
        scene.addLight(new DirectionalLight(
            new Vector3D(0.0, -1.0, 0.3),
            new Color(180, 210, 255),
            0.18
        ));

        // Material for the stone floor.
        Material floorMat = new Material(
            new Color(100, 90, 78),
            0.12, 0.85, 0.20, 32.0,
            0.08,
            0.0, 1.0
        );

        // Material for the temple walls.
        Material wallMat = new Material(
            new Color(120, 100, 72),
            0.12, 0.88, 0.05, 8.0,
            0.0, 0.0, 1.0
        );

        // Material for the darker ceiling.
        Material ceilingMat = new Material(
            new Color(55, 48, 40),
            0.08, 0.80, 0.0, 1.0,
            0.0, 0.0, 1.0
        );

        // Material for the side columns.
        Material columnMat = new Material(
            new Color(185, 170, 140),
            0.13, 0.85, 0.12, 24.0,
            0.0, 0.0, 1.0
        );

        // Material for the pedestal.
        Material pedestalMat = new Material(
            new Color(140, 108, 55),
            0.13, 0.75, 0.60, 96.0,
            0.25,
            0.0, 1.0
        );

        // Material for the glass jewel.
        Material crystalMat = new Material(
            new Color(200, 20, 30),
            0.04, 0.10, 0.95, 200.0,
            0.12,
            0.85,
            1.52
        );

        // Add part of the room geometry.
        scene.addObject(new Triangle(
            new Vector3D(-5.0, -1.35, 1.0),
            new Vector3D( 5.0, -1.35, 1.0),
            new Vector3D( 5.0, -1.35, -8.0),
            floorMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-5.0, -1.35, 1.0),
            new Vector3D( 5.0, -1.35, -8.0),
            new Vector3D(-5.0, -1.35, -8.0),
            floorMat
        ));

        scene.addObject(new Triangle(
            new Vector3D(-5.0, -1.35, -7.5),
            new Vector3D( 5.0, -1.35, -7.5),
            new Vector3D( 5.0,  3.5, -7.5),
            wallMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-5.0, -1.35, -7.5),
            new Vector3D( 5.0,  3.5, -7.5),
            new Vector3D(-5.0,  3.5, -7.5),
            wallMat
        ));

        scene.addObject(new Triangle(
            new Vector3D(-5.0, -1.35, 1.0),
            new Vector3D(-5.0, -1.35, -7.5),
            new Vector3D(-5.0,  3.5, -7.5),
            wallMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-5.0, -1.35, 1.0),
            new Vector3D(-5.0,  3.5, -7.5),
            new Vector3D(-5.0,  3.5,  1.0),
            wallMat
        ));

        scene.addObject(new Triangle(
            new Vector3D(5.0, -1.35, 1.0),
            new Vector3D(5.0, -1.35, -7.5),
            new Vector3D(5.0,  3.5, -7.5),
            wallMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(5.0, -1.35, 1.0),
            new Vector3D(5.0,  3.5, -7.5),
            new Vector3D(5.0,  3.5,  1.0),
            wallMat
        ));

        scene.addObject(new Triangle(
            new Vector3D(-5.0, 3.5,  1.0),
            new Vector3D( 5.0, 3.5,  1.0),
            new Vector3D( 5.0, 3.5, -7.5),
            ceilingMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-5.0, 3.5,  1.0),
            new Vector3D( 5.0, 3.5, -7.5),
            new Vector3D(-5.0, 3.5, -7.5),
            ceilingMat
        ));

        // Load one of the column models.
        // Load one of the column models.
        ObjReader.load("COLUMN.obj", scene, columnMat, new Vector3D(-3, -1.35, -3.2), 1.0);
        // Load one of the column models.
        ObjReader.load("COLUMN.obj", scene, columnMat, new Vector3D(-3, -1.35, -5.5), 1.0);
        // Load one of the column models.
        ObjReader.load("COLUMN.obj", scene, columnMat, new Vector3D( 3, -1.35, -3.2), 1.0);
        // Load one of the column models.
        ObjReader.load("COLUMN.obj", scene, columnMat, new Vector3D( 3, -1.35, -5.5), 1.0);

        // Load the pedestal model.
        ObjReader.load(
            "pedestal.obj",
            scene,
            pedestalMat,
            new Vector3D(1.6, -1.35, -2),
            0.75
        );

        // Load the jewel model.
        ObjReader.load(
            "PearJewelOBJ.obj",
            scene,
            crystalMat,
            new Vector3D(0.0, -0.3, -3.5),
            0.26
        );

        // Render and save the image.
        Raytracer raytracer = new Raytracer();
        BufferedImage image = raytracer.render(scene);
        raytracer.saveImage(image, settings.getOutputFile());
    }
}
