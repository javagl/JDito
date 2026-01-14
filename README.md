# JDito

A Java based implementation of the DiTO method for computing 
Oriented Bounding Boxes.

### References

This is a Java port of dito.ts, which is a JavaScript port of the 
C++ sample implementation of the DiTO OBB construction method 
"Fast Computation of Tight-Fitting Oriented Bounding Boxes" 
of the book "Game Engine Gems 2".

- [dito.ts](https://github.com/Esri/dito.ts) (commit a7cfa662d1a6d6f0f387deaa1fb9d8edb6f298bc)
- [Original DiTO source code](https://gameenginegems.com/geg2.php) (See supplementary files for Chapter 1)

The original code has largely been ported verbatim (but split into
several classes/files). One change affected the whole code: It's not 
possible to overload the `[...]` operator in Java, as it was done 
in `WritableArrayLike`. Therefore, the necessary abstraction 
is offered as a `ReadableDoubleArray` interface.

### Example

The public interface for this library boils down to a function that receives 
the input points, and returns an `OrientedBoundingBox`:

```java
// The array of input points
double points[] = new double[] { ... }

// Compute the oriented bounding box
OrientedBoundingBox obb = JDito.compute(points);

// Print the results
System.out.println(Arrays.toString(obb.center));
System.out.println(Arrays.toString(obb.halfAxes));
```

There also is a `compute` function that receives a `ReadableDoubleArray` object.
This is an interface that just offers the functions that reflect a read-only array of
`double` values. Arbitrary input data structures can trivially be mapped to this
interface to feed the data into `JDito`.


