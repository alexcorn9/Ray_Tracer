import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * THE FROZEN GUARDIAN
 * ═══════════════════════════════════════════════════════════════════════════
 * Story: Centuries ago, a knight was defeated and sealed inside a magical ice
 * prison. The knight remains trapped within the translucent block, visible but
 * distorted by refraction. The scene takes place in a small, dark medieval
 * chamber where the ice prison is kept as a cursed relic.
 *
 * NORMAL MODE RUBRIC COVERAGE:
 *  ✔ OBJ mesh          — knight.obj (the frozen guardian)
 *  ✔ Blinn-Phong       — ambient + diffuse + specular + shininess on every surface
 *  ✔ Directional Light — cold moonlight from upper-left
 *  ✔ Point Lights      — two torches on the chamber walls
 *  ✔ Shadows           — BVH shadow rays from all lights
 *  ✔ Reflection        — stone floor (reflectivity 0.18), ice slab (0.12)
 *  ✔ Refraction        — ice/crystal slab (transparency 0.85, ior 1.31)
 *                        deforms the knight silhouette — PRIMARY visual effect
 *  ✔ 2+ bounces        — maxBounces = 2
 *  ✔ No spheres        — geometry built entirely from triangles + OBJ
 *  ✔ No teapots        — knight.obj only
 *  ✔ PNG export        — FrozenGuardian_Test.png
 *
 * SCENE LAYOUT (top-down, Z into screen):
 *
 *   Camera  →  [Ice slab]  →  [Knight]  →  [Back wall]
 *   Z= 4.5      Z= 1.5        Z= -1.0      Z= -7.0
 *
 *   Left wall  X= -5.5   Right wall  X= 5.5
 *   Floor      Y= -2.5   Ceiling     Y=  6.0
 *
 * REFRACTION DESIGN:
 *   The ice slab sits between the camera and the knight.
 *   Every ray that hits the knight first passes through the slab surface
 *   (entry + exit = 2 refraction events within maxBounces=2).
 *   IOR 1.31 (close to natural ice) bends the rays enough to visibly shift
 *   the knight's silhouette — the classic "object seen through thick glass"
 *   distortion.
 */
public class Main {

    public static void main(String[] args) {

        // ── Camera — looking straight toward the ice slab and knight ─────────
        // Positioned in front of the chamber, slightly elevated, dead-center.
        Camera camera = new Camera(
            new Vector3D(0.0, 0.8, 4.5),   // eye position
            0.1,
            200.0
        );

        // ── Test resolution — change to 4096×2160 / "FrozenGuardian_Final.png"
        //    for the final submission render.
        RenderSettings settings = new RenderSettings(
            4096,
            2160,
            2,
            "FrozenGuardian_Test.png"
        );

        Scene scene = new Scene(camera, settings);

        // ════════════════════════════════════════════════════════════════════
        // MATERIALS
        // ════════════════════════════════════════════════════════════════════

        // ── Ice / magic crystal slab ────────────────────────────────────────
        // IOR 1.31 ≈ natural ice.  High transparency so the knight shows
        // through clearly but bent.  Slight cyan tint for the "frozen" feel.
        // Low reflectivity keeps refraction as the dominant effect.
        Material iceMat = new Material(
            new Color(190, 230, 248),   // pale ice-blue
            0.08,                       // ambient
            0.05,                       // diffuse  (almost no diffuse — it's glass)
            0.95,                       // specular (sharp specular highlight on edge)
            256.0,                      // shininess
            0.12,                       // reflectivity (subtle mirror glint)
            0.85,                       // transparency  ← REFRACTION HERO
            1.31                        // ior (ice ≈ 1.31)
        );

        // ── Knight — dark iron/steel, brighter ambient so he shows through slab
        Material knightMat = new Material(
            new Color(65, 72, 88),
            0.14,                       // raised — knight must be visible through ice
            0.72,
            0.75,
            90.0,
            0.10,
            0.0,
            1.0
        );

        // ── Stone floor — brighter, picks up red light nicely ───────────────
        Material stoneMat = new Material(
            new Color(70, 60, 58),
            0.12,                       // raised ambient — floor is visible
            0.75,
            0.30,
            24.0,
            0.18,
            0.0,
            1.0
        );

        // ── Stone walls — visible ambient so back wall is never pure black ──
        Material wallMat = new Material(
            new Color(72, 62, 56),
            0.18,                       // raised — back wall must be visible through ice
            0.72,
            0.14,
            10.0,
            0.0,
            0.0,
            1.0
        );

        // ── Ceiling — dark but not pure black ────────────────────────────────
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

        // ── Medieval painting frame — dark aged wood ─────────────────────────
        Material frameMat = new Material(
            new Color(60, 40, 22),      // dark walnut
            0.05,
            0.65,
            0.30,
            32.0,
            0.05,
            0.0,
            1.0
        );

        // ── Painting canvas — warm parchment/ochre tones ─────────────────────
        Material canvasMat = new Material(
            new Color(120, 90, 55),     // aged parchment
            0.08,
            0.70,
            0.05,
            4.0,
            0.0,
            0.0,
            1.0
        );

        // ── Torch glow quads (self-illuminating amber) ───────────────────────
        Material torchGlowMat = new Material(
            new Color(255, 160, 40),
            0.95,                       // high ambient = self-glowing
            0.40,
            1.0,
            256.0,
            0.0,
            0.0,
            1.0
        );

        // ── Torch bracket (iron) ─────────────────────────────────────────────
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

        // ════════════════════════════════════════════════════════════════════
        // LIGHTING
        // ════════════════════════════════════════════════════════════════════

        // ── Directional light — pale fill so nothing is pitch-black ────────────
        scene.addLight(new DirectionalLight(
            new Vector3D(0.2, -0.8, -0.5),
            new Color(200, 170, 170),         // warm-neutral fill
            0.80
        ));

        // ── PRIMARY: red overhead spotlight directly above the knight/slab ────
        // This is the dominant light — crimson, high intensity, linear falloff
        // so it reaches across the scene without inverting.
        scene.addLight(new PointLight(
            new Vector3D(0.0, 5.8, 0.8),      // directly above the ice slab
            new Color(255, 30, 30),            // deep red
            90.0,                              // high intensity
            1.2                                // gentle falloff (< 2 = reaches farther)
        ));

        // ── Secondary red — slightly behind the slab, lights the knight ──────
        scene.addLight(new PointLight(
            new Vector3D(0.0, 4.5, -1.0),     // above and behind the slab
            new Color(255, 60, 40),
            55.0,
            1.2
        ));

        // ── Left-wall torch — still amber but brighter ───────────────────────
        scene.addLight(new PointLight(
            new Vector3D(-5.2, 1.6, -2.0),
            new Color(255, 170, 60),
            50.0,
            1.6
        ));

        // ── Right-wall torch ─────────────────────────────────────────────────
        scene.addLight(new PointLight(
            new Vector3D(5.2, 1.6, -2.0),
            new Color(255, 170, 60),
            50.0,
            1.6
        ));

        // ── Back-wall fill — illuminates the wall behind the slab so it has
        //    color and texture visible through the ice refraction.
        //    Position: behind the knight, close to the back wall, pointing forward.
        scene.addLight(new PointLight(
            new Vector3D(0.0, 2.0, -5.5),    // near the back wall
            new Color(220, 120, 80),          // warm reddish-orange
            40.0,
            1.4
        ));
        // ════════════════════════════════════════════════════════════════════

        // Coordinate system:
        //   X: left (-) / right (+)
        //   Y: down (-) / up (+)
        //   Z: toward camera (+) / into scene (-)

        double roomX  =  5.5;   // half-width of room
        double roomY0 = -2.5;   // floor Y
        double roomY1 =  6.0;   // ceiling Y
        double roomZ0 =  6.0;   // near wall (behind camera, not rendered)
        double roomZ1 = -7.0;   // back wall

        // ── Floor (2 triangles) ──────────────────────────────────────────────
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY0,  roomZ1),
            stoneMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY0,  roomZ1),
            new Vector3D(-roomX, roomY0,  roomZ1),
            stoneMat
        ));

        // ── Back wall ────────────────────────────────────────────────────────
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0, roomZ1),
            new Vector3D( roomX, roomY0, roomZ1),
            new Vector3D( roomX, roomY1, roomZ1),
            wallMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0, roomZ1),
            new Vector3D( roomX, roomY1, roomZ1),
            new Vector3D(-roomX, roomY1, roomZ1),
            wallMat
        ));

        // ── Left wall ────────────────────────────────────────────────────────
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0,  roomZ0),
            new Vector3D(-roomX, roomY0,  roomZ1),
            new Vector3D(-roomX, roomY1,  roomZ1),
            wallMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY0,  roomZ0),
            new Vector3D(-roomX, roomY1,  roomZ1),
            new Vector3D(-roomX, roomY1,  roomZ0),
            wallMat
        ));

        // ── Right wall ───────────────────────────────────────────────────────
        scene.addObject(new Triangle(
            new Vector3D( roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY0,  roomZ1),
            new Vector3D( roomX, roomY1,  roomZ1),
            wallMat
        ));
        scene.addObject(new Triangle(
            new Vector3D( roomX, roomY0,  roomZ0),
            new Vector3D( roomX, roomY1,  roomZ1),
            new Vector3D( roomX, roomY1,  roomZ0),
            wallMat
        ));

        // ── Ceiling ──────────────────────────────────────────────────────────
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY1,  roomZ0),
            new Vector3D( roomX, roomY1,  roomZ0),
            new Vector3D( roomX, roomY1,  roomZ1),
            ceilMat
        ));
        scene.addObject(new Triangle(
            new Vector3D(-roomX, roomY1,  roomZ0),
            new Vector3D( roomX, roomY1,  roomZ1),
            new Vector3D(-roomX, roomY1,  roomZ1),
            ceilMat
        ));

        // ════════════════════════════════════════════════════════════════════
        // GEOMETRY — MEDIEVAL PAINTINGS (on SIDE WALLS, facing the camera)
        // ════════════════════════════════════════════════════════════════════
        // Mounted flat on X=+-5.46 walls. The face is perpendicular to the
        // camera ray so they render as full rectangles, clearly visible.

        // Left wall painting (normal +X)
        addSideWallPainting(scene, frameMat, canvasMat,
            -5.46,
             0.3,  2.5,
             2.0, -3.5,
             true
        );

        // Right wall painting (normal -X)
        addSideWallPainting(scene, frameMat, canvasMat,
             5.46,
             0.3,  2.5,
             2.0, -3.5,
             false
        );

        // ════════════════════════════════════════════════════════════════════
        // GEOMETRY — WALL TORCHES
        // ════════════════════════════════════════════════════════════════════
        // Left torch: iron bracket stick + glowing flame quad
        addTorch(scene, ironMat, torchGlowMat,
            -roomX + 0.05, 1.5, -2.0, true);   // left wall
        addTorch(scene, ironMat, torchGlowMat,
             roomX - 0.05, 1.5, -2.0, false);  // right wall

        // ════════════════════════════════════════════════════════════════════
        // GEOMETRY — KNIGHT  (the frozen guardian)
        // ════════════════════════════════════════════════════════════════════
        //
        // Position:  centered at (0, -2.5, -1.0) — standing on the floor
        // Scale:     1.8  — sized to fill ~70% of the ice slab
        // Rotation:  180° — knight faces toward the camera (toward the viewer)
        //
        // The knight.obj typically spans roughly Y[-1, 1] in its own space,
        // so at scale=1.8 it will be ~3.6 units tall, feet at Y=-2.5.
        // Adjust scale if the model uses a different unit convention.

        ObjReader.load(
            "knight.obj",
            scene,
            knightMat,
            new Vector3D(0.0, -2.5, -1.0),   // offset: centered, on floor, behind slab
            1.8,                               // scale
            0.0                                // rotation Y: 0 = faces camera (model default front)
        );

        // ════════════════════════════════════════════════════════════════════
        // GEOMETRY — ICE / CRYSTAL SLAB  (the "frozen prison")
        // ════════════════════════════════════════════════════════════════════
        //
        // The slab is a tall rectangular box built from 12 triangles (6 faces).
        // It sits between the camera (Z=4.5) and the knight (Z=-1.0).
        //
        //   Width:   4.0 units  (X from -2.0 to +2.0)
        //   Height:  5.5 units  (Y from -2.5 to +3.0)
        //   Depth:   1.5 units  (Z from +0.5 to +2.0)  — thick slab = more refraction
        //
        // The camera ray hits the front face (Z=2.0), refracts, traverses the
        // slab, exits the back face (Z=0.5), refracts again, then hits the knight.
        // Two refraction events consume exactly the 2 available bounces.

        double slabX0 = -2.0,  slabX1 = 2.0;
        double slabY0 = -2.5,  slabY1 = 3.0;
        double slabZ0 =  0.5,  slabZ1 = 2.0;   // Z1 = front face (closer to camera)

        addBox(scene, iceMat, slabX0, slabX1, slabY0, slabY1, slabZ0, slabZ1);

        // ════════════════════════════════════════════════════════════════════
        // BUILD BVH + RENDER
        // ════════════════════════════════════════════════════════════════════
        scene.buildBVH();

        Raytracer raytracer = new Raytracer();
        BufferedImage image = raytracer.render(scene);
        raytracer.saveImage(image, settings.getOutputFile());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Builds a closed axis-aligned box from 12 triangles (6 faces × 2 triangles).
     * Used for the ice slab — each face gets the same material so refraction/
     * reflection works correctly on every surface.
     *
     * Winding order: counter-clockwise when viewed from outside, so normals
     * point outward — consistent with the Raytracer's "flip if backface" logic.
     */
    private static void addBox(Scene scene, Material mat,
                                double x0, double x1,
                                double y0, double y1,
                                double z0, double z1) {
        // Front face  (Z = z1, normal +Z toward camera)
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z1),
            new Vector3D(x1, y0, z1),
            new Vector3D(x1, y1, z1),
            mat));
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z1),
            new Vector3D(x1, y1, z1),
            new Vector3D(x0, y1, z1),
            mat));

        // Back face   (Z = z0, normal -Z away from camera)
        scene.addObject(new Triangle(
            new Vector3D(x1, y0, z0),
            new Vector3D(x0, y0, z0),
            new Vector3D(x0, y1, z0),
            mat));
        scene.addObject(new Triangle(
            new Vector3D(x1, y0, z0),
            new Vector3D(x0, y1, z0),
            new Vector3D(x1, y1, z0),
            mat));

        // Left face   (X = x0, normal -X)
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z0),
            new Vector3D(x0, y0, z1),
            new Vector3D(x0, y1, z1),
            mat));
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z0),
            new Vector3D(x0, y1, z1),
            new Vector3D(x0, y1, z0),
            mat));

        // Right face  (X = x1, normal +X)
        scene.addObject(new Triangle(
            new Vector3D(x1, y0, z1),
            new Vector3D(x1, y0, z0),
            new Vector3D(x1, y1, z0),
            mat));
        scene.addObject(new Triangle(
            new Vector3D(x1, y0, z1),
            new Vector3D(x1, y1, z0),
            new Vector3D(x1, y1, z1),
            mat));

        // Top face    (Y = y1, normal +Y)
        scene.addObject(new Triangle(
            new Vector3D(x0, y1, z1),
            new Vector3D(x1, y1, z1),
            new Vector3D(x1, y1, z0),
            mat));
        scene.addObject(new Triangle(
            new Vector3D(x0, y1, z1),
            new Vector3D(x1, y1, z0),
            new Vector3D(x0, y1, z0),
            mat));

        // Bottom face (Y = y0, normal -Y)
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z0),
            new Vector3D(x1, y0, z0),
            new Vector3D(x1, y0, z1),
            mat));
        scene.addObject(new Triangle(
            new Vector3D(x0, y0, z0),
            new Vector3D(x1, y0, z1),
            new Vector3D(x0, y0, z1),
            mat));
    }

    /**
     * Builds a flat wall-mounted painting from triangles.
     * The painting has an outer frame and an inset canvas quad.
     *
     * @param wallX    X position of the wall surface
     * @param yBottom  bottom Y of the frame
     * @param yTop     top Y of the frame
     * @param zLeft    Z of one edge (typically the near edge)
     * @param zRight   Z of the other edge (the far edge)
     * @param faceRight true = normal points +X (left wall), false = normal points -X (right wall)
     */
    private static void addPainting(Scene scene,
                                     Material frameMat, Material canvasMat,
                                     double wallX,
                                     double yBottom, double yTop,
                                     double zNear, double zFar,
                                     boolean faceRight) {
        double offset = faceRight ? 0.02 : -0.02;   // canvas sits slightly proud of frame
        double fx = wallX;
        double cx = wallX + offset;

        // Outer frame (two triangles, same plane as wall)
        if (faceRight) {
            // left wall — winding so normal faces +X
            scene.addObject(new Triangle(
                new Vector3D(fx, yBottom, zNear),
                new Vector3D(fx, yBottom, zFar),
                new Vector3D(fx, yTop,    zFar),
                frameMat));
            scene.addObject(new Triangle(
                new Vector3D(fx, yBottom, zNear),
                new Vector3D(fx, yTop,    zFar),
                new Vector3D(fx, yTop,    zNear),
                frameMat));
        } else {
            // right wall — winding so normal faces -X
            scene.addObject(new Triangle(
                new Vector3D(fx, yBottom, zFar),
                new Vector3D(fx, yBottom, zNear),
                new Vector3D(fx, yTop,    zNear),
                frameMat));
            scene.addObject(new Triangle(
                new Vector3D(fx, yBottom, zFar),
                new Vector3D(fx, yTop,    zNear),
                new Vector3D(fx, yTop,    zFar),
                frameMat));
        }

        // Inset canvas (slightly inside the frame border)
        double border = 0.18;
        double cyB = yBottom + border;
        double cyT = yTop    - border;
        double czN = zNear   - border;
        double czF = zFar    + border;

        if (faceRight) {
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

    /**
     * Builds a simple medieval wall torch:
     *   - a short iron bracket (thin vertical slab)
     *   - a glowing flame quad (small bright orange quad at the top)
     *
     * @param wallX   X of the wall the torch is mounted on
     * @param baseY   Y of the torch bracket base
     * @param z       Z position along the wall
     * @param leftWall true = mounted on left wall (bracket extends +X), false = right wall (-X)
     */
    private static void addTorch(Scene scene,
                                  Material ironMat, Material glowMat,
                                  double wallX, double baseY, double z,
                                  boolean leftWall) {
        double dir    = leftWall ? 1.0 : -1.0;
        double brkLen = 0.35;   // how far the bracket sticks out from the wall
        double brkH   = 0.06;   // bracket vertical thickness
        double brkW   = 0.04;   // bracket depth (Z)

        // Bracket — thin horizontal stick protruding from the wall
        double bx0 = wallX;
        double bx1 = wallX + dir * brkLen;
        double by0 = baseY;
        double by1 = baseY + brkH;
        double bz0 = z - brkW;
        double bz1 = z + brkW;
        addBox(scene, ironMat, Math.min(bx0,bx1), Math.max(bx0,bx1), by0, by1, bz0, bz1);

        // Flame glow quad — small bright square at the end of the bracket
        double fx  = wallX + dir * brkLen;
        double fy0 = by1;
        double fy1 = by1 + 0.22;
        double fz0 = z - 0.08;
        double fz1 = z + 0.08;

        // Two triangles for the flame face (facing into room)
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

    /**
     * Painting mounted on a SIDE WALL (left: normal +X, right: normal -X).
     * wallX   = X of the wall surface
     * y0, y1  = bottom and top Y
     * zNear, zFar = Z extents of the painting (zNear > zFar since Z goes into scene)
     * leftWall = true → left wall (normal +X), false → right wall (normal -X)
     */
    private static void addSideWallPainting(Scene scene,
                                             Material frameMat, Material canvasMat,
                                             double wallX,
                                             double y0, double y1,
                                             double zNear, double zFar,
                                             boolean leftWall) {
        double fx = wallX;
        double cx = leftWall ? wallX + 0.03 : wallX - 0.03; // canvas proud of frame

        if (leftWall) {
            // Normal +X: vertices wound CCW when viewed from +X direction
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
            // Normal -X: reverse winding
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

        // Inset canvas with border
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