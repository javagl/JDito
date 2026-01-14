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
 * An abstraction layer for a <code>double[]</code> array
 */
interface WritableDoubleArray extends ReadableDoubleArray
{
    /**
     * Set the value at the specified index
     * 
     * @param index The index
     * @param value The value
     */
    void set(int index, double value);

}