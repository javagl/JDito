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
 * A port of the ExtremalPoints class from dito.ts
 */
@SuppressWarnings("javadoc")
class ExtremalPoints
{
    private static final int numPoints = 7;

    final double[] buffer;
    final WritableDoubleArray minProj;
    final WritableDoubleArray maxProj;
    final WritableDoubleArray minVert[] = new WritableDoubleArray[numPoints];
    final WritableDoubleArray maxVert[] = new WritableDoubleArray[numPoints];

    ExtremalPoints(Attribute positions)
    {
        // setup storage
        int bufferSize = numPoints * (1 + 1 + 3 + 3);
        this.buffer = new double[bufferSize];

        int offset = 0;
        this.minProj = DoubleArrays.create(this.buffer, offset, numPoints);
        offset += numPoints;

        this.maxProj = DoubleArrays.create(this.buffer, offset, numPoints);
        offset += numPoints;

        for (int i = 0; i < numPoints; ++i)
        {
            this.minVert[i] = DoubleArrays.create(this.buffer, offset, 3);
            offset += 3;
        }
        for (int i = 0; i < numPoints; ++i)
        {
            this.maxVert[i] = DoubleArrays.create(this.buffer, offset, 3);
            offset += 3;
        }

        // init storage
        for (int i = 0; i < numPoints; ++i)
        {
            this.minProj.set(i, Double.POSITIVE_INFINITY);
            this.maxProj.set(i, Double.NEGATIVE_INFINITY);
        }
        int minIndices[] = new int[numPoints];
        int maxIndices[] = new int[numPoints];

        ReadableDoubleArray data = positions.data;
        int offsetIdx = positions.offsetIdx;
        int strideIdx = positions.strideIdx;

        // find extremal points
        for (int i = offsetIdx; i < data.length(); i += strideIdx)
        {
            // Slab 0: dir {1, 0, 0}
            double proj = data.get(i);
            if (proj < this.minProj.get(0))
            {
                this.minProj.set(0, proj);
                minIndices[0] = i;
            }
            if (proj > this.maxProj.get(0))
            {
                this.maxProj.set(0, proj);
                maxIndices[0] = i;
            }

            // Slab 1: dir {0, 1, 0}
            proj = data.get(i + 1);
            if (proj < this.minProj.get(1))
            {
                this.minProj.set(1, proj);
                minIndices[1] = i;
            }
            if (proj > this.maxProj.get(1))
            {
                this.maxProj.set(1, proj);
                maxIndices[1] = i;
            }

            // Slab 2: dir {0, 0, 1}
            proj = data.get(i + 2);
            if (proj < this.minProj.get(2))
            {
                this.minProj.set(2, proj);
                minIndices[2] = i;
            }
            if (proj > this.maxProj.get(2))
            {
                this.maxProj.set(2, proj);
                maxIndices[2] = i;
            }

            // Slab 3: dir {1, 1, 1}
            proj = data.get(i) + data.get(i + 1) + data.get(i + 2);
            if (proj < this.minProj.get(3))
            {
                this.minProj.set(3, proj);
                minIndices[3] = i;
            }
            if (proj > this.maxProj.get(3))
            {
                this.maxProj.set(3, proj);
                maxIndices[3] = i;
            }

            // Slab 4: dir {1, 1, -1}
            proj = data.get(i) + data.get(i + 1) - data.get(i + 2);
            if (proj < this.minProj.get(4))
            {
                this.minProj.set(4, proj);
                minIndices[4] = i;
            }
            if (proj > this.maxProj.get(4))
            {
                this.maxProj.set(4, proj);
                maxIndices[4] = i;
            }

            // Slab 5: dir {1, -1, 1}
            proj = data.get(i) - data.get(i + 1) + data.get(i + 2);
            if (proj < this.minProj.get(5))
            {
                this.minProj.set(5, proj);
                minIndices[5] = i;
            }
            if (proj > this.maxProj.get(5))
            {
                this.maxProj.set(5, proj);
                maxIndices[5] = i;
            }

            // Slab 6: dir {1, -1, -1}
            proj = data.get(i) - data.get(i + 1) - data.get(i + 2);
            if (proj < this.minProj.get(6))
            {
                this.minProj.set(6, proj);
                minIndices[6] = i;
            }
            if (proj > this.maxProj.get(6))
            {
                this.maxProj.set(6, proj);
                maxIndices[6] = i;
            }
        }

        for (int i = 0; i < numPoints; ++i)
        {
            int index = minIndices[i];
            Vec.v3copy(this.minVert[i], data, index);
            index = maxIndices[i];
            Vec.v3copy(this.maxVert[i], data, index);
        }
        // Note: Normalization of the extremal projection values can be done
        // here.
        // DiTO-14 only needs the extremal vertices, and the extremal
        // projection values for slab 0-2 (to set the initial AABB).
        // Since unit normals are used for slab 0-2, no normalization is
        // needed.
    }
}