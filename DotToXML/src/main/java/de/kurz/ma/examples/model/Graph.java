package de.kurz.ma.examples.model;

import java.util.HashSet;
import java.util.Set;

public class Graph {
    final private Set<Node> nodes;
    final private Set<Edge> edges;

    public Graph() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }
}
