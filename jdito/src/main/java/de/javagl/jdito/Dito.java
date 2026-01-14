/**
 * JDito - DiTO oriented bounding box computation
 * 
 * Distributed under the 2-clause BSD license. See LICENSE for details.
 * 
 * Copyright 2025 Marco Hutter (Java port)
 * Copyright 2018 Stefan Eilemann (TypeScript port)
 * Copyright 2011 Thomas Larsson and Linus Kallberg (C++ implementation).
 */
package de.javagl.jdito;

/**
 * A port of the main functionality from dito.ts
 */
@SuppressWarnings("javadoc")
class Dito
{
    private static final double epsilon = 0.000001;
    private final WritableDoubleArray alMid = DoubleArrays.create(3);
    private final WritableDoubleArray alLen = DoubleArrays.create(3);;

    // Derived from the TypedScript implementation of
    // https://github.com/Esri/dito.ts
    // Derived from the C++ sample implementation of
    // http://www.idt.mdh.se/~tla/publ/FastOBBs.pdf
    void computeOBB(Attribute positions, Obb obb)
    {
        ReadableDoubleArray data = positions.data;
        int strideIdx = positions.strideIdx;
        int count = data.length() / strideIdx;
        if (count <= 0)
        {
            return;
        }

        // Select seven extremal points along predefined slab directions
        ExtremalPoints extremals = new ExtremalPoints(positions);

        // Compute size of AABB (max and min projections of vertices are already
        // computed as slabs 0-2)
        Vec.v3add(alMid, extremals.minProj, extremals.maxProj);
        Vec.v3scale(alMid, alMid, 0.5);

        Vec.v3subtract(alLen, extremals.maxProj, extremals.minProj);

        double alVal = _getQualityValue(alLen);
        Orientation best = new Orientation();
        best.quality = alVal;

        if (count < 14)
        {
            positions = new Attribute();
            positions.data = DoubleArrays.create(extremals.buffer, 14, 14 * 3);
            positions.size = 3;
            positions.offsetIdx = 0;
            positions.strideIdx = 3;
        }

        // Find best OBB axes based on the constructed base triangle
        // Vertices of the large base triangle
        WritableDoubleArray p0 = DoubleArrays.create(3);
        WritableDoubleArray p1 = DoubleArrays.create(3);
        WritableDoubleArray p2 = DoubleArrays.create(3);
        // Edge vectors of the large base triangle
        WritableDoubleArray e0 = DoubleArrays.create(3);
        WritableDoubleArray e1 = DoubleArrays.create(3);
        WritableDoubleArray e2 = DoubleArrays.create(3);
        // Unit normal of the large base triangle
        WritableDoubleArray n = DoubleArrays.create(3);

        switch (_findBestObbAxesFromBaseTriangle(extremals, positions, n, p0,
            p1, p2, e0, e1, e2, best, obb))
        {
            case 1:
                _finalizeAxisAlignedOBB(alMid, alLen, obb);
                return;
            case 2:
                _finalizeLineAlignedOBB(positions, e0, obb);
                return;
        }

        // Find improved OBB axes based on constructed di-tetrahedral shape
        // raised from base triangle
        _findImprovedObbAxesFromUpperAndLowerTetrasOfBaseTriangle(positions, n,
            p0, p1, p2, e0, e1, e2, best, obb);

        // compute the true obb dimensions by iterating over all vertices
        _computeObbDimensions(positions, best.b0, best.b1, best.b2, bMin, bMax);
        WritableDoubleArray bLen = DoubleArrays.create(3);
        Vec.v3subtract(bLen, bMax, bMin);
        best.quality = _getQualityValue(bLen);

        // Check if the OBB extent is still smaller than the intial AABB
        if (best.quality < alVal)
        {
            // if so, assign all OBB params
            _finalizeOBB(best.b0, best.b1, best.b2, bMin, bMax, bLen, obb);
        }
        else
        {
            // otherwise, assign all OBB params using the intial AABB
            _finalizeAxisAlignedOBB(alMid, alLen, obb);
        }
    }

    private int _findBestObbAxesFromBaseTriangle(ExtremalPoints extremals,
        Attribute positions, WritableDoubleArray n, WritableDoubleArray p0,
        WritableDoubleArray p1, WritableDoubleArray p2, WritableDoubleArray e0,
        WritableDoubleArray e1, WritableDoubleArray e2, Orientation best,
        Obb obb)
    {
        _findFurthestPointPair(extremals, p0, p1);

        // Degenerate case 1:
        // If the furthest points are very close, return OBB aligned with the
        // initial AABB
        if (Vec.v3squaredDistance(p0, p1) < epsilon)
        {
            return 1;
        }

        // Compute edge vector of the line segment p0, p1
        Vec.v3subtract(e0, p0, p1);
        Vec.v3normalize(e0, e0);

        // Find a third point furthest away from line given by p0, e0 to define
        // the large base triangle
        double dist2 =
            _findFurthestPointFromInfiniteEdge(positions, p0, e0, p2);

        // Degenerate case 2:
        // If the third point is located very close to the line, return an OBB
        // aligned with the line
        if (dist2 < epsilon)
        {
            return 2;
        }

        // Compute the two remaining edge vectors and the normal vector of the
        // base triangle
        Vec.v3subtract(e1, p1, p2);
        Vec.v3normalize(e1, e1);
        Vec.v3subtract(e2, p2, p0);
        Vec.v3normalize(e2, e2);
        Vec.v3cross(n, e1, e0);
        Vec.v3normalize(n, n);

        _findBestObbAxesFromTriangleNormalAndEdgeVectors(positions, n, e0, e1,
            e2, best);
        return 0; // success
    }

    private final WritableDoubleArray q0 = DoubleArrays.create(3);
    private final WritableDoubleArray q1 = DoubleArrays.create(3);

    // Edge vectors towards q0/1
    private final WritableDoubleArray f0 = DoubleArrays.create(3);
    private final WritableDoubleArray f1 = DoubleArrays.create(3);
    private final WritableDoubleArray f2 = DoubleArrays.create(3);

    // Unit normals of tetra tris
    private final WritableDoubleArray n0 = DoubleArrays.create(3);
    private final WritableDoubleArray n1 = DoubleArrays.create(3);
    private final WritableDoubleArray n2 = DoubleArrays.create(3);

    private void _findImprovedObbAxesFromUpperAndLowerTetrasOfBaseTriangle(
        Attribute positions, ReadableDoubleArray n, ReadableDoubleArray p0,
        ReadableDoubleArray p1, ReadableDoubleArray p2, ReadableDoubleArray e0,
        ReadableDoubleArray e1, ReadableDoubleArray e2, Orientation best,
        Obb obb)
    {
        // Find furthest points above and below the plane of the base triangle
        // for tetra constructions
        _findUpperLowerTetraPoints(positions, n, p0, p1, p2, q0, q1);

        // For each valid point found, search for the best OBB axes based on the
        // 3 arising triangles
        if (!Double.isNaN(q0.get(0)))
        {
            Vec.v3subtract(f0, q0, p0);
            Vec.v3normalize(f0, f0);
            Vec.v3subtract(f1, q0, p1);
            Vec.v3normalize(f1, f1);
            Vec.v3subtract(f2, q0, p2);
            Vec.v3normalize(f2, f2);

            Vec.v3cross(n0, f1, e0);
            Vec.v3normalize(n0, n0);
            Vec.v3cross(n1, f2, e1);
            Vec.v3normalize(n1, n1);
            Vec.v3cross(n2, f0, e2);
            Vec.v3normalize(n2, n2);

            _findBestObbAxesFromTriangleNormalAndEdgeVectors(positions, n0, e0,
                f1, f0, best);
            _findBestObbAxesFromTriangleNormalAndEdgeVectors(positions, n1, e1,
                f2, f1, best);
            _findBestObbAxesFromTriangleNormalAndEdgeVectors(positions, n2, e2,
                f0, f2, best);
        }
        if (!Double.isNaN(q1.get(0)))
        {
            Vec.v3subtract(f0, q1, p0);
            Vec.v3normalize(f0, f0);
            Vec.v3subtract(f1, q1, p1);
            Vec.v3normalize(f1, f1);
            Vec.v3subtract(f2, q1, p2);
            Vec.v3normalize(f2, f2);

            Vec.v3cross(n0, f1, e0);
            Vec.v3normalize(n0, n0);
            Vec.v3cross(n1, f2, e1);
            Vec.v3normalize(n1, n1);
            Vec.v3cross(n2, f0, e2);
            Vec.v3normalize(n2, n2);

            _findBestObbAxesFromTriangleNormalAndEdgeVectors(positions, n0, e0,
                f1, f0, best);
            _findBestObbAxesFromTriangleNormalAndEdgeVectors(positions, n1, e1,
                f2, f1, best);
            _findBestObbAxesFromTriangleNormalAndEdgeVectors(positions, n2, e2,
                f0, f2, best);
        }
    }

    private static void _findFurthestPointPair(ExtremalPoints extremals,
        WritableDoubleArray p0, WritableDoubleArray p1)
    {
        double maxDist2 =
            Vec.v3squaredDistance(extremals.maxVert[0], extremals.minVert[0]);
        int index = 0;

        for (int i = 1; i < 7; ++i)
        {
            double dist2 = Vec.v3squaredDistance(extremals.maxVert[i],
                extremals.minVert[i]);
            if (dist2 > maxDist2)
            {
                maxDist2 = dist2;
                index = i;
            }
        }
        Vec.v3copy(p0, extremals.minVert[index]);
        Vec.v3copy(p1, extremals.maxVert[index]);
    }

    private final WritableDoubleArray u0 = DoubleArrays.create(3);

    private double _findFurthestPointFromInfiniteEdge(Attribute positions,
        ReadableDoubleArray p0, ReadableDoubleArray e0, WritableDoubleArray p)
    {
        ReadableDoubleArray data = positions.data;
        int offsetIdx = positions.offsetIdx;
        int strideIdx = positions.strideIdx;

        double maxDist2 = Double.NEGATIVE_INFINITY;
        int maxIndex = 0;

        for (int i = offsetIdx; i < data.length(); i += strideIdx)
        {
            // inlined _dist2PointInfiniteEdge
            u0.set(0, data.get(i) - p0.get(0));
            u0.set(1, data.get(i + 1) - p0.get(1));
            u0.set(2, data.get(i + 2) - p0.get(2));
            double t = e0.get(0) * u0.get(0) + e0.get(1) * u0.get(1)
                + e0.get(2) * u0.get(2);
            double sqLen_e0 = e0.get(0) * e0.get(0) + e0.get(1) * e0.get(1)
                + e0.get(2) * e0.get(2);
            double sqLen_u0 = u0.get(0) * u0.get(0) + u0.get(1) * u0.get(1)
                + u0.get(2) * u0.get(2);
            double dist2 = sqLen_u0 - (t * t) / sqLen_e0;

            if (dist2 > maxDist2)
            {
                maxDist2 = dist2;
                maxIndex = i;
            }
        }

        Vec.v3copy(p, data, maxIndex);
        return maxDist2;
    }

    private final WritableDoubleArray minmax = DoubleArrays.create(2);

    private void _findUpperLowerTetraPoints(Attribute positions,
        ReadableDoubleArray n, ReadableDoubleArray p0, ReadableDoubleArray p1,
        ReadableDoubleArray p2, WritableDoubleArray q0, WritableDoubleArray q1)
    {
        _findExtremalPoints_OneDir(positions, n, minmax, q1, q0);
        double triProj = Vec.v3dot(p0, n);

        if (minmax.get(1) - epsilon <= triProj)
        {
            q0.set(0, Double.NaN); // invalidate
        }
        if (minmax.get(0) + epsilon >= triProj)
        {
            q1.set(0, Double.NaN); // invalidate
        }
    }

    private final WritableDoubleArray m0 = DoubleArrays.create(3);
    private final WritableDoubleArray m1 = DoubleArrays.create(3);
    private final WritableDoubleArray m2 = DoubleArrays.create(3);
    private final WritableDoubleArray dmax = DoubleArrays.create(3);
    private final WritableDoubleArray dmin = DoubleArrays.create(3);
    private final WritableDoubleArray dlen = DoubleArrays.create(3);

    private void _findBestObbAxesFromTriangleNormalAndEdgeVectors(
        Attribute positions, ReadableDoubleArray n, ReadableDoubleArray e0,
        ReadableDoubleArray e1, ReadableDoubleArray e2, Orientation best)
    {
        if (Vec.v3squaredLength(n) < epsilon)
        {
            return;
        }

        Vec.v3cross(m0, e0, n);
        Vec.v3cross(m1, e1, n);
        Vec.v3cross(m2, e2, n);

        // The operands are assumed to be orthogonal and unit normals
        _findExtremalProjs_OneDir(positions, n, minmax);
        dmin.set(1, minmax.get(0));
        dmax.set(1, minmax.get(1));
        dlen.set(1, dmax.get(1) - dmin.get(1));

        ReadableDoubleArray[] edges = new ReadableDoubleArray[]
        { e0, e1, e2 };
        ReadableDoubleArray[] ems = new ReadableDoubleArray[]
        { m0, m1, m2 };

        for (int i = 0; i < 3; ++i)
        {
            _findExtremalProjs_OneDir(positions, edges[i], minmax);
            dmin.set(0, minmax.get(0));
            dmax.set(0, minmax.get(1));

            _findExtremalProjs_OneDir(positions, ems[i], minmax);
            dmin.set(2, minmax.get(0));
            dmax.set(2, minmax.get(1));

            dlen.set(0, dmax.get(0) - dmin.get(0));
            dlen.set(2, dmax.get(2) - dmin.get(2));
            double quality = _getQualityValue(dlen);

            if (quality < best.quality)
            {
                Vec.v3copy(best.b0, edges[i]);
                Vec.v3copy(best.b1, n);
                Vec.v3copy(best.b2, ems[i]);
                best.quality = quality;
            }
        }
    }

    private final ReadableDoubleArray point = DoubleArrays.create(3);

    private static void _findExtremalProjs_OneDir(Attribute positions,
        ReadableDoubleArray n, WritableDoubleArray minmax)
    {
        ReadableDoubleArray data = positions.data;
        int offsetIdx = positions.offsetIdx;
        int strideIdx = positions.strideIdx;

        minmax.set(0, Double.POSITIVE_INFINITY);
        minmax.set(1, Double.NEGATIVE_INFINITY);

        for (int i = offsetIdx; i < data.length(); i += strideIdx)
        {
            // opt: inline dot product
            double proj = data.get(i) * n.get(0) + data.get(i + 1) * n.get(1)
                + data.get(i + 2) * n.get(2);
            minmax.set(0, Math.min(minmax.get(0), proj));
            minmax.set(1, Math.max(minmax.get(1), proj));
        }
    }

    private void _findExtremalPoints_OneDir(Attribute positions,
        ReadableDoubleArray n, WritableDoubleArray minmax,
        WritableDoubleArray minVert, WritableDoubleArray maxVert)
    {
        ReadableDoubleArray data = positions.data;
        int offsetIdx = positions.offsetIdx;
        int strideIdx = positions.strideIdx;

        Vec.v3copy(minVert, data, offsetIdx);
        Vec.v3copy(maxVert, minVert);

        minmax.set(0, Vec.v3dot(point, n));
        minmax.set(1, minmax.get(0));

        for (int i = offsetIdx + strideIdx; i < data.length(); i += strideIdx)
        {
            double proj = data.get(i) * n.get(0) + data.get(i + 1) * n.get(1)
                + data.get(i + 2) * n.get(2);

            if (proj < minmax.get(0))
            {
                minmax.set(0, proj);
                Vec.v3copy(minVert, data, i);
            }
            if (proj > minmax.get(1))
            {
                minmax.set(1, proj);
                Vec.v3copy(maxVert, data, i);
            }
        }
    }

    private static void _finalizeAxisAlignedOBB(ReadableDoubleArray mid,
        ReadableDoubleArray len, Obb obb)
    {
        Vec.v3copy(obb.center, mid);
        Vec.v3scale(obb.halfSize, len, 0.5);
        obb.quaternion.set(0, 0.0);
        obb.quaternion.set(1, 0.0);
        obb.quaternion.set(2, 0.0);
        obb.quaternion.set(3, 1.0);
    }

    private final WritableDoubleArray r = DoubleArrays.create(3);
    private final WritableDoubleArray v = DoubleArrays.create(3);
    private final WritableDoubleArray w = DoubleArrays.create(3);
    private final WritableDoubleArray bMin = DoubleArrays.create(3);
    private final WritableDoubleArray bMax = DoubleArrays.create(3);
    private final WritableDoubleArray bLen = DoubleArrays.create(3);

    // This function is only called if the construction of the large base
    // triangle fails
    private void _finalizeLineAlignedOBB(Attribute positions,
        ReadableDoubleArray u, Obb obb)
    {
        // Given u, build any orthonormal base u, v, w
        // Make sure r is not equal to u
        Vec.v3copy(r, u);
        if (Math.abs(u.get(0)) > Math.abs(u.get(1))
            && Math.abs(u.get(0)) > Math.abs(u.get(2)))
        {
            r.set(0, 0.0);
        }
        else if (Math.abs(u.get(1)) > Math.abs(u.get(2)))
        {
            r.set(1, 0.0);
        }
        else
        {
            r.set(2, 0.0);
        }

        if (Vec.v3squaredLength(r) < epsilon)
        {
            r.set(0, 1.0);
            r.set(1, 1.0);
            r.set(2, 1.0);
        }

        Vec.v3cross(v, u, r);
        Vec.v3normalize(v, v);
        Vec.v3cross(w, u, v);
        Vec.v3normalize(w, w);

        // compute the true obb dimensions by iterating over all vertices
        _computeObbDimensions(positions, u, v, w, bMin, bMax);
        Vec.v3subtract(bLen, bMax, bMin);
        _finalizeOBB(u, v, w, bMin, bMax, bLen, obb);
    }

    private void _computeObbDimensions(Attribute positions,
        ReadableDoubleArray v0, ReadableDoubleArray v1, ReadableDoubleArray v2,
        WritableDoubleArray min, WritableDoubleArray max)
    {
        _findExtremalProjs_OneDir(positions, v0, minmax);
        min.set(0, minmax.get(0));
        max.set(0, minmax.get(1));
        _findExtremalProjs_OneDir(positions, v1, minmax);
        min.set(1, minmax.get(0));
        max.set(1, minmax.get(1));
        _findExtremalProjs_OneDir(positions, v2, minmax);
        min.set(2, minmax.get(0));
        max.set(2, minmax.get(1));
    }

    private final WritableDoubleArray tmp = DoubleArrays.create(3);
    private final WritableDoubleArray rot = DoubleArrays.create(new double[]
    { 1, 0, 0, 0, 1, 0, 0, 0, 1 });
    private final WritableDoubleArray q = DoubleArrays.create(3);

    private void _finalizeOBB(ReadableDoubleArray v0, ReadableDoubleArray v1,
        ReadableDoubleArray v2, ReadableDoubleArray min,
        ReadableDoubleArray max, ReadableDoubleArray len, Obb obb)
    {
        rot.set(0, v0.get(0));
        rot.set(1, v0.get(1));
        rot.set(2, v0.get(2));
        rot.set(3, v1.get(0));
        rot.set(4, v1.get(1));
        rot.set(5, v1.get(2));
        rot.set(6, v2.get(0));
        rot.set(7, v2.get(1));
        rot.set(8, v2.get(2));
        Vec.quatFromMat3(obb.quaternion, rot);

        // midpoint expressed in the OBB's own coordinate system
        Vec.v3add(q, min, max);
        Vec.v3scale(q, q, 0.5);

        // Compute midpoint expressed in the standard base
        Vec.v3scale(obb.center, v0, q.get(0));
        Vec.v3scale(tmp, v1, q.get(1));
        Vec.v3add(obb.center, obb.center, tmp);
        Vec.v3scale(tmp, v2, q.get(2));
        Vec.v3add(obb.center, obb.center, tmp);

        Vec.v3scale(obb.halfSize, len, 0.5);
    }

    private static double _getQualityValue(ReadableDoubleArray len)
    {
        double len0 = len.get(0);
        double len1 = len.get(1);
        double len2 = len.get(2);
        return len0 * len1 + len0 * len2 + len1 * len2; // half box area
    }

}
