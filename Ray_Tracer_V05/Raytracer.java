import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Raytracer {

    public BufferedImage render(Scene scene) {
        Camera camera = scene.getCamera(); // Get camera
        int width = camera.getWidth(); // Image width
        int height = camera.getHeight(); // Image height

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // Create image

        for (int y = 0; y < height; y++) { // Loop rows
            for (int x = 0; x < width; x++) { // Loop columns

                // Convert pixel to screen space (-1 to 1)
                double screenX = (2.0 * (x + 0.5) / width - 1.0);
                double screenY = (1.0 - 2.0 * (y + 0.5) / height);

                Vector3D direction = new Vector3D(screenX, screenY, -1).normalize(); // Ray direction
                Ray ray = new Ray(camera.getPosition(), direction); // Create ray

                Intersection closest = null; // Closest hit

                for (Object3D object : scene.getObjects()) { // Check all objects
                    Intersection hit = object.getIntersection(ray); // Intersect ray

                    if (hit != null) { // If hit
                        double t = hit.getDistance(); // Distance

                        // Check if inside camera range
                        if (t > camera.getNear() && t < camera.getFar()) {
                            // Keep closest hit
                            if (closest == null || t < closest.getDistance()) {
                                closest = hit;
                            }
                        }
                    }
                }

                // Set pixel color
                if (closest != null) {
                    Color shadedColor = calculateColor(closest, scene); // Calculate shading color
                    image.setRGB(x, y, shadedColor.getRGB()); // Object shaded color
                } else {
                    image.setRGB(x, y, Color.BLACK.getRGB()); // Background color
                }
            }
        }

        return image; // Return image
    }

    private Color calculateColor(Intersection hit, Scene scene) {
        Object3D object = hit.getObject(); // Get hit object
        Vector3D point = hit.getPosition(); // Get hit point
        Vector3D normal = object.getNormal(point); // Get object normal

        Color objectColor = object.getColor(); // Get object color

        double red = 0;
        double green = 0;
        double blue = 0;

        for (Light light : scene.getLights()) { // Check all lights

            Vector3D lightDirection = light.getDirection().multiply(-1).normalize(); // Direction to light

            double nDotL = normal.dotProduct(lightDirection); // Lambertian dot product
            nDotL = Math.max(0, nDotL); // Avoid negative light

            double intensity = light.getIntensity() * nDotL; // Final light intensity

            Color lightColor = light.getColor(); // Get light color

            red += objectColor.getRed() * (lightColor.getRed() / 255.0) * intensity; // Red diffuse
            green += objectColor.getGreen() * (lightColor.getGreen() / 255.0) * intensity; // Green diffuse
            blue += objectColor.getBlue() * (lightColor.getBlue() / 255.0) * intensity; // Blue diffuse
        }

        int r = (int)Math.min(255, red); // Clamp red
        int g = (int)Math.min(255, green); // Clamp green
        int b = (int)Math.min(255, blue); // Clamp blue

        return new Color(r, g, b); // Return final color
    }

    public void saveImage(BufferedImage image, String filename) {
        try {
            ImageIO.write(image, "png", new File(filename)); // Save file
            System.out.println("Image saved as " + filename); // Print message
        } catch (Exception e) {
            e.printStackTrace(); // Print error
        }
    }
}
