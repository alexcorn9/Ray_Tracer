import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.imageio.ImageIO;


public class Raytracer {

    // Shadow bias — same value as original
    private static final double BIAS = 2e-4;

    public BufferedImage render(Scene scene) {
        Camera         camera   = scene.getCamera();
        RenderSettings settings = scene.getSettings();
        int width    = settings.getWidth();
        int height   = settings.getHeight();
        int maxDepth = settings.getMaxBounces();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        double aspectRatio  = (double) width / height;

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Rendering with " + cores + " thread(s) — "
            + width + "×" + height + ", maxBounces=" + maxDepth);

        ForkJoinPool pool = new ForkJoinPool(cores);
        pool.invoke(new RenderBand(image, scene, 0, height, width, height,
                                   aspectRatio, maxDepth));
        pool.shutdown();

        return image;
    }

    private class RenderBand extends RecursiveAction {

        private static final int BAND_SIZE = 16; // rows per leaf task

        private final BufferedImage image;
        private final Scene         scene;
        private final int yStart, yEnd, width, height;
        private final double aspectRatio;
        private final int maxDepth;

        RenderBand(BufferedImage image, Scene scene,
                   int yStart, int yEnd, int width, int height,
                   double aspectRatio, int maxDepth) {
            this.image       = image;
            this.scene       = scene;
            this.yStart      = yStart;
            this.yEnd        = yEnd;
            this.width       = width;
            this.height      = height;
            this.aspectRatio = aspectRatio;
            this.maxDepth    = maxDepth;
        }

        @Override
        protected void compute() {
            if (yEnd - yStart <= BAND_SIZE) {
                // Leaf: render rows directly
                Camera camera = scene.getCamera();
                for (int y = yStart; y < yEnd; y++) {
                    for (int x = 0; x < width; x++) {
                        double screenX = ((2.0 * (x + 0.5) / width)  - 1.0) * aspectRatio;
                        double screenY =   1.0 - (2.0 * (y + 0.5) / height);
                        Ray ray = new Ray(
                            camera.getPosition(),
                            new Vector3D(screenX, screenY, -1).normalize()
                        );
                        Color color = traceRay(ray, scene, maxDepth);
                        image.setRGB(x, y, color.getRGB());
                    }
                }
            } else {
                // Split band in half and run both halves in parallel
                int mid = (yStart + yEnd) / 2;
                RenderBand top = new RenderBand(image, scene, yStart, mid,
                                                width, height, aspectRatio, maxDepth);
                RenderBand bot = new RenderBand(image, scene, mid, yEnd,
                                                width, height, aspectRatio, maxDepth);
                invokeAll(top, bot);
            }
        }
    }

    private Color traceRay(Ray ray, Scene scene, int depth) {
        Camera camera = scene.getCamera();
        Intersection hit = findClosest(ray, scene, camera.getNear(), camera.getFar());

        if (hit == null) return new Color(12, 10, 8);

        return shade(hit, ray, scene, depth);
    }

    private Color shade(Intersection hit, Ray ray, Scene scene, int depth) {
        Object3D obj   = hit.getObject();
        Material mat   = obj.getMaterial();
        Vector3D point = hit.getPosition();

        Vector3D normal = (hit.getNormal() != null)
            ? hit.getNormal().normalize()
            : obj.getNormal(point).normalize();

        Vector3D viewDir = ray.getDirection().multiply(-1).normalize();
        if (normal.dotProduct(viewDir) < 0) normal = normal.multiply(-1);

        double r = mat.getColor().getRed()   * mat.getAmbient();
        double g = mat.getColor().getGreen() * mat.getAmbient();
        double b = mat.getColor().getBlue()  * mat.getAmbient();

        for (Light light : scene.getLights()) {
            Vector3D lightDir     = light.getDirectionFrom(point);
            double   maxShadowDst = light.getMaxShadowDistance(point);

            if (isInShadow(point, normal, lightDir, maxShadowDst, scene))
                continue;

            double nDotL = Math.max(0, normal.dotProduct(lightDir));

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

            r += (mat.getColor().getRed()   * mat.getDiffuse() * nDotL + 255 * spec) * li * lr;
            g += (mat.getColor().getGreen() * mat.getDiffuse() * nDotL + 255 * spec) * li * lg;
            b += (mat.getColor().getBlue()  * mat.getDiffuse() * nDotL + 255 * spec) * li * lb;
        }

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

        if (depth > 0 && mat.getTransparency() > 0) {
            double cosI   = -normal.dotProduct(ray.getDirection());
            boolean inside = cosI < 0;

            double n1 = inside ? mat.getIor() : 1.0;
            double n2 = inside ? 1.0          : mat.getIor();
            Vector3D refrNormal = inside ? normal.multiply(-1) : normal;

            Vector3D refrDir = refract(ray.getDirection(), refrNormal, n1, n2);
            if (refrDir != null) {
                Vector3D refrOrigin = point.subtract(refrNormal.multiply(BIAS));
                Ray      refrRay    = new Ray(refrOrigin, refrDir);
                Color    refrColor  = traceRay(refrRay, scene, depth - 1);

                double k = mat.getTransparency();
                r += refrColor.getRed()   * k;
                g += refrColor.getGreen() * k;
                b += refrColor.getBlue()  * k;
            }
        }

        r = tonemapReinhard(r);
        g = tonemapReinhard(g);
        b = tonemapReinhard(b);

        return new Color(clamp(r), clamp(g), clamp(b));
    }

    private double tonemapReinhard(double v) {
        double n = v / 255.0;
        return (n / (1.0 + n)) * 255.0;
    }

    private Vector3D reflect(Vector3D dir, Vector3D normal) {
        return dir.subtract(normal.multiply(2.0 * dir.dotProduct(normal))).normalize();
    }

    private Vector3D refract(Vector3D dir, Vector3D normal, double n1, double n2) {
        double ratio = n1 / n2;
        double cosI  = -normal.dotProduct(dir);
        double sinT2 = ratio * ratio * (1.0 - cosI * cosI);
        if (sinT2 > 1.0) return null;
        double cosT = Math.sqrt(1.0 - sinT2);
        return dir.multiply(ratio).add(normal.multiply(ratio * cosI - cosT)).normalize();
    }

    private Intersection findClosest(Ray ray, Scene scene, double tMin, double tMax) {
        BVHNode bvh = scene.getBVH();
        if (bvh != null) {
            return bvh.intersect(ray, tMin, tMax);
        }
        // Fallback: original O(n) brute force
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

    private boolean isInShadow(
        Vector3D point, Vector3D normal,
        Vector3D lightDir, double maxDist,
        Scene scene
    ) {
        Vector3D shadowOrigin = point.add(normal.multiply(BIAS));
        Ray      shadowRay    = new Ray(shadowOrigin, lightDir);

        BVHNode bvh = scene.getBVH();
        if (bvh != null) {
            return bvh.intersectShadow(shadowRay, BIAS, maxDist - BIAS);
        }
        // Fallback: original O(n) brute force
        for (Object3D obj : scene.getObjects()) {
            Intersection hit = obj.getIntersection(shadowRay);
            if (hit == null) continue;
            double t = hit.getDistance();
            if (t > BIAS && t < maxDist - BIAS) return true;
        }
        return false;
    }

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