import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ObjReader {

    // Simple load function
    public static void load(
        String filename,
        Scene scene,
        Color color
    ) {

        load(
            filename,
            scene,
            color,
            new Vector3D(0, 0, -3),
            1.0
        );
    }

    // Main OBJ loader
    public static void load(
        String filename,
        Scene scene,
        Color color,
        Vector3D offset,
        double scale
    ) {

        // Store model vertices
        List<Vector3D> vertices =
            new ArrayList<>();

        // Store vertex normals
        List<Vector3D> normals =
            new ArrayList<>();

        // Store texture coordinates
        List<double[]> textures =
            new ArrayList<>();

        String objectName = "default";
        String groupName = "default";

        // Current smoothing group
        String smoothingGroup = "off";

        try (
            BufferedReader reader =
                new BufferedReader(
                    new FileReader(filename)
                )
        ) {

            String line;

            // Read OBJ line by line
            while ((line = reader.readLine()) != null) {

                line = line.trim();

                // Skip empty lines
                if (
                    line.length() == 0 ||
                    line.startsWith("#")
                ) {
                    continue;
                }

                // Split line into tokens
                String[] parts =
                    line.split("\\s+");

                // Vertex
                if (parts[0].equals("v")) {

                    double x =
                        Double.parseDouble(parts[1])
                        * scale
                        + offset.getX();

                    double y =
                        Double.parseDouble(parts[2])
                        * scale
                        + offset.getY();

                    double z =
                        Double.parseDouble(parts[3])
                        * scale
                        + offset.getZ();

                    vertices.add(
                        new Vector3D(x, y, z)
                    );

                // Vertex normal
                } else if (parts[0].equals("vn")) {

                    double x =
                        Double.parseDouble(parts[1]);

                    double y =
                        Double.parseDouble(parts[2]);

                    double z =
                        Double.parseDouble(parts[3]);

                    normals.add(
                        new Vector3D(x, y, z)
                        .normalize()
                    );

                // Texture coordinate
                } else if (parts[0].equals("vt")) {

                    double u =
                        Double.parseDouble(parts[1]);

                    double v = 0;

                    if (parts.length > 2) {
                        v =
                            Double.parseDouble(
                                parts[2]
                            );
                    }

                    textures.add(
                        new double[] {u, v}
                    );

                // Object name
                } else if (parts[0].equals("o")) {

                    objectName = getName(parts);

                // Group name
                } else if (parts[0].equals("g")) {

                    groupName = getName(parts);

                // Smoothing group
                } else if (parts[0].equals("s")) {

                    smoothingGroup = parts[1];

                // Face
                } else if (parts[0].equals("f")) {

                    addFace(
                        parts,
                        vertices,
                        normals,
                        scene,
                        color,
                        smoothingGroup
                    );
                }
            }

            // Print model information
            System.out.println(
                "OBJ loaded: " + filename
            );

            System.out.println(
                "Object: " +
                objectName +
                ", Group: " +
                groupName +
                ", Smoothing: " +
                smoothingGroup
            );

            System.out.println(
                "v: " + vertices.size() +
                ", vt: " + textures.size() +
                ", vn: " + normals.size()
            );

        } catch (Exception e) {

            System.out.println(
                "Error loading OBJ file: " +
                filename
            );

            e.printStackTrace();
        }
    }

    // Convert OBJ face into triangles
    private static void addFace(
        String[] parts,
        List<Vector3D> vertices,
        List<Vector3D> normals,
        Scene scene,
        Color color,
        String smoothingGroup
    ) {

        // Vertex indices
        int[] vertexIndices =
            new int[parts.length - 1];

        // Normal indices
        int[] normalIndices =
            new int[parts.length - 1];

        boolean hasNormals = true;

        // Read all face indices
        for (int i = 1; i < parts.length; i++) {

            vertexIndices[i - 1] =
                getVertexIndex(
                    parts[i],
                    vertices.size()
                );

            normalIndices[i - 1] =
                getNormalIndex(
                    parts[i],
                    normals.size()
                );

            // Missing normal
            if (normalIndices[i - 1] < 0) {
                hasNormals = false;
            }
        }

        // Check smoothing mode
        boolean smooth =
            !smoothingGroup.equalsIgnoreCase("off")
            &&
            !smoothingGroup.equals("0");

        // Convert polygon into triangles
        for (
            int i = 1;
            i < vertexIndices.length - 1;
            i++
        ) {

            Vector3D v0 =
                vertices.get(vertexIndices[0]);

            Vector3D v1 =
                vertices.get(vertexIndices[i]);

            Vector3D v2 =
                vertices.get(vertexIndices[i + 1]);

            // Use phong shading
            if (smooth && hasNormals) {

                Vector3D n0 =
                    normals.get(normalIndices[0]);

                Vector3D n1 =
                    normals.get(normalIndices[i]);

                Vector3D n2 =
                    normals.get(normalIndices[i + 1]);

                scene.addObject(
                    new Triangle(
                        v0,
                        v1,
                        v2,
                        n0,
                        n1,
                        n2,
                        color
                    )
                );

            } else {

                // Use flat shading
                scene.addObject(
                    new Triangle(
                        v0,
                        v1,
                        v2,
                        color
                    )
                );
            }
        }
    }

    // Read vertex index from OBJ token
    private static int getVertexIndex(
        String token,
        int vertexCount
    ) {

        String[] values = token.split("/");

        int index =
            Integer.parseInt(values[0]);

        // Negative indices support
        if (index < 0) {
            return vertexCount + index;
        }

        // OBJ indices start at 1
        return index - 1;
    }

    // Read normal index from OBJ token
    private static int getNormalIndex(
        String token,
        int normalCount
    ) {

        String[] values = token.split("/");

        // No normal available
        if (
            values.length < 3 ||
            values[2].length() == 0
        ) {
            return -1;
        }

        int index =
            Integer.parseInt(values[2]);

        // Negative indices support
        if (index < 0) {
            return normalCount + index;
        }

        // OBJ indices start at 1
        return index - 1;
    }

    // Safe name extraction
    private static String getName(
        String[] parts
    ) {

        if (parts.length < 2) {
            return "default";
        }

        return parts[1];
    }
}