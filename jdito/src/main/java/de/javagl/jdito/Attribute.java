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
 * A port of the Attribute interface from dito.ts.
 * 
 * Note: This structure is largely obsolete with the introduction of the
 * {@link ReadableDoubleArray} and {@link WritableDoubleArray} interfaces. It is
 * retained only for consistency with the original implementation.
 */
class Attribute
{
    /** Data uses the data type of the vertex attribute */
    ReadableDoubleArray data;

    /** Components per vertex */
    int size;

    /** Index into data array i.e. not a byte offset */
    int offsetIdx;

    /** Stride across data array i.e. not a byte stride */
    int strideIdx;
}