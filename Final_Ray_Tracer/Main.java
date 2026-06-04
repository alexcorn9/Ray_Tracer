import java.awt.Color;
import java.awt.image.BufferedImage;

// This class is part of the ray tracer.
public class Main {

    public static void main(String[] args) {

// Set the camera for this scene.
        Camera camera = new Camera(
            new Vector3D(0.0, 0.8, 4.5),
            0.1,
            200.0
        );

// Set the render size and output file.
        RenderSettings settings = new RenderSettings(
            4096,
            2160,
            2,
            "FrozenGuardian_Test.png"
        );

// Create the scene object.
        Scene scene = new Scene(camera, settings);

// Material for the ice block.
        Material iceMat = new Material(
            new Color(190, 230, 248),
            0.08,
            0.05,
            0.95,
            256.0,
            0.12,
            0.85,
            1.31
        );

// Material for the knight model.
        Material knightMat = new Material(
            new Color(65, 72, 88),
            0.14,
            0.72,
            0.75,
            90.0,
            0.10,
            0.0,
            1.0
        );

// Material for the floor.
        Material stoneMat = new Material(
            new Color(70, 60, 58),
            0.12,
            0.75,
            0.30,
            24.0,
            0.18,
            0.0,
            1.0
        );

// Material for the walls.
        Material wallMat = new Material(
            new Color(72, 62, 56),
            0.18,
            0.72,
            0.14,
            10.0,
            0.0,
            0.0,
            1.0
        );

// Material for the ceiling.
        Material ceilMat = new Material(
            new Color(40, 35, 32),
            0.08,
            0.50,
            0.06,
            4.0,
            0.0,
            0.0,
            1.0
        );

// Material for the painting frame.
        Material frameMat = new Material(
            new Color(60, 40, 22),
            0.05,
            0.65,
            0.30,
            32.0,
            0.05,
            0.0,
            1.0
        );

// Material for the painting canvas.
        Material canvasMat = new Material(
            new Color(120, 90, 55),
            0.08,
            0.70,
            0.05,
            4.0,
            0.0,
            0.0,
            1.0
        );

// Material for the torch glow.
        Material torchGlowMat = new Material(
            new Color(255, 160, 40),
            0.95,
            0.40,
            1.0,
            256.0,
            0.0,
            0.0,
            1.0
        );

// Material for the torch bracket.
        Material ironMat = new Material(
            new Color(35, 33, 30),
            0.04,
            0.55,
            0.20,
            16.0,
            0.0,
            0.0,
            1.0
        );

// Add the main directional light.
        scene.addLight(new DirectionalLight(
            new Vector3D(0.2, -0.8, -0.5),
            new Color(200, 170, 170),
            0.80
        ));

// Add a point light to the scene.
        scene.addLight(new PointLight(
            new Vector3D(0.0, 5.8, 0.8),
            new Color(255, 30, 30),
            90.0,
            1.2
        ));

        scene.addLight(new PointLight(
            new Vector3D(0.0, 4.5, -1.0),
            new Color(255, 60, 40),
            55.0,
            1.2
        ));

        scene.addLight(new PointLight(
            new Vector3D(-5.2, 1.6, -2.0),
            new Color(255, 170, 60),
            50.0,
            1.6
        ));

        scene.addLight(new PointLight(
            new Vector3D(5.2, 1.6, -2.0),
            new Color(255, 170, 60),
            50.0,
            1.6
        ));

        scene.addLight(new PointLight(
            new Vector3D(0.0, 2.0, -5.5),
            new Color(220, 120, 80),
            40.0,
            1.4
        ));

// Room size values.
        double roomX  =  5.5;
        double roomY0 = -2.5;
        double roomY1 =  6.0;
        double roomZ0 =  6.0;
        double roomZ1 = -7.0;

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY0,  roomZ1),
            stoneMat
        ));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY0,  roomZ1),
            new Vector3D(-roomX, roomY0,  roomZ1),
            stoneMat
        ));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0, roomZ1),
            new Vector3D( roomX, roomY0, roomZ1),
            new Vector3D( roomX, roomY1, roomZ1),
            wallMat
        ));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0, roomZ1),
            new Vector3D( roomX, roomY1, roomZ1),
            new Vector3D(-roomX, roomY1, roomZ1),
            wallMat
        ));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0,  roomZ0),
            new Vector3D(-roomX, roomY0,  roomZ1),
            new Vector3D(-roomX, roomY1,  roomZ1),
            wallMat
        ));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0,  roomZ0),
            new Vector3D(-roomX, roomY1,  roomZ1),
            new Vector3D(-roomX, roomY1,  roomZ0),
            wallMat
        ));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D( roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY0,  roomZ1),
            new Vector3D( roomX, roomY1,  roomZ1),
            wallMat
        ));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D( roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY1,  roomZ1),
            new Vector3D( roomX, roomY1,  roomZ0),
            wallMat
        ));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY1,  roomZ0),
            new Vector3D( roomX, roomY1,  roomZ0),
            new Vector3D( roomX, roomY1,  roomZ1),
            ceilMat
        ));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY1,  roomZ0),
            new Vector3D( roomX, roomY1,  roomZ1),
            new Vector3D(-roomX, roomY1,  roomZ1),
            ceilMat
        ));

// Add a wall painting.
        addSideWallPainting(scene, frameMat, canvasMat,
            -5.46,
             0.3,  2.5,
             2.0, -3.5,
             true
        );

        addSideWallPainting(scene, frameMat, canvasMat,
             5.46,
             0.3,  2.5,
             2.0, -3.5,
             false
        );

// Add a wall torch.
        addTorch(scene, ironMat, torchGlowMat,
            -roomX + 0.05, 1.5, -2.0, true);
        addTorch(scene, ironMat, torchGlowMat,
             roomX - 0.05, 1.5, -2.0, false);

// Load the knight OBJ model.
        ObjReader.load(
            "knight.obj",
            scene,
            knightMat,
            new Vector3D(0.0, -2.5, -1.0),
            1.8,
            0.0
        );

// Ice block size values.
        double slabX0 = -2.0,  slabX1 = 2.0;
        double slabY0 = -2.5,  slabY1 = 3.0;
        double slabZ0 =  0.5,  slabZ1 = 2.0;

// Add the ice block.
        addBox(scene, iceMat, slabX0, slabX1, slabY0, slabY1, slabZ0, slabZ1);

// Build the BVH before rendering.
        scene.buildBVH();

// Render the image.
        Raytracer raytracer = new Raytracer();
        BufferedImage image = raytracer.render(scene);
        raytracer.saveImage(image, settings.getOutputFile());
    }

// Helper method to build a box from triangles.
    private static void addBox(Scene scene, Material mat,
                                double x0, double x1,
                                double y0, double y1,
                                double z0, double z1) {
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z1),
            new Vector3D(x1, y0, z1),
            new Vector3D(x1, y1, z1),
            mat));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z1),
            new Vector3D(x1, y1, z1),
            new Vector3D(x0, y1, z1),
            mat));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x1, y0, z0),
            new Vector3D(x0, y0, z0),
            new Vector3D(x0, y1, z0),
            mat));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x1, y0, z0),
            new Vector3D(x0, y1, z0),
            new Vector3D(x1, y1, z0),
            mat));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z0),
            new Vector3D(x0, y0, z1),
            new Vector3D(x0, y1, z1),
            mat));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z0),
            new Vector3D(x0, y1, z1),
            new Vector3D(x0, y1, z0),
            mat));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x1, y0, z1),
            new Vector3D(x1, y0, z0),
            new Vector3D(x1, y1, z0),
            mat));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x1, y0, z1),
            new Vector3D(x1, y1, z0),
            new Vector3D(x1, y1, z1),
            mat));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x0, y1, z1),
            new Vector3D(x1, y1, z1),
            new Vector3D(x1, y1, z0),
            mat));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x0, y1, z1),
            new Vector3D(x1, y1, z0),
            new Vector3D(x0, y1, z0),
            mat));

        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z0),
            new Vector3D(x1, y0, z0),
            new Vector3D(x1, y0, z1),
            mat));
        // Add a triangle to the scene.
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z0),
            new Vector3D(x1, y0, z1),
            new Vector3D(x0, y0, z1),
            mat));
    }

// Helper method to add a painting.
    private static void addPainting(Scene scene,
                                     Material frameMat, Material canvasMat,
                                     double wallX,
                                     double yBottom, double yTop,
                                     double zNear, double zFar,
                                     boolean faceRight) {
        double offset = faceRight ? 0.02 : -0.02;
        double fx = wallX;
        double cx = wallX + offset;

        if (faceRight) {
            // Add a triangle to the scene.
            scene.addObject(new Triangle(
                new Vector3D(fx, yBottom, zNear),
                new Vector3D(fx, yBottom, zFar),
                new Vector3D(fx, yTop,    zFar),
                frameMat));
            // Add a triangle to the scene.
            scene.addObject(new Triangle(
                new Vector3D(fx, yBottom, zNear),
                new Vector3D(fx, yTop,    zFar),
                new Vector3D(fx, yTop,    zNear),
                frameMat));
        } else {
            // Add a triangle to the scene.
            scene.addObject(new Triangle(
                new Vector3D(fx, yBottom, zFar),
                new Vector3D(fx, yBottom, zNear),
                new Vector3D(fx, yTop,    zNear),
                frameMat));
            // Add a triangle to the scene.
            scene.addObject(new Triangle(
                new Vector3D(fx, yBottom, zFar),
                new Vector3D(fx, yTop,    zNear),
                new Vector3D(fx, yTop,    zFar),
                frameMat));
        }

        double border = 0.18;
        double cyB = yBottom + border;
        double cyT = yTop    - border;
        double czN = zNear   - border;
        double czF = zFar    + border;

        if (faceRight) {
            // Add a triangle to the scene.
            scene.addObject(new Triangle(
                new Vector3D(cx, cyB, czN),
                new Vector3D(cx, cyB, czF),
                new Vector3D(cx, cyT, czF),
                canvasMat));
            // Add a triangle to the scene.
            scene.addObject(new Triangle(
                new Vector3D(cx, cyB, czN),
                new Vector3D(cx, cyT, czF),
                new Vector3D(cx, cyT, czN),
                canvasMat));
        } else {
            // Add a triangle to the scene.
            scene.addObject(new Triangle(
                new Vector3D(cx, cyB, czF),
                new Vector3D(cx, cyB, czN),
                new Vector3D(cx, cyT, czN),
                canvasMat));
            // Add a triangle to the scene.
            scene.addObject(new Triangle(
                new Vector3D(cx, cyB, czF),
                new Vector3D(cx, cyT, czN),
                new Vector3D(cx, cyT, czF),
                canvasMat));
        }
    }

// Helper method to add a torch.
    private static void addTorch(Scene scene,
                                  Material ironMat, Material glowMat,
                                  double wallX, double baseY, double z,
                                  boolean leftWall) {
        double dir    = leftWall ? 1.0 : -1.0;
        double brkLen = 0.35;
        double brkH   = 0.06;
        double brkW   = 0.04;

        double bx0 = wallX;
        double bx1 = wallX + dir * brkLen;
        double by0 = baseY;
        double by1 = baseY + brkH;
        double bz0 = z - brkW;
        double bz1 = z + brkW;
        addBox(scene, ironMat, Math.min(bx0,bx1), Math.max(bx0,bx1), by0, by1, bz0, bz1);

        double fx  = wallX + dir * brkLen;
        double fy0 = by1;
        double fy1 = by1 + 0.22;
        double fz0 = z - 0.08;
        double fz1 = z + 0.08;

        if (leftWall) {
            scene.addObject(new Triangle(
                new Vector3D(fx + 0.02, fy0, fz0),
                new Vector3D(fx + 0.02, fy0, fz1),
                new Vector3D(fx + 0.02, fy1, fz1),
                glowMat));
            scene.addObject(new Triangle(
                new Vector3D(fx + 0.02, fy0, fz0),
                new Vector3D(fx + 0.02, fy1, fz1),
                new Vector3D(fx + 0.02, fy1, fz0),
                glowMat));
        } else {
            scene.addObject(new Triangle(
                new Vector3D(fx - 0.02, fy0, fz1),
                new Vector3D(fx - 0.02, fy0, fz0),
                new Vector3D(fx - 0.02, fy1, fz0),
                glowMat));
            scene.addObject(new Triangle(
                new Vector3D(fx - 0.02, fy0, fz1),
                new Vector3D(fx - 0.02, fy1, fz0),
                new Vector3D(fx - 0.02, fy1, fz1),
                glowMat));
        }
    }

// Helper method for side wall paintings.
    private static void addSideWallPainting(Scene scene,
                                             Material frameMat, Material canvasMat,
                                             double wallX,
                                             double y0, double y1,
                                             double zNear, double zFar,
                                             boolean leftWall) {
        double fx = wallX;
        double cx = leftWall ? wallX + 0.03 : wallX - 0.03;

        if (leftWall) {
            scene.addObject(new Triangle(
                new Vector3D(fx, y0, zNear),
                new Vector3D(fx, y0, zFar),
                new Vector3D(fx, y1, zFar),
                frameMat));
            scene.addObject(new Triangle(
                new Vector3D(fx, y0, zNear),
                new Vector3D(fx, y1, zFar),
                new Vector3D(fx, y1, zNear),
                frameMat));
        } else {
            scene.addObject(new Triangle(
                new Vector3D(fx, y0, zFar),
                new Vector3D(fx, y0, zNear),
                new Vector3D(fx, y1, zNear),
                frameMat));
            scene.addObject(new Triangle(
                new Vector3D(fx, y0, zFar),
                new Vector3D(fx, y1, zNear),
                new Vector3D(fx, y1, zFar),
                frameMat));
        }

        double b  = 0.18;
        double czN = zNear - b;
        double czF = zFar  + b;
        double cyB = y0 + b;
        double cyT = y1 - b;

        if (leftWall) {
            scene.addObject(new Triangle(
                new Vector3D(cx, cyB, czN),
                new Vector3D(cx, cyB, czF),
                new Vector3D(cx, cyT, czF),
                canvasMat));
            scene.addObject(new Triangle(
                new Vector3D(cx, cyB, czN),
                new Vector3D(cx, cyT, czF),
                new Vector3D(cx, cyT, czN),
                canvasMat));
        } else {
            scene.addObject(new Triangle(
                new Vector3D(cx, cyB, czF),
                new Vector3D(cx, cyB, czN),
                new Vector3D(cx, cyT, czN),
                canvasMat));
            scene.addObject(new Triangle(
                new Vector3D(cx, cyB, czF),
                new Vector3D(cx, cyT, czN),
                new Vector3D(cx, cyT, czF),
                canvasMat));
        }
    }
}
