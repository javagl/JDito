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
 * A port of the Orientation class from dito.ts
 */
@SuppressWarnings("javadoc")
class Orientation
{
    // OBB orientation
    final WritableDoubleArray b0 = DoubleArrays.create(new double[]
    { 1, 0, 0 });

    // OBB orientation
    final WritableDoubleArray b1 = DoubleArrays.create(new double[]
    { 0, 1, 0 });

    // OBB orientation
    final WritableDoubleArray b2 = DoubleArrays.create(new double[]
    { 0, 0, 1 });

    // evaluation of OBB for orientation
    double quality = 0.0;
}