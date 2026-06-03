import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Raytracer {

    // Offset applied to all secondary ray origins to prevent self-intersection.
    // Applied in the normal direction so it works for any geometry including OBJs.
    private static final double BIAS = 1e-4;

    // ── Public render entry point ─────────────────────────────────────────────
    public BufferedImage render(Scene scene) {
        Camera         camera   = scene.getCamera();
        RenderSettings settings = scene.getSettings();
        int width    = settings.getWidth();
        int height   = settings.getHeight();
        int maxDepth = settings.getMaxBounces();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // Map pixel to [-1,1] screen space (maintains aspect ratio 1:1)
                double screenX = (2.0 * (x + 0.5) / width)  - 1.0;
                double screenY = 1.0 - (2.0 * (y + 0.5) / height);

                Ray ray = new Ray(
                    camera.getPosition(),
                    new Vector3D(screenX, screenY, -1).normalize()
                );

                Color color = traceRay(ray, scene, maxDepth);
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }

    // ── Core recursive ray tracer ─────────────────────────────────────────────
    private Color traceRay(Ray ray, Scene scene, int depth) {
        Camera camera = scene.getCamera();
        Intersection hit = findClosest(ray, scene, camera.getNear(), camera.getFar());
        if (hit == null) return Color.BLACK;

        return shade(hit, ray, scene, depth);
    }

    // ── Shading: Blinn-Phong + shadows + reflection + refraction ─────────────
    private Color shade(Intersection hit, Ray ray, Scene scene, int depth) {
        Object3D obj      = hit.getObject();
        Material mat      = obj.getMaterial();
        Vector3D point    = hit.getPosition();

        // Prefer interpolated normal stored in intersection, fall back to geometry normal
        Vector3D normal = (hit.getNormal() != null)
            ? hit.getNormal().normalize()
            : obj.getNormal(point).normalize();

        // Make sure the normal faces the incoming ray
        Vector3D viewDir = ray.getDirection().multiply(-1).normalize();
        if (normal.dotProduct(viewDir) < 0) normal = normal.multiply(-1);

        double r = mat.getColor().getRed()   * mat.getAmbient();
        double g = mat.getColor().getGreen() * mat.getAmbient();
        double b = mat.getColor().getBlue()  * mat.getAmbient();

        // ── Per-light Blinn-Phong ──────────────────────────────────────────
        for (Light light : scene.getLights()) {
            Vector3D lightDir     = light.getDirectionFrom(point);
            double   maxShadowDst = light.getMaxShadowDistance(point);

            if (isInShadow(point, normal, lightDir, maxShadowDst, hit.getObject(), scene))
                continue;

            // Diffuse (Lambert)
            double nDotL = Math.max(0, normal.dotProduct(lightDir));

            // Specular (Blinn-Phong half-vector)
            double spec = 0;
            if (mat.getSpecular() > 0 && nDotL > 0) {
                Vector3D halfVec = lightDir.add(viewDir).normalize();
                double   nDotH   = Math.max(0, normal.dotProduct(halfVec));
                spec = mat.getSpecular() * Math.pow(nDotH, mat.getShininess());
            }

            double li = light.getIntensityAt(point);
            Color  lc = light.getColor();

            double lr = lc.getRed()   / 255.0;
            double lg = lc.getGreen() / 255.0;
            double lb = lc.getBlue()  / 255.0;

            // Diffuse contribution uses object colour; specular uses light colour directly
            r += (mat.getColor().getRed()   * mat.getDiffuse() * nDotL + 255 * spec) * li * lr;
            g += (mat.getColor().getGreen() * mat.getDiffuse() * nDotL + 255 * spec) * li * lg;
            b += (mat.getColor().getBlue()  * mat.getDiffuse() * nDotL + 255 * spec) * li * lb;
        }

        // ── Reflection ────────────────────────────────────────────────────
        if (depth > 0 && mat.getReflectivity() > 0) {
            Vector3D reflDir    = reflect(ray.getDirection(), normal);
            Vector3D reflOrigin = point.add(normal.multiply(BIAS));
            Ray      reflRay    = new Ray(reflOrigin, reflDir);
            Color    reflColor  = traceRay(reflRay, scene, depth - 1);

            double k = mat.getReflectivity();
            r += reflColor.getRed()   * k;
            g += reflColor.getGreen() * k;
            b += reflColor.getBlue()  * k;
        }

        // ── Refraction ────────────────────────────────────────────────────
        if (depth > 0 && mat.getTransparency() > 0) {
            double cosI   = -normal.dotProduct(ray.getDirection());
            boolean inside = cosI < 0;

            // Entering or exiting the medium?
            double n1 = inside ? mat.getIor() : 1.0;
            double n2 = inside ? 1.0          : mat.getIor();
            Vector3D refrNormal = inside ? normal.multiply(-1) : normal;

            Vector3D refrDir = refract(ray.getDirection(), refrNormal, n1, n2);
            if (refrDir != null) { // null = Total Internal Reflection
                Vector3D refrOrigin = point.subtract(refrNormal.multiply(BIAS));
                Ray      refrRay    = new Ray(refrOrigin, refrDir);
                Color    refrColor  = traceRay(refrRay, scene, depth - 1);

                double k = mat.getTransparency();
                r += refrColor.getRed()   * k;
                g += refrColor.getGreen() * k;
                b += refrColor.getBlue()  * k;
            }
        }

        return new Color(clamp(r), clamp(g), clamp(b));
    }

    // ── Reflection vector ─────────────────────────────────────────────────────
    // R = D - 2(D·N)N
    private Vector3D reflect(Vector3D dir, Vector3D normal) {
        return dir.subtract(normal.multiply(2.0 * dir.dotProduct(normal))).normalize();
    }

    // ── Snell's Law refraction vector ─────────────────────────────────────────
    // Returns null on Total Internal Reflection.
    private Vector3D refract(Vector3D dir, Vector3D normal, double n1, double n2) {
        double ratio = n1 / n2;
        double cosI  = -normal.dotProduct(dir);
        double sinT2 = ratio * ratio * (1.0 - cosI * cosI);
        if (sinT2 > 1.0) return null; // Total Internal Reflection
        double cosT = Math.sqrt(1.0 - sinT2);
        return dir.multiply(ratio).add(normal.multiply(ratio * cosI - cosT)).normalize();
    }

    // ── Shadow test ───────────────────────────────────────────────────────────
    // Uses normal-based bias: moves the shadow ray origin along the surface normal
    // so it cannot immediately re-intersect the same surface regardless of geometry.
    // This works correctly for OBJ meshes where each triangle is a separate object.
    private boolean isInShadow(
        Vector3D point, Vector3D normal,
        Vector3D lightDir, double maxDist,
        Object3D sourceObject, Scene scene
    ) {
        // Offset origin along normal to avoid self-intersection
        Vector3D shadowOrigin = point.add(normal.multiply(BIAS));
        Ray      shadowRay    = new Ray(shadowOrigin, lightDir);

        for (Object3D obj : scene.getObjects()) {
            Intersection hit = obj.getIntersection(shadowRay);
            if (hit == null) continue;
            double t = hit.getDistance();
            // Must be past the bias epsilon and before the light
            if (t > BIAS && t < maxDist) return true;
        }
        return false;
    }

    // ── Closest intersection ──────────────────────────────────────────────────
    private Intersection findClosest(Ray ray, Scene scene, double tMin, double tMax) {
        Intersection closest = null;
        for (Object3D obj : scene.getObjects()) {
            Intersection hit = obj.getIntersection(ray);
            if (hit == null) continue;
            double t = hit.getDistance();
            if (t > tMin && t < tMax && (closest == null || t < closest.getDistance()))
                closest = hit;
        }
        return closest;
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    private int clamp(double v) {
        if (v < 0)   return 0;
        if (v > 255) return 255;
        return (int) v;
    }

    public void saveImage(BufferedImage image, String filename) {
        try {
            ImageIO.write(image, "png", new File(filename));
            System.out.println("Image saved: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}