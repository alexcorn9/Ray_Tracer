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
                    image.setRGB(x, y, closest.getObject().getColor().getRGB()); // Object color
                } else {
                    image.setRGB(x, y, Color.WHITE.getRGB()); // Background color
                }
            }
        }

        return image; // Return image
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