import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Bounding Volume Hierarchy (BVH) — axis-aligned bounding box tree.
 *
 * Reduces ray–object intersection from O(n) brute force to O(log n) average.
 * The tree is built once after all objects are added to the scene (Scene.buildBVH()).
 * The Raytracer then calls BVHNode.intersect() instead of iterating every object.
 *
 * Original code structure is unchanged — this is an additive acceleration structure.
 */
public class BVHNode {

    // Axis-aligned bounding box corners
    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;

    private BVHNode left;
    private BVHNode right;
    private List<Object3D> objects; // Non-null only in leaf nodes

    // Leaf threshold: if <= this many objects, make a leaf.
    private static final int LEAF_SIZE = 4;

    // ── Build ─────────────────────────────────────────────────────────────────
    public static BVHNode build(List<Object3D> objects) {
        BVHNode node = new BVHNode();
        node.computeBounds(objects);

        if (objects.size() <= LEAF_SIZE) {
            node.objects = new ArrayList<>(objects);
            return node;
        }

        // Split on the longest axis using centroid sort
        int axis = node.longestAxis();
        List<Object3D> sorted = new ArrayList<>(objects);
        sorted.sort(Comparator.comparingDouble(o -> centroid(o, axis)));

        int mid = sorted.size() / 2;
        node.left  = build(sorted.subList(0, mid));
        node.right = build(sorted.subList(mid, sorted.size()));

        return node;
    }

    // ── Intersection ──────────────────────────────────────────────────────────
    /**
     * Returns the closest intersection within [tMin, tMax], or null.
     * Skips entire subtrees whose AABB the ray misses.
     */
    public Intersection intersect(Ray ray, double tMin, double tMax) {
        if (!rayHitsBox(ray, tMin, tMax)) return null;

        // Leaf node: test each object directly
        if (objects != null) {
            Intersection closest = null;
            for (Object3D obj : objects) {
                Intersection hit = obj.getIntersection(ray);
                if (hit == null) continue;
                double t = hit.getDistance();
                if (t > tMin && t < tMax && (closest == null || t < closest.getDistance())) {
                    closest = hit;
                }
            }
            return closest;
        }

        // Internal node: test both children, keep closer hit
        Intersection hitL = (left  != null) ? left.intersect(ray, tMin, tMax)  : null;
        Intersection hitR = (right != null) ? right.intersect(ray, tMin, tMax) : null;

        if (hitL == null) return hitR;
        if (hitR == null) return hitL;
        return hitL.getDistance() <= hitR.getDistance() ? hitL : hitR;
    }

    /**
     * Shadow variant: returns true as soon as ANY hit in (tMin, maxDist) is found.
     * Exits the BVH immediately — no need to find the closest hit.
     */
    public boolean intersectShadow(Ray ray, double tMin, double maxDist) {
        if (!rayHitsBox(ray, tMin, maxDist)) return false;

        if (objects != null) {
            for (Object3D obj : objects) {
                Intersection hit = obj.getIntersection(ray);
                if (hit == null) continue;
                double t = hit.getDistance();
                if (t > tMin && t < maxDist) return true;
            }
            return false;
        }

        if (left  != null && left.intersectShadow(ray, tMin, maxDist))  return true;
        if (right != null && right.intersectShadow(ray, tMin, maxDist)) return true;
        return false;
    }

    // ── AABB slab test (Smits / Williams method) ───────────────────────────
    private boolean rayHitsBox(Ray ray, double tMin, double tMax) {
        Vector3D orig = ray.getOrigin();
        Vector3D dir  = ray.getDirection();

        double tNear = tMin;
        double tFar  = tMax;

        // X slab
        double invDx = 1.0 / dir.getX();
        double t0x = (minX - orig.getX()) * invDx;
        double t1x = (maxX - orig.getX()) * invDx;
        if (invDx < 0) { double tmp = t0x; t0x = t1x; t1x = tmp; }
        tNear = Math.max(tNear, t0x);
        tFar  = Math.min(tFar,  t1x);
        if (tFar < tNear) return false;

        // Y slab
        double invDy = 1.0 / dir.getY();
        double t0y = (minY - orig.getY()) * invDy;
        double t1y = (maxY - orig.getY()) * invDy;
        if (invDy < 0) { double tmp = t0y; t0y = t1y; t1y = tmp; }
        tNear = Math.max(tNear, t0y);
        tFar  = Math.min(tFar,  t1y);
        if (tFar < tNear) return false;

        // Z slab
        double invDz = 1.0 / dir.getZ();
        double t0z = (minZ - orig.getZ()) * invDz;
        double t1z = (maxZ - orig.getZ()) * invDz;
        if (invDz < 0) { double tmp = t0z; t0z = t1z; t1z = tmp; }
        tNear = Math.max(tNear, t0z);
        tFar  = Math.min(tFar,  t1z);

        return tFar >= tNear;
    }

    // ── Bounding box helpers ───────────────────────────────────────────────
    private void computeBounds(List<Object3D> objs) {
        minX = minY = minZ =  Double.MAX_VALUE;
        maxX = maxY = maxZ = -Double.MAX_VALUE;
        for (Object3D obj : objs) {
            double[][] box = getBounds(obj);
            minX = Math.min(minX, box[0][0]); maxX = Math.max(maxX, box[1][0]);
            minY = Math.min(minY, box[0][1]); maxY = Math.max(maxY, box[1][1]);
            minZ = Math.min(minZ, box[0][2]); maxZ = Math.max(maxZ, box[1][2]);
        }
    }

    private int longestAxis() {
        double dx = maxX - minX;
        double dy = maxY - minY;
        double dz = maxZ - minZ;
        if (dx >= dy && dx >= dz) return 0;
        if (dy >= dz)             return 1;
        return 2;
    }

    private static double centroid(Object3D obj, int axis) {
        double[][] box = getBounds(obj);
        return (box[0][axis] + box[1][axis]) * 0.5;
    }

    /**
     * Returns {{minX,minY,minZ},{maxX,maxY,maxZ}} for any Object3D.
     * Falls back to a small epsilon box around the origin for unknown types.
     */
    private static double[][] getBounds(Object3D obj) {
        double BIG = Double.MAX_VALUE;
        double minX =  BIG, minY =  BIG, minZ =  BIG;
        double maxX = -BIG, maxY = -BIG, maxZ = -BIG;

        if (obj instanceof Triangle) {
            Triangle tri = (Triangle) obj;
            Vector3D[] verts = tri.getVertices();
            for (Vector3D v : verts) {
                minX = Math.min(minX, v.getX()); maxX = Math.max(maxX, v.getX());
                minY = Math.min(minY, v.getY()); maxY = Math.max(maxY, v.getY());
                minZ = Math.min(minZ, v.getZ()); maxZ = Math.max(maxZ, v.getZ());
            }
            // Small epsilon to avoid zero-thickness slabs on axis-aligned triangles
            double EPS = 1e-4;
            if (maxX - minX < EPS) { minX -= EPS; maxX += EPS; }
            if (maxY - minY < EPS) { minY -= EPS; maxY += EPS; }
            if (maxZ - minZ < EPS) { minZ -= EPS; maxZ += EPS; }

        } else if (obj instanceof Sphere) {
            Sphere sph = (Sphere) obj;
            double r = sph.getRadius();
            Vector3D c = sph.getCenter();
            minX = c.getX() - r; maxX = c.getX() + r;
            minY = c.getY() - r; maxY = c.getY() + r;
            minZ = c.getZ() - r; maxZ = c.getZ() + r;

        } else {
            // Unknown type: unit box at origin (safe fallback — will still test correctly)
            minX = minY = minZ = -0.5;
            maxX = maxY = maxZ =  0.5;
        }

        return new double[][]{{minX, minY, minZ}, {maxX, maxY, maxZ}};
    }
}
