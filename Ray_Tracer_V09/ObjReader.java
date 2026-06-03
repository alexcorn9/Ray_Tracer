import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// Loads Wavefront OBJ files and adds triangles to a Scene.
// Supports vertex normals (vn) for Phong shading and polygon fan triangulation.
public class ObjReader {

    // Load with default position and scale
    public static void load(String filename, Scene scene, Color color) {
        load(filename, scene, color, new Vector3D(0, 0, -3), 1.0);
    }

    // Load with offset and uniform scale
    public static void load(String filename, Scene scene, Color color,
                            Vector3D offset, double scale) {
        load(filename, scene, Material.matte(color), offset, scale);
    }

    // Load with a full Material (allows reflection / refraction on OBJ models)
    public static void load(String filename, Scene scene, Material material,
                            Vector3D offset, double scale) {
        List<Vector3D> vertices = new ArrayList<>();
        List<Vector3D> normals  = new ArrayList<>();
        String smoothingGroup   = "off";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "v":
                        vertices.add(new Vector3D(
                            Double.parseDouble(parts[1]) * scale + offset.getX(),
                            Double.parseDouble(parts[2]) * scale + offset.getY(),
                            Double.parseDouble(parts[3]) * scale + offset.getZ()
                        ));
                        break;
                    case "vn":
                        normals.add(new Vector3D(
                            Double.parseDouble(parts[1]),
                            Double.parseDouble(parts[2]),
                            Double.parseDouble(parts[3])
                        ).normalize());
                        break;
                    case "s":
                        smoothingGroup = parts[1];
                        break;
                    case "f":
                        addFace(parts, vertices, normals, scene, material, smoothingGroup);
                        break;
                    // "vt", "o", "g", "usemtl", "mtllib" – ignored intentionally
                }
            }
            System.out.println("OBJ loaded: " + filename
                + "  v=" + vertices.size() + "  vn=" + normals.size());
        } catch (Exception e) {
            System.err.println("Error loading OBJ: " + filename);
            e.printStackTrace();
        }
    }

    // Fan-triangulate a polygon face and add triangles to the scene
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

        // Fan triangulation: (0,1,2), (0,2,3), (0,3,4), …
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

    // Parse "v", "v/vt", "v/vt/vn", "v//vn" — returns 0-based index
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