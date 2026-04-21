public class Camera {
    private Vector3D position;
    private int width;
    private int height;

    public Camera(Vector3D position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public Vector3D getPosition() {
        return position;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
