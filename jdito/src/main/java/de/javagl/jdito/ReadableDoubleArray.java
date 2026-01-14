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
 * An thin abstraction layer for a <code>double[]</code> array
 */
public interface ReadableDoubleArray
{
    /**
     * Returns the value at the specified index
     * 
     * @param index The index
     * @return THe value
     */
    double get(int index);

    /**
     * Returns the length of this array
     * 
     * @return The length
     */
    int length();

}