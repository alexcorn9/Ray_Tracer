import java.awt.Color;
import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {

        // Camera pulled back slightly so the pedestal doesn't dominate the foreground
        Camera camera = new Camera(
            new Vector3D(0, 0.3, 0.5),
            0.1,
            100.0
        );

        RenderSettings settings = new RenderSettings(
            4096,
            2160,
            2,           // 4 bounces: enough for glass refraction + one reflection
            "Discovery.png"
        );

        Scene scene = new Scene(camera, settings);

        // =====================================================================
        // LIGHTING — Indiana Jones / ancient temple palette
        // Key:   warm golden torch above-left  (main illumination)
        // Fill:  dimmer warm torch above-right  (reduces hard shadow contrast)
        // Gem:   very soft warm glow near jewel (makes refraction visible)
        // Dir:   cool ambient sky leak from above (separates ceiling from walls)
        // No red point light — that was the primary cause of the red saturation.
        // =====================================================================

        // Main torch — upper-left, warm golden amber
        scene.addLight(new PointLight(
            new Vector3D(-2.0, 3.5, -3.5),
            new Color(255, 210, 140),   // warm golden amber
            90.0,
            2.0
        ));

        // Fill torch — upper-right, slightly cooler warm
        scene.addLight(new PointLight(
            new Vector3D(2.5, 2.8, -4.5),
            new Color(255, 200, 130),
            55.0,
            2.0
        ));

        // Near torch — between camera and pedestal, low and warm.
        // Lights the front of the pedestal so it doesn't go black.
        scene.addLight(new PointLight(
            new Vector3D(0.0, 1.2, -1.5),
            new Color(255, 220, 160),
            18.0,
            2.0
        ));

        // Soft gem accent — right next to the jewel, very low intensity.
        // Gives the refracted colour a visible warm tint without flooding the room.
        scene.addLight(new PointLight(
            new Vector3D(0.3, 0.6, -2.9),
            new Color(255, 180, 100),
            4.0,
            2.0
        ));

        // Directional sky leak — cool, very dim.
        // Keeps the ceiling from being pitch black and adds depth to shadows.
        scene.addLight(new DirectionalLight(
            new Vector3D(0.0, -1.0, 0.3),   // coming straight down
            new Color(180, 210, 255),        // pale cool blue
            0.18
        ));

        // =====================================================================
        // MATERIALS
        // Ambient raised to 0.12 so unlighted areas read as dark stone,
        // not pure black. Diffuse kept high for good torch response.
        // =====================================================================

        // Stone floor — dark sandstone, slightly reflective
        Material floorMat = new Material(
            new Color(100, 90, 78),
            0.12, 0.85, 0.20, 32.0,
            0.08,   // subtle stone reflection
            0.0, 1.0
        );

        // Walls — warm limestone, matte
        Material wallMat = new Material(
            new Color(120, 100, 72),
            0.12, 0.88, 0.05, 8.0,
            0.0, 0.0, 1.0
        );

        // Ceiling — darker, almost charcoal stone
        Material ceilingMat = new Material(
            new Color(55, 48, 40),
            0.08, 0.80, 0.0, 1.0,
            0.0, 0.0, 1.0
        );

        // Columns — light aged limestone
        Material columnMat = new Material(
            new Color(185, 170, 140),
            0.13, 0.85, 0.12, 24.0,
            0.0, 0.0, 1.0
        );

        // Pedestal — polished dark gold / ancient bronze
        Material pedestalMat = new Material(
            new Color(140, 108, 55),
            0.13, 0.75, 0.60, 96.0,
            0.25,   // noticeable reflection to show ray bounces
            0.0, 1.0
        );

        // Gem / jewel — ruby glass
        // transparency 0.85 + ior 1.52 gives strong visible refraction.
        // reflectivity 0.12 gives Fresnel-like bright highlights.
        // Low ambient so the gem glows from light, not from nothing.
        // Color is a deep crimson — bright enough to read as ruby in refraction.
        Material crystalMat = new Material(
            new Color(200, 20, 30),
            0.04, 0.10, 0.95, 200.0,
            0.12,
            0.85,
            1.52
        );

        // =====================================================================
        // GEOMETRY — room box
        // =====================================================================

        // Floor
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

        // Back wall
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

        // Left wall
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

        // Right wall
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

        // Ceiling
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

        // =====================================================================
        // COLUMNS — symmetric pairs flanking the central aisle
        // Moved slightly further from camera so they frame without occluding.
        // =====================================================================

        ObjReader.load("COLUMN.obj", scene, columnMat, new Vector3D(-3, -1.35, -3.2), 1.0);
        ObjReader.load("COLUMN.obj", scene, columnMat, new Vector3D(-3, -1.35, -5.5), 1.0);
        ObjReader.load("COLUMN.obj", scene, columnMat, new Vector3D( 3, -1.35, -3.2), 1.0);
        ObjReader.load("COLUMN.obj", scene, columnMat, new Vector3D( 3, -1.35, -5.5), 1.0);

        // =====================================================================
        // PEDESTAL — centred on scene axis, pushed further back so its shadow
        // falls behind it instead of toward the camera.
        // =====================================================================

        ObjReader.load(
            "pedestal.obj",
            scene,
            pedestalMat,
            new Vector3D(1.6, -1.35, -2),  // centred X, deeper Z
            0.75
        );

        // =====================================================================
        // JEWEL — ruby glass, centred on the pedestal top
        // Scale kept small so refraction bends are visible across its volume.
        // =====================================================================

        ObjReader.load(
            "PearJewelOBJ.obj",
            scene,
            crystalMat,
            new Vector3D(0.0, -0.3, -3.5),  // sits on top of pedestal
            0.26
        );

        // =====================================================================
        // RENDER
        // =====================================================================

        Raytracer raytracer = new Raytracer();
        BufferedImage image = raytracer.render(scene);
        raytracer.saveImage(image, settings.getOutputFile());
    }
}