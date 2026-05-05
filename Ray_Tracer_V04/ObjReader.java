import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ObjReader {

    // Simple version with default position and size
    public static void load(String filename, Scene scene, Color color) {
        load(filename, scene, color, new Vector3D(0, 0, -3), 1.0);
    }

    // Main function to read the OBJ file
    public static void load(String filename, Scene scene, Color color, Vector3D offset, double scale) {
        
        List<Vector3D> vertices = new ArrayList<>(); // Stores all points of the model
        List<Vector3D> normals = new ArrayList<>(); // Stores normals (not used yet)
        List<double[]> textures = new ArrayList<>(); // Stores textures (not used yet)

        String objectName = "default"; // Name of object
        String groupName = "default"; // Name of group
        boolean smooth = false; // Smooth shading (not used)

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            // Read file line by line
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines or comments
                if (line.length() == 0 || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+"); // Split text by spaces

                // "v" = vertex (a point in 3D)
                if (parts[0].equals("v")) {
                    double x = Double.parseDouble(parts[1]) * scale + offset.getX();
                    double y = Double.parseDouble(parts[2]) * scale + offset.getY();
                    double z = Double.parseDouble(parts[3]) * scale + offset.getZ();

                    vertices.add(new Vector3D(x, y, z)); // Save point

                // "vn" = normal (direction, not used yet)
                } else if (parts[0].equals("vn")) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    normals.add(new Vector3D(x, y, z));

                // "vt" = texture coords (not used)
                } else if (parts[0].equals("vt")) {
                    double u = Double.parseDouble(parts[1]);
                    double v = 0;
                    if (parts.length > 2) v = Double.parseDouble(parts[2]);

                    textures.add(new double[] { u, v });

                // Object name
                } else if (parts[0].equals("o")) {
                    objectName = getName(parts);

                // Group name
                } else if (parts[0].equals("g")) {
                    groupName = getName(parts);

                // Smooth shading on/off (not used)
                } else if (parts[0].equals("s")) {
                    smooth = !parts[1].equalsIgnoreCase("off") && !parts[1].equals("0");

                // "f" = face (a triangle or polygon)
                } else if (parts[0].equals("f")) {
                    addFace(parts, vertices, scene, color); // Convert to triangles
                }
            }

            // Print info in console
            System.out.println("OBJ loaded: " + filename);
            System.out.println("Object: " + objectName + ", Group: " + groupName + ", Smooth: " + smooth);
            System.out.println("v: " + vertices.size() + ", vt: " + textures.size() + ", vn: " + normals.size());

        } catch (Exception e) {
            System.out.println("Error loading OBJ file: " + filename);
            e.printStackTrace();
        }
    }

    // Convert a face into triangles
    private static void addFace(String[] parts, List<Vector3D> vertices, Scene scene, Color color) {
        
        int[] faceIndices = new int[parts.length - 1]; // Stores indices of vertices

        // Read indices from face
        for (int i = 1; i < parts.length; i++) {
            faceIndices[i - 1] = getVertexIndex(parts[i], vertices.size());
        }

        // Convert polygon into triangles (fan method)
        for (int i = 1; i < faceIndices.length - 1; i++) {
            Vector3D v0 = vertices.get(faceIndices[0]);
            Vector3D v1 = vertices.get(faceIndices[i]);
            Vector3D v2 = vertices.get(faceIndices[i + 1]);

            scene.addObject(new Triangle(v0, v1, v2, color)); // Add triangle to scene
        }
    }

    // Get index of vertex from OBJ format
    private static int getVertexIndex(String token, int vertexCount) {
        String[] values = token.split("/"); // Format can be v/vt/vn
        int index = Integer.parseInt(values[0]);

        // If index is negative (OBJ supports this)
        if (index < 0) return vertexCount + index;

        return index - 1; // OBJ starts at 1, Java starts at 0
    }

    // Get name safely
    private static String getName(String[] parts) {
        if (parts.length < 2) return "default";
        return parts[1];
    }
}
