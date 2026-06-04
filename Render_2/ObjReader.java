import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// Loads Wavefront OBJ files and adds triangles to a Scene.
public class ObjReader {

    // Load with default position and scale
    public static void load(String filename, Scene scene, Color color) {
        load(filename, scene, color, new Vector3D(0, 0, -3), 1.0);
    }

    // Load with offset and uniform scale
    public static void load(String filename, Scene scene, Color color,
                            Vector3D offset, double scale) {
        load(filename, scene, Material.matte(color), offset, scale, 0.0);
    }

    // Load with a full Material (no rotation)
    public static void load(String filename, Scene scene, Material material,
                            Vector3D offset, double scale) {
        load(filename, scene, material, offset, scale, 0.0);
    }

    // Load with a full Material AND Y-axis rotation (degrees)
    public static void load(String filename, Scene scene, Material material,
                            Vector3D offset, double scale, double rotationYDeg) {
        List<Vector3D> vertices = new ArrayList<>();
        List<Vector3D> normals  = new ArrayList<>();
        String smoothingGroup   = "off";

        double rad    = Math.toRadians(rotationYDeg);
        double cosA   = Math.cos(rad);
        double sinA   = Math.sin(rad);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "v": {
                        double lx = Double.parseDouble(parts[1]) * scale;
                        double ly = Double.parseDouble(parts[2]) * scale;
                        double lz = Double.parseDouble(parts[3]) * scale;
                        // Rotate around Y axis
                        double rx = cosA * lx + sinA * lz;
                        double rz = -sinA * lx + cosA * lz;
                        vertices.add(new Vector3D(
                            rx + offset.getX(),
                            ly + offset.getY(),
                            rz + offset.getZ()
                        ));
                        break;
                    }
                    case "vn": {
                        double nx = Double.parseDouble(parts[1]);
                        double ny = Double.parseDouble(parts[2]);
                        double nz = Double.parseDouble(parts[3]);
                        // Rotate normal the same way
                        double rnx = cosA * nx + sinA * nz;
                        double rnz = -sinA * nx + cosA * nz;
                        normals.add(new Vector3D(rnx, ny, rnz).normalize());
                        break;
                    }
                    case "s":
                        smoothingGroup = parts[1];
                        break;
                    case "f":
                        addFace(parts, vertices, normals, scene, material, smoothingGroup);
                        break;
                }
            }
            System.out.println("OBJ loaded: " + filename
                + "  v=" + vertices.size() + "  vn=" + normals.size());
        } catch (Exception e) {
            System.err.println("Error loading OBJ: " + filename);
            e.printStackTrace();
        }
    }

    private static void addFace(String[] parts, List<Vector3D> vertices,
                                List<Vector3D> normals, Scene scene,
                                Material material, String smoothingGroup) {
        int n = parts.length - 1;
        int[]  vi = new int[n];
        int[]  ni = new int[n];
        boolean hasNormals = true;

        for (int i = 0; i < n; i++) {
            vi[i] = parseVertexIndex(parts[i + 1], vertices.size());
            ni[i] = parseNormalIndex(parts[i + 1], normals.size());
            if (ni[i] < 0) hasNormals = false;
        }

        boolean smooth = hasNormals
            && !smoothingGroup.equalsIgnoreCase("off")
            && !smoothingGroup.equals("0");

        for (int i = 1; i < n - 1; i++) {
            Vector3D v0 = vertices.get(vi[0]);
            Vector3D v1 = vertices.get(vi[i]);
            Vector3D v2 = vertices.get(vi[i + 1]);

            if (smooth) {
                scene.addObject(new Triangle(v0, v1, v2,
                    normals.get(ni[0]), normals.get(ni[i]), normals.get(ni[i + 1]),
                    material));
            } else {
                scene.addObject(new Triangle(v0, v1, v2, material));
            }
        }
    }

    private static int parseVertexIndex(String token, int count) {
        int idx = Integer.parseInt(token.split("/")[0]);
        return idx < 0 ? count + idx : idx - 1;
    }

    private static int parseNormalIndex(String token, int count) {
        String[] parts = token.split("/");
        if (parts.length < 3 || parts[2].isEmpty()) return -1;
        int idx = Integer.parseInt(parts[2]);
        return idx < 0 ? count + idx : idx - 1;
    }
}