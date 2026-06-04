import java.awt.Color;

// This class is part of the ray tracer.
public class Triangle extends Object3D {

// Triangle vertices and normals.
    private Vector3D v0, v1, v2;
    private Vector3D n0, n1, n2;
    private boolean hasVertexNormals;
    private boolean isPrism;
    private Vector3D[][] prismFaces;
    private Vector3D[]   prismNormals;

// Constructor for a colored triangle.
    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Color color) {
        super(color);
        init(v0, v1, v2, null, null, null, false);
    }

// Constructor for a material triangle.
    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Material material) {
        super(material);
        init(v0, v1, v2, null, null, null, false);
    }

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2,
                    Vector3D n0, Vector3D n1, Vector3D n2, Color color) {
        super(color);
        init(v0, v1, v2, n0, n1, n2, true);
    }

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2,
                    Vector3D n0, Vector3D n1, Vector3D n2, Material material) {
        super(material);
        init(v0, v1, v2, n0, n1, n2, true);
    }

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Color color, double depth) {
        super(color);
        initPrism(v0, v1, v2, depth);
    }

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Material material, double depth) {
        super(material);
        initPrism(v0, v1, v2, depth);
    }

// Store the triangle data.
    private void init(Vector3D v0, Vector3D v1, Vector3D v2,
                      Vector3D n0, Vector3D n1, Vector3D n2, boolean smooth) {
        this.v0 = v0; this.v1 = v1; this.v2 = v2;
        this.hasVertexNormals = smooth;
        this.isPrism = false;

        if (smooth) {
            this.n0 = n0.normalize();
            this.n1 = n1.normalize();
            this.n2 = n2.normalize();
        } else {
            Vector3D flat = computeNormal(v0, v1, v2);
            this.n0 = this.n1 = this.n2 = flat;
        }
    }

// Build a simple prism shape.
    private void initPrism(Vector3D v0, Vector3D v1, Vector3D v2, double depth) {
        this.v0 = v0; this.v1 = v1; this.v2 = v2;
        this.hasVertexNormals = false;
        this.isPrism = true;

        Vector3D offset = new Vector3D(0, 0, -depth);
        Vector3D b0 = v0.add(offset);
        Vector3D b1 = v1.add(offset);
        Vector3D b2 = v2.add(offset);

        prismFaces = new Vector3D[][] {
            { v0, v1, v2 },
            { b2, b1, b0 },
            { v0, b0, b1 }, { v0, b1, v1 },
            { v1, b1, b2 }, { v1, b2, v2 },
            { v2, b2, b0 }, { v2, b0, v0 }
        };

        prismNormals = new Vector3D[prismFaces.length];
        for (int i = 0; i < prismFaces.length; i++)
            prismNormals[i] = computeNormal(prismFaces[i][0], prismFaces[i][1], prismFaces[i][2]);

        this.n0 = this.n1 = this.n2 = prismNormals[0];
    }

    @Override
// Check if a ray hits the triangle.
    public Intersection getIntersection(Ray ray) {
        return isPrism ? getPrismIntersection(ray)
                       : singleTriangle(ray, v0, v1, v2, n0, n1, n2, hasVertexNormals);
    }

// Check all prism faces.
    private Intersection getPrismIntersection(Ray ray) {
        Intersection closest = null;
        for (int i = 0; i < prismFaces.length; i++) {
            Intersection hit = singleTriangle(ray,
                prismFaces[i][0], prismFaces[i][1], prismFaces[i][2],
                prismNormals[i],  prismNormals[i],  prismNormals[i], false);
            if (hit != null && (closest == null || hit.getDistance() < closest.getDistance()))
                closest = hit;
        }
        return closest;
    }

// Single triangle intersection test.
    private Intersection singleTriangle(Ray ray,
        Vector3D aV, Vector3D bV, Vector3D cV,
        Vector3D aN, Vector3D bN, Vector3D cN, boolean smooth) {

        final double EPS = 1e-7;
        Vector3D edge1 = bV.subtract(aV);
        Vector3D edge2 = cV.subtract(aV);
        Vector3D h = ray.getDirection().crossProduct(edge2);
        double   a = edge1.dotProduct(h);
        if (a > -EPS && a < EPS) return null;

        double   f = 1.0 / a;
        Vector3D s = ray.getOrigin().subtract(aV);
        double   u = f * s.dotProduct(h);
        if (u < 0 || u > 1) return null;

        Vector3D q = s.crossProduct(edge1);
        double   v = f * ray.getDirection().dotProduct(q);
        if (v < 0 || u + v > 1) return null;

        double t = f * edge2.dotProduct(q);
        if (t <= EPS) return null;

        Vector3D point  = ray.getPoint(t);
        Vector3D normal = smooth
            ? interpolateNormal(u, v, aN, bN, cN)
            : computeNormal(aV, bV, cV);

        return new Intersection(t, point, this, normal);
    }

    @Override
// Return the normal for shading.
    public Vector3D getNormal(Vector3D point) {
        if (isPrism)           return n0;
        if (hasVertexNormals)  return interpolateNormalFromPoint(point);
        return computeNormal(v0, v1, v2);
    }

// Blend normals for smooth shading.
    private Vector3D interpolateNormal(double u, double v,
                                       Vector3D aN, Vector3D bN, Vector3D cN) {
        double w = 1.0 - u - v;
        return aN.multiply(w).add(bN.multiply(u)).add(cN.multiply(v)).normalize();
    }

// Estimate the normal from a hit point.
    private Vector3D interpolateNormalFromPoint(Vector3D point) {
        Vector3D e1 = v1.subtract(v0), e2 = v2.subtract(v0), vp = point.subtract(v0);
        double d00 = e1.dotProduct(e1), d01 = e1.dotProduct(e2), d11 = e2.dotProduct(e2);
        double d20 = vp.dotProduct(e1), d21 = vp.dotProduct(e2);
        double denom = d00 * d11 - d01 * d01;
        if (Math.abs(denom) < 1e-10) return computeNormal(v0, v1, v2);
        double bv = (d11 * d20 - d01 * d21) / denom;
        double bw = (d00 * d21 - d01 * d20) / denom;
        return interpolateNormal(bv, bw, n0, n1, n2);
    }

// Calculate the face normal.
    private Vector3D computeNormal(Vector3D a, Vector3D b, Vector3D c) {
        return b.subtract(a).crossProduct(c.subtract(a)).normalize();
    }

// Return vertices for the BVH.
    public Vector3D[] getVertices() {
        if (isPrism) {
            java.util.Set<Vector3D> seen = new java.util.LinkedHashSet<>();
            for (Vector3D[] face : prismFaces) {
                for (Vector3D v : face) seen.add(v);
            }
            return seen.toArray(new Vector3D[0]);
        }
        return new Vector3D[]{ v0, v1, v2 };
    }
}
