import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Raytracer {

    public BufferedImage render(Scene scene) {
        Camera camera = scene.getCamera();
        int width = camera.getWidth();
        int height = camera.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double screenX = (2.0 * (x + 0.5) / width - 1.0); // Map pixel to screen
                double screenY = (1.0 - 2.0 * (y + 0.5) / height);

                Vector3D direction = new Vector3D(screenX, screenY, -1).normalize();
                Ray ray = new Ray(camera.getPosition(), direction);

                Intersection closest = null;

                for (Object3D object : scene.getObjects()) {
                    Intersection hit = object.getIntersection(ray);

                    if (hit != null) {
                        double t = hit.getDistance();

                        if (t > camera.getNear() && t < camera.getFar()) {
                            if (closest == null || t < closest.getDistance()) {
                                closest = hit;
                            }
                        }
                    }
                }

                if (closest != null) {
                    image.setRGB(x, y, closest.getObject().getColor().getRGB());
                } else {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        return image;
    }

    public void saveImage(BufferedImage image, String filename) {
        try {
            ImageIO.write(image, "png", new File(filename));
            System.out.println("Image saved as " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}