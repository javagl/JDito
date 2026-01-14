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
 * A port of the Obb interface from dito.ts
 */
class Obb
{
    /**
     * A 3-element array for the center of the OBB
     */
    final WritableDoubleArray center = DoubleArrays.create(3);

    /**
     * A 3-element array for the size of the OBB
     */
    final WritableDoubleArray halfSize = DoubleArrays.create(3);

    /**
     * A 4-element array describing the rotation of the OBB, as a scalar-last
     * quaternion
     */
    final WritableDoubleArray quaternion = DoubleArrays.create(4);
}