// This class is used by the ray tracer.
public class RenderSettings {

    // Image size and render options.
    private int width;
    private int height;
    private int maxBounces;
    private String outputFile;

    // Create settings with default bounces.
    public RenderSettings(int width, int height) {
        this(width, height, 4, "render.png");
    }

    // Create full render settings.
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
