package de.kurz.ma.dotToXml.dot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;

public class Node {

    final private Integer number;
    final private Map<String, String> attributes;
    private Edge incoming;

    final private String ID = UUID.randomUUID().toString();

    private final List<Edge> outgoing = new ArrayList<>();

    public Node(final Integer number, final Map<String, String> attributes) {
        this.number = number;
        this.attributes = attributes;
    }

    public void setIncoming(final Edge incoming) {
        if (nonNull(this.incoming)) {
            throw new IllegalStateException(String.format("Edge is already set for Node.\nEdge: %s\nNode: %s", this.incoming, this.attributes.get("label")));
        }
        this.incoming = incoming;
    }

    public void addOutgoing(final Edge outgoing) {
        this.outgoing.add(outgoing);
    }

    public Integer getNumber() {
        return number;
    }

    public String getID() {
        return ID;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Edge getIncoming() {
        return incoming;
    }

    public List<Edge> getOutgoing() {
        return outgoing;
    }

}
