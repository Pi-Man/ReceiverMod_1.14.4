package io.github.jdiemke.triangulation;

import java.util.Objects;

/**
 * 2D edge class implementation.
 * 
 * @author Johannes Diemke
 */
public class Edge2D {

    public Vector2D a;
    public Vector2D b;

    /**
     * Constructor of the 2D edge class used to create a new edge instance from
     * two 2D vectors describing the edge's vertices.
     * 
     * @param a
     *            The first vertex of the edge
     * @param b
     *            The second vertex of the edge
     */
    public Edge2D(Vector2D a, Vector2D b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge2D edge2D = (Edge2D) o;
        return a.equals(edge2D.a) &&
                b.equals(edge2D.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}