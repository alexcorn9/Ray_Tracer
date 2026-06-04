import java.awt.Color;
import java.awt.image.BufferedImage;


public class Main {

    public static void main(String[] args) {

        
        Camera camera = new Camera(
            new Vector3D(-0.9, 0.95, -0.3),
            0.1,
            200.0
        );

        RenderSettings settings = new RenderSettings(
            4096, 2160,
            4,
            "NightRoad.png"
        );

        Scene scene = new Scene(camera, settings);

        // MATERIALS

        // the flashlight and blinkers uniformly.
        Material asphalt = new Material(
            new Color(20, 20, 23),
            0.01, 0.50, 0.40, 28.0,
            0.30,       // full-road reflection — covers refraction requirement via road surface
            0.0, 1.0
        );

        // Road markings — white center dashes
        Material roadMark = new Material(
            new Color(210, 210, 200),
            0.02, 0.75, 0.08, 8.0,
            0.0, 0.0, 1.0
        );

        // Road edge lines — brighter white continuous stripes
        Material edgeLine = new Material(
            new Color(245, 245, 238),
            0.03, 0.82, 0.05, 4.0,
            0.0, 0.0, 1.0
        );

        // Night sky
        Material skyMat = new Material(
            new Color(3, 3, 10),
            0.01, 0.4, 0.0, 1.0,
            0.0, 0.0, 1.0
        );

        // Forest silhouette
        Material treeMat = new Material(
            new Color(9, 16, 9),
            0.008, 0.45, 0.0, 1.0,
            0.0, 0.0, 1.0
        );

        // Road shoulder / gravel
        Material shoulderMat = new Material(
            new Color(28, 26, 22),
            0.008, 0.55, 0.0, 1.0,
            0.0, 0.0, 1.0
        );

        Material carBodyMat = new Material(
            new Color(22, 26, 55),
            0.04, 0.50, 0.70, 96.0,
            0.22,
            0.0, 1.0
        );

        Material blinkerMat = new Material(
            new Color(255, 140, 10),
            0.90, 0.60, 1.0, 256.0,
            0.10, 0.0, 1.0
        );

        // LIGHTING

        scene.addLight(new PointLight(
            new Vector3D(-0.9, 0.95, -0.3),
            new Color(255, 200, 130),    // warm amber flashlight glow
            160.0,
            1.5
        ));

        scene.addLight(new PointLight(
            new Vector3D(-0.5, 0.6, -1.0),
            new Color(240, 185, 110),
            25.0,
            2.0
        ));

        // rot=270, scale=0.25, offset=(0.27,0,-3):
        // Taillight positions: X≈[-0.06, 0.60], Y≈0.22, Z≈-1.92
        scene.addLight(new PointLight(
            new Vector3D(-0.06, 0.22, -1.92),
            new Color(255, 120, 5),
            20.0, 2.0
        ));
        scene.addLight(new PointLight(
            new Vector3D(0.60, 0.22, -1.92),
            new Color(255, 120, 5),
            20.0, 2.0
        ));

        // GEOMETRY — ROAD

        double roadW    =  3.2;
        double roadNear =  4.0;
        double roadFar  = -80.0;
        double roadY    = -0.01;

        scene.addObject(new Triangle(
            new Vector3D(-roadW, roadY, roadNear),
            new Vector3D( roadW, roadY, roadNear),
            new Vector3D( roadW, roadY, roadFar),
            asphalt
        ));
        scene.addObject(new Triangle(
            new Vector3D(-roadW, roadY, roadNear),
            new Vector3D( roadW, roadY, roadFar),
            new Vector3D(-roadW, roadY, roadFar),
            asphalt
        ));

        // Shoulders
        double shoulderW = 7.5;
        scene.addObject(new Triangle(
            new Vector3D( roadW,     roadY, roadNear),
            new Vector3D( shoulderW, roadY, roadNear),
            new Vector3D( shoulderW, roadY, roadFar),
            shoulderMat
        ));
        scene.addObject(new Triangle(
            new Vector3D( roadW,     roadY, roadNear),
            new Vector3D( shoulderW, roadY, roadFar),
            new Vector3D( roadW,     roadY, roadFar),
            shoulderMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-shoulderW, roadY, roadNear),
            new Vector3D(-roadW,     roadY, roadNear),
            new Vector3D(-roadW,     roadY, roadFar),
            shoulderMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-shoulderW, roadY, roadNear),
            new Vector3D(-roadW,     roadY, roadFar),
            new Vector3D(-shoulderW, roadY, roadFar),
            shoulderMat
        ));

        double markY = roadY + 0.003;
        double markW = 0.06;
        for (int i = 0; i < 22; i++) {
            double zs = -4.5 - i * 3.5;
            double ze = zs - 1.8;
            scene.addObject(new Triangle(
                new Vector3D(-markW, markY, zs),
                new Vector3D( markW, markY, zs),
                new Vector3D( markW, markY, ze),
                roadMark
            ));
            scene.addObject(new Triangle(
                new Vector3D(-markW, markY, zs),
                new Vector3D( markW, markY, ze),
                new Vector3D(-markW, markY, ze),
                roadMark
            ));
        }

        double eW = 0.10;
        double eY = roadY + 0.003;

        // Left edge
        scene.addObject(new Triangle(
            new Vector3D(-roadW - eW, eY, roadNear),
            new Vector3D(-roadW,      eY, roadNear),
            new Vector3D(-roadW,      eY, roadFar),
            edgeLine
        ));
        scene.addObject(new Triangle(
            new Vector3D(-roadW - eW, eY, roadNear),
            new Vector3D(-roadW,      eY, roadFar),
            new Vector3D(-roadW - eW, eY, roadFar),
            edgeLine
        ));

        // Right edge
        scene.addObject(new Triangle(
            new Vector3D(roadW,      eY, roadNear),
            new Vector3D(roadW + eW, eY, roadNear),
            new Vector3D(roadW + eW, eY, roadFar),
            edgeLine
        ));
        scene.addObject(new Triangle(
            new Vector3D(roadW,      eY, roadNear),
            new Vector3D(roadW + eW, eY, roadFar),
            new Vector3D(roadW,      eY, roadFar),
            edgeLine
        ));

        // GEOMETRY — SKY BOX

        double skyDist = -79.0;
        double skyH    = 32.0;
        double skyW    = 75.0;

        scene.addObject(new Triangle(
            new Vector3D(-skyW, roadY, skyDist),
            new Vector3D( skyW, roadY, skyDist),
            new Vector3D( skyW, skyH,  skyDist),
            skyMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-skyW, roadY, skyDist),
            new Vector3D( skyW, skyH,  skyDist),
            new Vector3D(-skyW, skyH,  skyDist),
            skyMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-skyW, roadY, roadNear),
            new Vector3D(-skyW, roadY, skyDist),
            new Vector3D(-skyW, skyH,  skyDist),
            skyMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-skyW, roadY, roadNear),
            new Vector3D(-skyW, skyH,  skyDist),
            new Vector3D(-skyW, skyH,  roadNear),
            skyMat
        ));
        scene.addObject(new Triangle(
            new Vector3D( skyW, roadY, roadNear),
            new Vector3D( skyW, roadY, skyDist),
            new Vector3D( skyW, skyH,  skyDist),
            skyMat
        ));
        scene.addObject(new Triangle(
            new Vector3D( skyW, roadY, roadNear),
            new Vector3D( skyW, skyH,  skyDist),
            new Vector3D( skyW, skyH,  roadNear),
            skyMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-skyW, skyH, roadNear),
            new Vector3D( skyW, skyH, roadNear),
            new Vector3D( skyW, skyH, skyDist),
            skyMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-skyW, skyH, roadNear),
            new Vector3D( skyW, skyH, skyDist),
            new Vector3D(-skyW, skyH, skyDist),
            skyMat
        ));

        // GEOMETRY — FOREST

        double[][] leftTrees = {
            {-4.0,-3.5,1.1,4.2},{-4.2,-5.0,1.0,5.0},{-3.9,-6.5,1.2,4.5},
            {-4.1,-8.0,1.3,5.5},{-4.0,-9.5,1.0,4.8},{-4.2,-11.0,1.2,5.2},
            {-3.9,-12.8,1.1,4.6},{-4.1,-14.5,1.3,5.8},{-4.0,-16.5,1.2,5.0},
            {-4.2,-18.5,1.1,4.8},{-4.0,-21.0,1.3,5.5},{-4.1,-24.5,1.2,5.2},
            {-4.0,-28.5,1.4,6.0},{-4.2,-34.0,1.3,5.8},{-4.0,-41.0,1.5,6.5},
            {-6.0,-4.0,1.6,6.0},{-6.2,-6.0,1.7,6.8},{-5.9,-8.0,1.5,5.8},
            {-6.1,-10.0,1.6,7.0},{-5.8,-12.0,1.5,6.2},{-6.2,-14.0,1.7,7.2},
            {-5.9,-16.5,1.6,6.5},{-6.1,-19.5,1.7,7.0},{-5.8,-23.0,1.7,7.5},
            {-6.2,-28.0,1.8,7.8},{-5.9,-35.0,1.8,7.2},{-6.1,-44.0,1.9,8.0},
            {-8.2,-4.5,2.1,7.5},{-8.0,-7.5,2.2,8.0},{-8.3,-11.0,2.0,7.2},
            {-8.1,-15.0,2.2,8.5},{-8.0,-20.0,2.1,7.8},{-8.3,-27.0,2.3,9.0},
            {-8.1,-36.0,2.2,8.5},{-8.2,-47.0,2.4,9.5},
        };
        double[][] rightTrees = {
            {4.0,-3.5,1.1,4.2},{4.2,-5.0,1.0,5.0},{3.9,-6.5,1.2,4.5},
            {4.1,-8.0,1.3,5.5},{4.0,-9.5,1.0,4.8},{4.2,-11.0,1.2,5.2},
            {3.9,-12.8,1.1,4.6},{4.1,-14.5,1.3,5.8},{4.0,-16.5,1.2,5.0},
            {4.2,-18.5,1.1,4.8},{4.0,-21.0,1.3,5.5},{4.1,-24.5,1.2,5.2},
            {4.0,-28.5,1.4,6.0},{4.2,-34.0,1.3,5.8},{4.0,-41.0,1.5,6.5},
            {6.0,-4.0,1.6,6.0},{6.2,-6.0,1.7,6.8},{5.9,-8.0,1.5,5.8},
            {6.1,-10.0,1.6,7.0},{5.8,-12.0,1.5,6.2},{6.2,-14.0,1.7,7.2},
            {5.9,-16.5,1.6,6.5},{6.1,-19.5,1.7,7.0},{5.8,-23.0,1.7,7.5},
            {6.2,-28.0,1.8,7.8},{5.9,-35.0,1.8,7.2},{6.1,-44.0,1.9,8.0},
            {8.2,-4.5,2.1,7.5},{8.0,-7.5,2.2,8.0},{8.3,-11.0,2.0,7.2},
            {8.1,-15.0,2.2,8.5},{8.0,-20.0,2.1,7.8},{8.3,-27.0,2.3,9.0},
            {8.1,-36.0,2.2,8.5},{8.2,-47.0,2.4,9.5},
        };
        for (double[] t : leftTrees)  addTree(scene, treeMat, t[0], t[1], t[2], t[3], roadY);
        for (double[] t : rightTrees) addTree(scene, treeMat, t[0], t[1], t[2], t[3], roadY);

        // GEOMETRY — CAR  (rotation 270°: front faces INTO scene)
        ObjReader.load(
            "LowPolyCar.obj",
            scene,
            carBodyMat,
            new Vector3D(0.27, 0.0, -3.0),
            0.25,
            270.0
        );

        double blH = 0.08, blW = 0.09;
        scene.addObject(new Triangle(
            new Vector3D(-0.06-blW, 0.16, -1.90),
            new Vector3D(-0.06+blW, 0.16, -1.90),
            new Vector3D(-0.06+blW, 0.16+blH, -1.90),
            blinkerMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-0.06-blW, 0.16, -1.90),
            new Vector3D(-0.06+blW, 0.16+blH, -1.90),
            new Vector3D(-0.06-blW, 0.16+blH, -1.90),
            blinkerMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(0.60-blW, 0.16, -1.90),
            new Vector3D(0.60+blW, 0.16, -1.90),
            new Vector3D(0.60+blW, 0.16+blH, -1.90),
            blinkerMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(0.60-blW, 0.16, -1.90),
            new Vector3D(0.60+blW, 0.16+blH, -1.90),
            new Vector3D(0.60-blW, 0.16+blH, -1.90),
            blinkerMat
        ));

        // BUILD BVH + RENDER
        scene.buildBVH();

        Raytracer raytracer = new Raytracer();
        BufferedImage image = raytracer.render(scene);
        raytracer.saveImage(image, settings.getOutputFile());
    }

    
    private static void addTree(Scene scene, Material mat,
                                double cx, double cz,
                                double halfW, double height, double baseY) {
        scene.addObject(new Triangle(
            new Vector3D(cx-halfW, baseY,         cz),
            new Vector3D(cx+halfW, baseY,         cz),
            new Vector3D(cx,       baseY+height,  cz),
            mat
        ));
        scene.addObject(new Triangle(
            new Vector3D(cx, baseY,         cz-halfW*0.7),
            new Vector3D(cx, baseY,         cz+halfW*0.7),
            new Vector3D(cx, baseY+height,  cz),
            mat
        ));
    }
}