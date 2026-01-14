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
 * Default implementation of a {@link WritableDoubleArray}
 */
class DefaultDoubleArray implements WritableDoubleArray
{
    /**
     * The backing array
     */
    private final double[] data;

    /**
     * The offset into the array
     */
    private final int offset;

    /**
     * The length of this array
     */
    private final int length;

    /**
     * Creates a new instance that is backed by the specified slice of the given
     * data
     * 
     * @param data The data
     * @param offset The offset
     * @param length The length
     */
    DefaultDoubleArray(double data[], int offset, int length)
    {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public double get(int index)
    {
        return data[index + offset];
    }

    @Override
    public void set(int index, double value)
    {
        data[index + offset] = value;
    }

    @Override
    public int length()
    {
        return length;
    }
}