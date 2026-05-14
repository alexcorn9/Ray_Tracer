import java.awt.Color;

public class Triangle extends Object3D {

    // Original triangle vertices
    private Vector3D v0, v1, v2;

    // Vertex normals for phong shading
    private Vector3D n0, n1, n2;

    // True when using vertex normals
    private boolean hasVertexNormals;

    // True when triangle is a 3D prism
    private boolean isPrism;

    // Prism face vertices
    private Vector3D[][] prismFaces;

    // Prism face normals
    private Vector3D[] prismNormals;

    // Flat triangle constructor
    public Triangle(
        Vector3D v0,
        Vector3D v1,
        Vector3D v2,
        Color color
    ) {

        // Use first vertex as object position
        super(v0, color);

        // Save vertices
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        // Compute flat normal
        Vector3D flatNormal =
            getFlatNormal();

        // Same normal for all vertices
        this.n0 = flatNormal;
        this.n1 = flatNormal;
        this.n2 = flatNormal;

        // No smooth normals
        this.hasVertexNormals = false;

        // Not a 3D prism
        this.isPrism = false;
    }

    // Phong triangle constructor
    public Triangle(
        Vector3D v0,
        Vector3D v1,
        Vector3D v2,
        Vector3D n0,
        Vector3D n1,
        Vector3D n2,
        Color color
    ) {

        // Use first vertex as object position
        super(v0, color);

        // Save vertices
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        // Save normalized vertex normals
        this.n0 = n0.normalize();
        this.n1 = n1.normalize();
        this.n2 = n2.normalize();

        // Use smooth shading
        this.hasVertexNormals = true;

        // Not a 3D prism
        this.isPrism = false;
    }

    // 3D triangular prism constructor
    public Triangle(
        Vector3D v0,
        Vector3D v1,
        Vector3D v2,
        Color color,
        double depth
    ) {

        // Use first vertex as object position
        super(v0, color);

        // Save front vertices
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        // No phong normals for prism
        this.hasVertexNormals = false;

        // This object is a 3D prism
        this.isPrism = true;

        // Move back face in Z direction
        Vector3D offset =
            new Vector3D(
                0,
                0,
                -depth
            );

        // Back triangle vertices
        Vector3D b0 =
            v0.add(offset);

        Vector3D b1 =
            v1.add(offset);

        Vector3D b2 =
            v2.add(offset);

        // Prism is made of 8 triangles
        prismFaces =
            new Vector3D[][] {

                // Front face
                { v0, v1, v2 },

                // Back face
                { b2, b1, b0 },

                // Bottom rectangular side
                { v0, b0, b1 },
                { v0, b1, v1 },

                // Right rectangular side
                { v1, b1, b2 },
                { v1, b2, v2 },

                // Left rectangular side
                { v2, b2, b0 },
                { v2, b0, v0 }
            };

        // Store normals for every face
        prismNormals =
            new Vector3D[prismFaces.length];

        // Compute prism face normals
        for (int i = 0; i < prismFaces.length; i++) {

            prismNormals[i] =
                computeNormal(
                    prismFaces[i][0],
                    prismFaces[i][1],
                    prismFaces[i][2]
                );
        }

        // Use first normal as default
        this.n0 = prismNormals[0];
        this.n1 = prismNormals[0];
        this.n2 = prismNormals[0];
    }

    @Override
    public Intersection getIntersection(Ray ray) {

        // If object is a 3D prism
        if (isPrism) {

            return getPrismIntersection(ray);
        }

        // Otherwise use normal triangle intersection
        return getSingleTriangleIntersection(
            ray,
            v0,
            v1,
            v2,
            n0,
            n1,
            n2,
            hasVertexNormals
        );
    }

    // Find closest prism face intersection
    private Intersection getPrismIntersection(
        Ray ray
    ) {

        // Store closest face hit
        Intersection closest = null;

        // Check all prism faces
        for (int i = 0; i < prismFaces.length; i++) {

            // Current face vertices
            Vector3D a =
                prismFaces[i][0];

            Vector3D b =
                prismFaces[i][1];

            Vector3D c =
                prismFaces[i][2];

            // Current face normal
            Vector3D faceNormal =
                prismNormals[i];

            // Intersect current face
            Intersection hit =
                getSingleTriangleIntersection(
                    ray,
                    a,
                    b,
                    c,
                    faceNormal,
                    faceNormal,
                    faceNormal,
                    false
                );

            // Valid hit
            if (hit != null) {

                // Keep nearest face
                if (
                    closest == null
                    ||
                    hit.getDistance()
                    <
                    closest.getDistance()
                ) {

                    closest = hit;
                }
            }
        }

        // Return closest prism hit
        return closest;
    }

    // Möller-Trumbore triangle intersection
    private Intersection getSingleTriangleIntersection(
        Ray ray,
        Vector3D aVertex,
        Vector3D bVertex,
        Vector3D cVertex,
        Vector3D aNormal,
        Vector3D bNormal,
        Vector3D cNormal,
        boolean smooth
    ) {

        // Small precision value
        double eps = 1e-6;

        // First triangle edge
        Vector3D edge1 =
            bVertex.subtract(aVertex);

        // Second triangle edge
        Vector3D edge2 =
            cVertex.subtract(aVertex);

        // Begin Möller-Trumbore algorithm
        Vector3D h =
            ray.getDirection()
            .crossProduct(edge2);

        // Determinant
        double a =
            edge1.dotProduct(h);

        // Ray is parallel to triangle
        if (a > -eps && a < eps) {
            return null;
        }

        // Inverse determinant
        double f =
            1.0 / a;

        // Vector from vertex to ray origin
        Vector3D s =
            ray.getOrigin()
            .subtract(aVertex);

        // First barycentric coordinate
        double u =
            f * s.dotProduct(h);

        // Outside triangle
        if (u < 0 || u > 1) {
            return null;
        }

        // Cross product for second coordinate
        Vector3D q =
            s.crossProduct(edge1);

        // Second barycentric coordinate
        double v =
            f
            *
            ray.getDirection()
            .dotProduct(q);

        // Outside triangle
        if (v < 0 || u + v > 1) {
            return null;
        }

        // Distance along ray
        double t =
            f * edge2.dotProduct(q);

        // Valid hit
        if (t > eps) {

            // Hit point
            Vector3D point =
                ray.getPoint(t);

            // Final normal
            Vector3D normal;

            // Use smooth interpolated normal
            if (smooth) {

                normal =
                    interpolateNormal(
                        u,
                        v,
                        aNormal,
                        bNormal,
                        cNormal
                    );

            } else {

                // Use flat face normal
                normal =
                    computeNormal(
                        aVertex,
                        bVertex,
                        cVertex
                    );
            }

            // Return intersection
            return new Intersection(
                t,
                point,
                this,
                normal
            );
        }

        // No valid hit
        return null;
    }

    @Override
    public Vector3D getNormal(
        Vector3D point
    ) {

        // Prism normals come from intersection
        if (isPrism) {
            return n0;
        }

        // Use interpolated normal if available
        if (hasVertexNormals) {
            return interpolateNormal(point);
        }

        // Otherwise use flat normal
        return getFlatNormal();
    }

    // Interpolate normal from barycentric coordinates
    private Vector3D interpolateNormal(
        double u,
        double v
    ) {

        return interpolateNormal(
            u,
            v,
            n0,
            n1,
            n2
        );
    }

    // Interpolate custom normals
    private Vector3D interpolateNormal(
        double u,
        double v,
        Vector3D aNormal,
        Vector3D bNormal,
        Vector3D cNormal
    ) {

        // Third barycentric coordinate
        double w =
            1.0 - u - v;

        // Weighted average normal
        Vector3D normal =
            aNormal.multiply(w)
            .add(bNormal.multiply(u))
            .add(cNormal.multiply(v))
            .normalize();

        // Flip normal toward camera
        if (normal.getZ() < 0) {
            normal =
                normal.multiply(-1);
        }

        return normal;
    }

    // Compute barycentric normal from point
    private Vector3D interpolateNormal(
        Vector3D point
    ) {

        // First edge
        Vector3D edge1 =
            v1.subtract(v0);

        // Second edge
        Vector3D edge2 =
            v2.subtract(v0);

        // Point vector
        Vector3D vp =
            point.subtract(v0);

        // Dot products
        double d00 =
            edge1.dotProduct(edge1);

        double d01 =
            edge1.dotProduct(edge2);

        double d11 =
            edge2.dotProduct(edge2);

        double d20 =
            vp.dotProduct(edge1);

        double d21 =
            vp.dotProduct(edge2);

        // Denominator
        double denom =
            d00 * d11 - d01 * d01;

        // Avoid division by zero
        if (denom == 0) {
            return getFlatNormal();
        }

        // Barycentric v
        double v =
            (
                d11 * d20
                -
                d01 * d21
            )
            /
            denom;

        // Barycentric w
        double w =
            (
                d00 * d21
                -
                d01 * d20
            )
            /
            denom;

        // Return interpolated normal
        return interpolateNormal(
            v,
            w
        );
    }

    // Compute flat normal
    private Vector3D getFlatNormal() {

        return computeNormal(
            v0,
            v1,
            v2
        );
    }

    // Compute normal from three points
    private Vector3D computeNormal(
        Vector3D a,
        Vector3D b,
        Vector3D c
    ) {

        // First edge
        Vector3D edge1 =
            b.subtract(a);

        // Second edge
        Vector3D edge2 =
            c.subtract(a);

        // Cross product normal
        Vector3D normal =
            edge1.crossProduct(edge2)
            .normalize();

        // Flip normal toward camera
        if (normal.getZ() < 0) {
            normal =
                normal.multiply(-1);
        }

        return normal;
    }
}