/**
 * JDito - DiTO oriented bounding box computation
 */
package de.javagl.dito.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.javagl.jdito.JDito;
import de.javagl.jdito.OrientedBoundingBox;
import de.javagl.jdito.ReadableDoubleArray;

/**
 * Basic JDito examples
 */
public class JDitoExamples
{
    /**
     * The entry point
     * 
     * @param args Not used
     */
    public static void main(String[] args)
    {
        computeUnitCube();
        computeBox();
        computeRandomBox();
    }

    /**
     * An example of computing the bounding box of a unit cube.
     * 
     * Booooring!
     */
    private static void computeUnitCube()
    {
        // @formatter:off
        double points[] = new double[]
        { 
            0.0, 0.0, 0.0, 
            1.0, 0.0, 0.0, 
            0.0, 1.0, 0.0,
            1.0, 1.0, 0.0, 
            0.0, 0.0, 1.0, 
            1.0, 0.0, 1.0, 
            0.0, 1.0, 1.0, 
            1.0, 1.0, 1.0, 
        };
        // @formatter:on
        OrientedBoundingBox bb = JDito.compute(points);

        System.out.println("OBB of a unit cube:");
        System.out.println(Arrays.toString(bb.center));
        System.out.println(Arrays.toString(bb.halfAxes));
    }

    /**
     * An example of computing the bounding box of a 
     * simple box
     */
    private static void computeBox()
    {
        // @formatter:off
        double points[] = new double[]
        { 
            0.0, 1.0, 0.0, 
            2.0, 5.0, 0.0, 
            4.0, 4.0, 0.0,
            2.0, 0.0, 0.0, 
            0.0, 1.0, 1.0, 
            2.0, 5.0, 1.0, 
            4.0, 4.0, 1.0,
            2.0, 0.0, 1.0, 
        };
        // @formatter:on
        OrientedBoundingBox bb = JDito.compute(points);

        System.out.println("OBB of a box:");
        System.out.println(Arrays.toString(bb.center));
        System.out.println(Arrays.toString(bb.halfAxes));
    }

    /**
     * An example of computing the bounding box of a box of random points.
     */
    private static void computeRandomBox()
    {
        double minX = 1.0;
        double minY = 2.0;
        double minZ = 3.0;
        double maxX = 6.0;
        double maxY = 5.0;
        double maxZ = 4.0;
        int n = 100000;
        Random random = new Random(0);

        // The following is a dummy example to illustrate some
        // possible real-world use case:

        // A dummy class representing a 3D point
        class Point3D
        {
            double x;
            double y;
            double z;
        }
        List<Point3D> points = new ArrayList<Point3D>();
        for (int i = 0; i < n; i++)
        {
            Point3D p = new Point3D();
            p.x = minX + random.nextDouble() * (maxX - minX);
            p.y = minY + random.nextDouble() * (maxY - minY);
            p.z = minZ + random.nextDouble() * (maxZ - minZ);
            points.add(p);
        }

        // An example for using the ReadableDoubleArray interface:
        // Implement the interface, backed by the list of points,
        // to let it appear like a flat array.
        ReadableDoubleArray array = new ReadableDoubleArray()
        {
            @Override
            public int length()
            {
                return points.size() * 3;
            }

            @Override
            public double get(int index)
            {
                Point3D p = points.get(index / 3);
                int component = index % 3;
                if (component == 0)
                {
                    return p.x;
                }
                else if (component == 1)
                {
                    return p.y;
                }
                return p.z;
            }
        };

        OrientedBoundingBox bb = JDito.compute(array);

        System.out.println("OBB of " + n + " random points");
        System.out.println("  centered at    (3.5, 3.5, 3.5)");
        System.out.println("  with half-size (2.5, 1.5, 0.5)");
        System.out.println(Arrays.toString(bb.center));
        System.out.println(Arrays.toString(bb.halfAxes));
    }

}
