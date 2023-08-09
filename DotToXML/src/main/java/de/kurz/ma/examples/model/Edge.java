package de.kurz.ma.examples.model;

public class Edge {

    final private Edge from;
    final private Edge to;

    public Edge(final Edge from, final Edge edge) {
        this.from = from;
        to = edge;
    }
}
