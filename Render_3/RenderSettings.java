// This class is part of the ray tracer.
public class RenderSettings {

// Render size and output options.
    private int width;
    private int height;
    private int maxBounces;
    private String outputFile;

// Create default render settings.
    public RenderSettings(int width, int height) {
        this(width, height, 4, "render.png");
    }

// Create custom render settings.
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
