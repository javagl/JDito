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
 * A class summarizing the vector math related functions ported from dito.ts
 */
@SuppressWarnings("javadoc")
class Vec
{
    static void v3copy(WritableDoubleArray out, ReadableDoubleArray a,
        int inOffset)
    {
        out.set(0, a.get(inOffset + 0));
        out.set(1, a.get(inOffset + 1));
        out.set(2, a.get(inOffset + 2));
    }

    static void v3add(WritableDoubleArray out, ReadableDoubleArray a,
        ReadableDoubleArray b)
    {
        out.set(0, a.get(0) + b.get(0));
        out.set(1, a.get(1) + b.get(1));
        out.set(2, a.get(2) + b.get(2));
    }

    static void v3subtract(WritableDoubleArray out, ReadableDoubleArray a,
        ReadableDoubleArray b)
    {
        out.set(0, a.get(0) - b.get(0));
        out.set(1, a.get(1) - b.get(1));
        out.set(2, a.get(2) - b.get(2));
    }

    static void v3scale(WritableDoubleArray out, ReadableDoubleArray a,
        double scale)
    {
        out.set(0, a.get(0) * scale);
        out.set(1, a.get(1) * scale);
        out.set(2, a.get(2) * scale);
    }

    static void v3copy(WritableDoubleArray out, ReadableDoubleArray a)
    {
        v3copy(out, a, 0);
    }

    static void v3cross(WritableDoubleArray out, ReadableDoubleArray a,
        ReadableDoubleArray b)
    {
        double ax = a.get(0);
        double ay = a.get(1);
        double az = a.get(2);
        double bx = b.get(0);
        double by = b.get(1);
        double bz = b.get(2);

        out.set(0, ay * bz - az * by);
        out.set(1, az * bx - ax * bz);
        out.set(2, ax * by - ay * bx);
    }

    static void v3normalize(WritableDoubleArray out, ReadableDoubleArray a)
    {
        double a0 = a.get(0);
        double a1 = a.get(1);
        double a2 = a.get(2);
        double len = a0 * a0 + a1 * a1 + a2 * a2;
        if (len > 0)
        {
            double invLen = 1 / Math.sqrt(len);

            out.set(0, a0 * invLen);
            out.set(1, a1 * invLen);
            out.set(2, a2 * invLen);
        }
    }

    static double v3squaredLength(ReadableDoubleArray a)
    {
        double a0 = a.get(0);
        double a1 = a.get(1);
        double a2 = a.get(2);
        return a0 * a0 + a1 * a1 + a2 * a2;
    }

    static double v3squaredDistance(ReadableDoubleArray a,
        ReadableDoubleArray b)
    {
        double a0 = a.get(0);
        double a1 = a.get(1);
        double a2 = a.get(2);
        double b0 = b.get(0);
        double b1 = b.get(1);
        double b2 = b.get(2);

        double x = b0 - a0;
        double y = b1 - a1;
        double z = b2 - a2;
        return x * x + y * y + z * z;
    }

    static double v3dot(ReadableDoubleArray a, ReadableDoubleArray b)
    {
        double a0 = a.get(0);
        double a1 = a.get(1);
        double a2 = a.get(2);
        double b0 = b.get(0);
        double b1 = b.get(1);
        double b2 = b.get(2);
        return a0 * b0 + a1 * b1 + a2 * b2;
    }

    static void quatFromMat3(WritableDoubleArray out, ReadableDoubleArray m)
    {
        // Algorithm in Ken Shoemake's article in 1987 SIGGRAPH course notes
        // article "Quaternion Calculus and Fast Animation".
        double m0 = m.get(0);
        double m1 = m.get(1);
        double m2 = m.get(2);
        double m3 = m.get(3);
        double m4 = m.get(4);
        double m5 = m.get(5);
        double m6 = m.get(6);
        double m7 = m.get(7);
        double m8 = m.get(8);

        double fTrace = m0 + m4 + m8;

        if (fTrace > 0.0)
        {
            // |w| > 1/2, may as well choose w > 1/2
            double fRoot = Math.sqrt(fTrace + 1.0); // 2w
            out.set(3, 0.5 * fRoot);
            fRoot = 0.5 / fRoot; // 1/(4w)
            out.set(0, (m5 - m7) * fRoot);
            out.set(1, (m6 - m2) * fRoot);
            out.set(2, (m1 - m3) * fRoot);
        }
        else
        {
            // |w| <= 1/2
            int i = 0;
            if (m4 > m0)
            {
                i = 1;
            }
            if (m8 > m.get(i * 3 + i))
            {
                i = 2;
            }
            int j = (i + 1) % 3;
            int k = (i + 2) % 3;

            double mA = m.get(i * 3 + i);
            double mB = m.get(j * 3 + j);
            double mC = m.get(k * 3 + k);
            double fRoot = Math.sqrt(mA - mB - mC + 1.0);
            out.set(i, 0.5 * fRoot);
            fRoot = 0.5 / fRoot;
            out.set(3, (m.get(j * 3 + k) - m.get(k * 3 + j)) * fRoot);
            out.set(j, (m.get(j * 3 + i) + m.get(i * 3 + j)) * fRoot);
            out.set(k, (m.get(k * 3 + i) + m.get(i * 3 + k)) * fRoot);
        }
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Vec()
    {
        // Private constructor to prevent instantiation
    }

}
