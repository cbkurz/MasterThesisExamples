package de.kurz.ma.examples.model;

import java.util.List;

public class Node {

    final private String name;
    final private Integer number;
    private Edge incoming;

    private List<Edge> outgoing;

    public Node(final String name, final Integer number) {
        this.name = name;
        this.number = number;
    }

    public void setIncoming(final Edge incoming) {
        this.incoming = incoming;
    }

    public void setOutgoing(final List<Edge> outgoing) {
        this.outgoing = outgoing;
    }
}
