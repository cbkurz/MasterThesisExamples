package de.kurz.ma.dotToXml.dot.model;

import java.util.Map;
import java.util.UUID;

public class Edge {

    final private Map<String , String > attributes;


    final private Node from;
    final private Node to;

    final private String ID = UUID.randomUUID().toString();

    public Edge(final Map<String, String> attributes, final Node from, final Node to) {
        this.attributes = attributes;
        this.from = from;
        from.addOutgoing(this);
        this.to = to;
        to.setIncoming(this);
    }

    public String getID() {
        return ID;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

}
