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
 * A class summarizing extended vector math functions
 */
class VecEx
{
    /**
     * Create a 3x3 matrix describing the half-axis representation that is
     * equivalent to the given quaternion and half-size
     * 
     * @param quaternion A 4-element array for the quaternion, in scalar-last
     *        representation
     * @param halfSize The half-size, as a 3-element array
     * @return A 9-element array representing the 3x3 matrix in column major
     *         order.
     */
    static double[] matrixFrom(double[] quaternion, double[] halfSize)
    {
        double m[] = quaternionToMatrix(quaternion);
        scale(m, halfSize);
        return m;
    }

    /**
     * Compute a 3x3 rotation matrix from the given quaternion.
     * 
     * The quaternion (in scalar-last representation) is given as a 4-element
     * array. The result is a 9-element array describing the rotation matrix in
     * column-major order.
     * 
     * @param quaternion The quaternion
     * @return The matrix
     */
    private static double[] quaternionToMatrix(double quaternion[])
    {
        double Qx = quaternion[0];
        double Qy = quaternion[1];
        double Qz = quaternion[2];
        double Qw = quaternion[3];
        double lenSquared = Qx * Qx + Qy * Qy + Qz * Qz + Qw * Qw;
        double invLength = 1.0 / Math.sqrt(lenSquared);

        double qx = quaternion[0] * invLength;
        double qy = quaternion[1] * invLength;
        double qz = quaternion[2] * invLength;
        double qw = quaternion[3] * invLength;

        double m[] = new double[9];
        m[0] = 1.0 - (qy * qy + qz * qz) * 2.0;
        m[3] = 2.0 * (qx * qy - qw * qz);
        m[6] = 2.0 * (qx * qz + qw * qy);
        
        m[1] = 2.0 * (qx * qy + qw * qz);
        m[4] = 1.0 - (qx * qx + qz * qz) * 2.0;
        m[7] = 2.0 * (qy * qz - qw * qx);
        
        m[2] = 2.0 * (qx * qz - qw * qy);
        m[5] = 2.0 * (qy * qz + qw * qx);
        m[8] = 1.0 - (qx * qx + qy * qy) * 2.0;
        return m;
    }

    /**
     * Apply the given scale factor to the given matrix.
     * 
     * The scale factor is a 3-element array. The matrix is a 9-element array
     * representing the matrix in column-major order.
     * 
     * @param m The matrix
     * @param s The scale factor
     */
    private static void scale(double m[], double s[])
    {
        m[0] *= s[0];
        m[1] *= s[0];
        m[2] *= s[0];
        m[3] *= s[1];
        m[4] *= s[1];
        m[5] *= s[1];
        m[6] *= s[2];
        m[7] *= s[2];
        m[8] *= s[2];
    }

    /**
     * Private constructor to prevent instantiation
     */
    private VecEx()
    {
        // Private constructor to prevent instantiation
    }

}
