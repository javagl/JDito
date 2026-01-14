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
 * A class representing the result of the oriented bounding box computation from
 * {@link JDito}.
 */
public class OrientedBoundingBox
{
    /**
     * The center of the oriented bounding box, as a 3-element array
     */
    public double center[];

    /**
     * The half-axes of the oriented bounding box.
     * 
     * This is 9-element array that represents a 3x3 matrix (in column-major
     * order) that describes the orientation and half-size of the bounding box.
     */
    public double halfAxes[];
}