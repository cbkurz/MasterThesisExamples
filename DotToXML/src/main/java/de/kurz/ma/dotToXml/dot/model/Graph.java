package de.kurz.ma.dotToXml.dot.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Graph {
    final private Set<Node> nodes;


    final private Set<Edge> edges;

    final private String ID = UUID.randomUUID().toString();

    public Graph() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public Set<Node> getNodes() {
        return Set.copyOf(nodes);
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public String getID() {
        return ID;
    }
}
