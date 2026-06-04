// The same Camera can be reused with any RenderSettings.
public class RenderSettings {

    private int width;          // Output image width  in pixels
    private int height;         // Output image height in pixels
    private int maxBounces;     // Maximum ray recursion depth (reflection + refraction)
    private String outputFile;  // Output filename

    public RenderSettings(int width, int height) {
        this(width, height, 4, "render.png");
    }

    public RenderSettings(int width, int height, int maxBounces, String outputFile) {
        this.width      = width;
        this.height     = height;
        this.maxBounces = maxBounces;
        this.outputFile = outputFile;
    }

    public int    getWidth()      { return width; }
    public int    getHeight()     { return height; }
    public int    getMaxBounces() { return maxBounces; }
    public String getOutputFile() { return outputFile; }
}