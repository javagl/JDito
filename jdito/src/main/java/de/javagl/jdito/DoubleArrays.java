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
 * Utility methods related to {@link ReadableDoubleArray} instances.
 */
public class DoubleArrays
{
    /**
     * Creates a new {@link ReadableDoubleArray} from the given array
     * 
     * @param array The array
     * @return The {@link ReadableDoubleArray}
     */
    public static ReadableDoubleArray fromArray(double array[])
    {
        return create(array);
    }

    /**
     * Creates a new {@link WritableDoubleArray} with the given length
     * 
     * @param length The length
     * @return The {@link WritableDoubleArray}
     */
    static WritableDoubleArray create(int length)
    {
        return create(new double[length], 0, length);
    }

    /**
     * Creates a new {@link WritableDoubleArray} backed by the given data
     * 
     * @param data The data
     * @return The {@link WritableDoubleArray}
     */
    static WritableDoubleArray create(double data[])
    {
        return create(data, 0, data.length);
    }

    /**
     * Creates a new {@link WritableDoubleArray} that is backed by the specified
     * slice of the given data
     * 
     * @param data The data
     * @param offset The offset
     * @param length The length
     * @return The {@link WritableDoubleArray}
     */
    static WritableDoubleArray create(double data[], int offset, int length)
    {
        return new DefaultDoubleArray(data, offset, length);
    }

    /**
     * Returns a plain double array from the given instance
     * 
     * @param r The {@link ReadableDoubleArray}
     * @return The plain array
     */
    static double[] toArray(ReadableDoubleArray r)
    {
        double a[] = new double[r.length()];
        for (int i = 0; i < r.length(); i++)
        {
            a[i] = r.get(i);
        }
        return a;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private DoubleArrays()
    {
        // Private constructor to prevent instantiation
    }

}
