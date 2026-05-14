import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Raytracer {

    // Main render function
    public BufferedImage render(Scene scene) {

        // Get scene camera
        Camera camera =
            scene.getCamera();

        // Image resolution
        int width =
            camera.getWidth();

        int height =
            camera.getHeight();

        // Create image buffer
        BufferedImage image =
            new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
            );

        // Loop all image rows
        for (int y = 0; y < height; y++) {

            // Loop all image columns
            for (int x = 0; x < width; x++) {

                // Convert pixel X into screen coordinates
                double screenX =
                    (
                        2.0 * (x + 0.5)
                        / width
                        - 1.0
                    );

                // Convert pixel Y into screen coordinates
                double screenY =
                    (
                        1.0
                        - 2.0 * (y + 0.5)
                        / height
                    );

                // Create ray direction
                Vector3D direction =
                    new Vector3D(
                        screenX,
                        screenY,
                        -1
                    ).normalize();

                // Create camera ray
                Ray ray =
                    new Ray(
                        camera.getPosition(),
                        direction
                    );

                // Find nearest object hit
                Intersection closest =
                    findClosestIntersection(
                        ray,
                        scene,
                        camera.getNear(),
                        camera.getFar()
                    );

                // Valid object hit
                if (closest != null) {

                    // Compute shaded color
                    Color shadedColor =
                        calculateColor(
                            closest,
                            scene
                        );

                    // Paint shaded pixel
                    image.setRGB(
                        x,
                        y,
                        shadedColor.getRGB()
                    );

                } else {

                    // Paint black background
                    image.setRGB(
                        x,
                        y,
                        Color.BLACK.getRGB()
                    );
                }
            }
        }

        // Return final rendered image
        return image;
    }

    // Find closest object intersection
    private Intersection findClosestIntersection(
        Ray ray,
        Scene scene,
        double minDistance,
        double maxDistance
    ) {

        // Store nearest hit
        Intersection closest = null;

        // Check all scene objects
        for (Object3D object : scene.getObjects()) {

            // Compute object intersection
            Intersection hit =
                object.getIntersection(ray);

            // Valid hit
            if (hit != null) {

                // Distance from ray origin
                double t =
                    hit.getDistance();

                // Check clipping range
                if (
                    t > minDistance
                    &&
                    t < maxDistance
                ) {

                    // Keep nearest hit only
                    if (
                        closest == null
                        ||
                        t < closest.getDistance()
                    ) {

                        closest = hit;
                    }
                }
            }
        }

        // Return closest object hit
        return closest;
    }

    // Compute object lighting
    private Color calculateColor(
        Intersection hit,
        Scene scene
    ) {

        // Hit object
        Object3D object =
            hit.getObject();

        // Hit position
        Vector3D point =
            hit.getPosition();

        // Interpolated phong normal
        Vector3D normal =
            hit.getNormal();

        // Fallback flat normal
        if (normal == null) {

            normal =
                object.getNormal(point);
        }

        // Normalize normal vector
        normal =
            normal.normalize();

        // Object base color
        Color objectColor =
            object.getColor();

        // Final RGB accumulators
        double red = 0;
        double green = 0;
        double blue = 0;

        // Ambient light amount
        double ambient = 0.05;

        // Add ambient lighting
        red +=
            objectColor.getRed()
            * ambient;

        green +=
            objectColor.getGreen()
            * ambient;

        blue +=
            objectColor.getBlue()
            * ambient;

        // Process every scene light
        for (Light light : scene.getLights()) {

            // Final light direction
            Vector3D lightDirection;

            // Maximum shadow ray distance
            double maxShadowDistance;

            // Final light intensity
            double lightIntensity;

            // Directional light
            if (
                light.getType()
                ==
                Light.DIRECTIONAL
            ) {

                // Reverse light direction
                lightDirection =
                    light.getDirection()
                    .multiply(-1)
                    .normalize();

                // Infinite shadow distance
                maxShadowDistance =
                    Double.MAX_VALUE;

                // Directional lights keep constant intensity
                lightIntensity =
                    light.getIntensity();

            } else {

                // Vector from point to light
                Vector3D toLight =
                    light.getPosition()
                    .subtract(point);

                // Distance to point light
                double distanceToLight =
                    toLight.magnitude();

                // Normalize light vector
                lightDirection =
                    toLight.normalize();

                // Shadow ray stops at light
                maxShadowDistance =
                    distanceToLight;

                // Light falloff equation
                // LI = intensity / d^power
                lightIntensity =
                    light.getIntensity()
                    /
                    Math.pow(
                        distanceToLight,
                        light.getFalloffPower()
                    );
            }

            // Check if light is blocked
            boolean inShadow =
                isInShadow(
                    hit,
                    point,
                    normal,
                    lightDirection,
                    maxShadowDistance,
                    scene
                );

            // Ignore blocked lights
            if (inShadow) {
                continue;
            }

            // Lambert diffuse equation
            double nDotL =
                normal.dotProduct(
                    lightDirection
                );

            // Prevent negative lighting
            nDotL =
                Math.max(0, nDotL);

            // Final diffuse intensity
            double finalIntensity =
                lightIntensity
                *
                nDotL;

            // Current light color
            Color lightColor =
                light.getColor();

            // Add red contribution
            red +=
                objectColor.getRed()
                *
                (
                    lightColor.getRed()
                    / 255.0
                )
                *
                finalIntensity;

            // Add green contribution
            green +=
                objectColor.getGreen()
                *
                (
                    lightColor.getGreen()
                    / 255.0
                )
                *
                finalIntensity;

            // Add blue contribution
            blue +=
                objectColor.getBlue()
                *
                (
                    lightColor.getBlue()
                    / 255.0
                )
                *
                finalIntensity;
        }

        // Clamp RGB values
        int r = clamp(red);
        int g = clamp(green);
        int b = clamp(blue);

        // Return final shaded color
        return new Color(r, g, b);
    }

    // Shadow ray collision test
    private boolean isInShadow(
        Intersection originalHit,
        Vector3D point,
        Vector3D normal,
        Vector3D lightDirection,
        double maxShadowDistance,
        Scene scene
    ) {

        // Small offset to avoid self collision
        double bias = 1e-4;

        // Move shadow ray origin slightly outside surface
        Vector3D shadowOrigin =
            point.add(
                normal.multiply(bias)
            );

        // Create shadow ray
        Ray shadowRay =
            new Ray(
                shadowOrigin,
                lightDirection
            );

        // Check all scene objects
        for (Object3D object : scene.getObjects()) {

            // Ignore same object
            if (
                object
                ==
                originalHit.getObject()
            ) {

                continue;
            }

            // Compute shadow intersection
            Intersection shadowHit =
                object.getIntersection(shadowRay);

            // Valid shadow collision
            if (shadowHit != null) {

                // Shadow hit distance
                double t =
                    shadowHit.getDistance();

                // Object blocks light
                if (
                    t > bias
                    &&
                    t < maxShadowDistance
                ) {

                    return true;
                }
            }
        }

        // Light is visible
        return false;
    }

    // Clamp RGB values between 0 and 255
    private int clamp(double value) {

        // Minimum value
        if (value < 0) {
            return 0;
        }

        // Maximum value
        if (value > 255) {
            return 255;
        }

        // Convert to integer
        return (int)value;
    }

    // Save PNG image
    public void saveImage(
        BufferedImage image,
        String filename
    ) {

        try {

            // Write image file
            ImageIO.write(
                image,
                "png",
                new File(filename)
            );

            System.out.println(
                "Image saved as "
                +
                filename
            );

        } catch (Exception e) {

            // Print save error
            e.printStackTrace();
        }
    }
}