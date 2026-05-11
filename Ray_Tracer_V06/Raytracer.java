import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Raytracer {

    // Render full scene
    public BufferedImage render(Scene scene) {

        Camera camera = scene.getCamera(); // Get camera

        int width = camera.getWidth(); // Image width
        int height = camera.getHeight(); // Image height

        // Create image buffer
        BufferedImage image =
            new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
            );

        // Loop image rows
        for (int y = 0; y < height; y++) {

            // Loop image columns
            for (int x = 0; x < width; x++) {

                // Convert pixel X to screen coordinates
                double screenX =
                    (2.0 * (x + 0.5) / width - 1.0);

                // Convert pixel Y to screen coordinates
                double screenY =
                    (1.0 - 2.0 * (y + 0.5) / height);

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

                // Store closest intersection
                Intersection closest = null;

                // Check all objects
                for (Object3D object : scene.getObjects()) {

                    // Compute ray intersection
                    Intersection hit =
                        object.getIntersection(ray);

                    // Valid hit
                    if (hit != null) {

                        double t =
                            hit.getDistance();

                        // Check clipping planes
                        if (
                            t > camera.getNear()
                            &&
                            t < camera.getFar()
                        ) {

                            // Keep nearest object
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

                // Object hit
                if (closest != null) {

                    // Compute lighting
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

                    // Paint background
                    image.setRGB(
                        x,
                        y,
                        Color.BLACK.getRGB()
                    );
                }
            }
        }

        // Return rendered image
        return image;
    }

    // Compute final object color
    private Color calculateColor(
        Intersection hit,
        Scene scene
    ) {

        Object3D object =
            hit.getObject(); // Hit object

        Vector3D point =
            hit.getPosition(); // Hit position

        // Get phong interpolated normal
        Vector3D normal =
            hit.getNormal();

        // Fallback normal
        if (normal == null) {

            normal =
                object.getNormal(point);
        }

        // Normalize normal vector
        normal = normal.normalize();

        // Base object color
        Color objectColor =
            object.getColor();

        // Final RGB values
        double red = 0;
        double green = 0;
        double blue = 0;

        // Process every light
        for (Light light : scene.getLights()) {

            Vector3D lightDirection;

            // Directional light
            if (
                light.getType()
                ==
                Light.DIRECTIONAL
            ) {

                // Invert direction towards object
                lightDirection =
                    light.getDirection()
                    .multiply(-1)
                    .normalize();

            } else {

                // Point light vector
                lightDirection =
                    light.getPosition()
                    .subtract(point)
                    .normalize();
            }

            // Lambert diffuse equation
            double nDotL =
                normal.dotProduct(
                    lightDirection
                );

            // Avoid negative light
            nDotL =
                Math.max(0, nDotL);

            // Final light intensity
            double finalIntensity =
                light.getIntensity()
                *
                nDotL;

            // Light color
            Color lightColor =
                light.getColor();

            // Diffuse red component
            red +=
                objectColor.getRed()
                *
                (lightColor.getRed() / 255.0)
                *
                finalIntensity;

            // Diffuse green component
            green +=
                objectColor.getGreen()
                *
                (lightColor.getGreen() / 255.0)
                *
                finalIntensity;

            // Diffuse blue component
            blue +=
                objectColor.getBlue()
                *
                (lightColor.getBlue() / 255.0)
                *
                finalIntensity;
        }

        // Clamp RGB values
        int r = clamp(red);
        int g = clamp(green);
        int b = clamp(blue);

        // Return final color
        return new Color(r, g, b);
    }

    // Clamp color values between 0 and 255
    private int clamp(double value) {

        // Minimum value
        if (value < 0) {
            return 0;
        }

        // Maximum value
        if (value > 255) {
            return 255;
        }

        return (int)value;
    }

    // Save rendered image
    public void saveImage(
        BufferedImage image,
        String filename
    ) {

        try {

            // Write PNG image
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

            // Print error
            e.printStackTrace();
        }
    }
}
