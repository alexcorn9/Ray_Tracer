import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// This class is used by the ray tracer.
public class ObjReader {

    // Load an OBJ with a simple color.
    public static void load(String filename, Scene scene, Color color) {
        load(filename, scene, color, new Vector3D(0, 0, -3), 1.0);
    }

    // Load an OBJ with position and scale.
    public static void load(String filename, Scene scene, Color color,
                            Vector3D offset, double scale) {
        load(filename, scene, Material.matte(color), offset, scale);
    }

    // Load an OBJ with a material.
    public static void load(String filename, Scene scene, Material material,
                            Vector3D offset, double scale) {
        // Store the vertices from the OBJ file.
List<Vector3D> vertices = new ArrayList<>();
        List<Vector3D> normals  = new ArrayList<>();
        String smoothingGroup   = "off";

        // Read the OBJ file line by line.
try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    // Read a vertex.
case "v":
                        vertices.add(new Vector3D(
                            Double.parseDouble(parts[1]) * scale + offset.getX(),
                            Double.parseDouble(parts[2]) * scale + offset.getY(),
                            Double.parseDouble(parts[3]) * scale + offset.getZ()
                        ));
                        break;
                    // Read a normal.
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
                    // Read a face.
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

    // Split each face into triangles.
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

    // Convert OBJ vertex index to Java index.
    private static int parseVertexIndex(String token, int count) {
        int idx = Integer.parseInt(token.split("/")[0]);
        return idx < 0 ? count + idx : idx - 1;
    }

    // Convert OBJ normal index to Java index.
    private static int parseNormalIndex(String token, int count) {
        String[] parts = token.split("/");
        if (parts.length < 3 || parts[2].isEmpty()) return -1;
        int idx = Integer.parseInt(parts[2]);
        return idx < 0 ? count + idx : idx - 1;
    }
}
