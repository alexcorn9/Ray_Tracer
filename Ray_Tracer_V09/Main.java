import java.awt.Color;
import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {

        Camera camera = new Camera(
            new Vector3D(0, 0, 0),
            0.1,
            100.0
        );

        RenderSettings settings = new RenderSettings(
            800,
            600,
            2,
            "Discovery.png"
        );

        Scene scene = new Scene(camera, settings);

        scene.addLight(new PointLight(
            new Vector3D(-1.8, 3.5, -2.0),
            Color.WHITE,
            80.0,
            2.0
        ));

        scene.addLight(new PointLight(
            new Vector3D(2.0, 2.0, -3.0),
            new Color(100, 170, 255),
            25.0,
            2.0
        ));

        scene.addLight(new DirectionalLight(
            new Vector3D(-0.4, -0.8, 0.6),
            new Color(210, 220, 255),
            0.55
        ));

        Material floorMat = Material.shiny(new Color(85, 80, 75), 0.45, 64.0);

        scene.addObject(new Triangle(
            new Vector3D(-3.2, -1.35, -2.0),
            new Vector3D( 3.2, -1.35, -2.0),
            new Vector3D( 3.2, -1.35, -7.0),
            floorMat
        ));

        scene.addObject(new Triangle(
            new Vector3D(-3.2, -1.35, -2.0),
            new Vector3D( 3.2, -1.35, -7.0),
            new Vector3D(-3.2, -1.35, -7.0),
            floorMat
        ));

        ObjReader.load(
            "Ruins.obj",
            scene,
            Material.matte(new Color(145, 105, 75)),
            new Vector3D(0.0, -1.15, -4.9),
            5.0
        );

        ObjReader.load(
            "Column.obj",
            scene,
            Material.matte(new Color(190, 155, 105)),
            new Vector3D(-1.25, -1.35, -3.6),
            5.0
        );

        ObjReader.load(
            "Column.obj",
            scene,
            Material.matte(new Color(190, 155, 105)),
            new Vector3D(1.25, -1.35, -3.6),
            5.0
        );

        ObjReader.load(
            "pedestal.obj",
            scene,
            Material.shiny(new Color(165, 130, 80), 0.55, 80.0),
            new Vector3D(0.0, -1.35, -2.85),
            0.70
        );

        ObjReader.load(
            "PearJewelOBJ.obj",
            scene,
            Material.glass(new Color(0, 220, 255), 1.5),
            new Vector3D(0.0, -0.78, -2.85),
            0.18
        );

        Raytracer raytracer = new Raytracer();
        BufferedImage image = raytracer.render(scene);
        raytracer.saveImage(image, settings.getOutputFile());
    }
}