public class Camera {
    private Vector3D position; // Camera position in 3D space
    private int width; // Image width
    private int height; // Image height
    private double near; // Minimum visible distance
    private double far; // Maximum visible distance

    public Camera(Vector3D position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.near = 0.1;
        this.far = 100.0;
    }

    public Camera(Vector3D position, int width, int height, double near, double far) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.near = near;
        this.far = far;
    }

    public Vector3D getPosition() { return position; } // Return camera position
    public int getWidth() { return width; } // Return image width
    public int getHeight() { return height; } // Return image height
    public double getNear() { return near; } // Return near clipping plane
    public double getFar() { return far; } // Return far clipping plane
}
