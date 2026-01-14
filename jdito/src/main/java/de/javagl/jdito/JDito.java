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
 * A class for computing the oriented bounding box for a set of 3D points.
 */
public class JDito
{
    /**
     * A thread-local instance of {@link Dito}.
     * 
     * The original dito.ts implementation used a bunch of static variables,
     * meaning that a direct port could not be thread safe. Given that the
     * functionality is offered here as a single static function as well,
     * thread-safety is achieved with a thread-local instance of the Dito
     * object.
     */
    private static final ThreadLocal<Dito> threadLocalDito =
        ThreadLocal.withInitial(() -> new Dito());

    /**
     * Compute the oriented bounding box for the given points.
     * 
     * Three consecutive elements of the given array are assumed to represent
     * the x, y, and z-coordinates of a single point.
     * 
     * For the case that the input data is not stored as a flat
     * <code>double</code> array, the {@link #compute(ReadableDoubleArray)}
     * method can be used.
     * 
     * @param points The points
     * @return The oriented bounding box
     */
    public static OrientedBoundingBox compute(double[] points)
    {
        return compute(DoubleArrays.fromArray(points));
    }

    /**
     * Compute the oriented bounding box for the given points.
     * 
     * Three consecutive elements of the given array are assumed to represent
     * the x, y, and z-coordinates of a single point.
     * 
     * @param points The points
     * @return The oriented bounding box
     */
    public static OrientedBoundingBox compute(ReadableDoubleArray points)
    {
        Dito dito = threadLocalDito.get();
        Obb obb = new Obb();
        Attribute positions = new Attribute();
        positions.data = points;
        positions.size = 3;
        positions.offsetIdx = 0;
        positions.strideIdx = 3;
        dito.computeOBB(positions, obb);

        double center[] = DoubleArrays.toArray(obb.center);
        double halfSize[] = DoubleArrays.toArray(obb.halfSize);
        double quaternion[] = DoubleArrays.toArray(obb.quaternion);

        OrientedBoundingBox result = new OrientedBoundingBox();
        result.center = center;
        result.halfAxes = VecEx.matrixFrom(quaternion, halfSize);
        return result;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private JDito()
    {
        // Private constructor to prevent instantiation
    }

}
