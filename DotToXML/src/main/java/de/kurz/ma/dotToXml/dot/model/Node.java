package de.kurz.ma.dotToXml.dot.model;

import de.kurz.ma.dotToXml.xml.XmlSupport;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Node implements XmlSupport.ElementWriter {

    final private Integer number;
    final private String label;
    private Edge incoming;

    final private String ID = UUID.randomUUID().toString();

    private final List<Edge> outgoing = new ArrayList<>();

    public Node(final Integer number, final String label) {
        this.number = number;
        this.label = label;
    }

    public void setIncoming(final Edge incoming) {
        if (nonNull(this.incoming)) {
            throw new IllegalStateException(String.format("Edge is already set for Node.\nEdge: %s\nNode: %s", this.incoming, this.label));
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

    @Override
    public void writeToXml(final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeEmptyElement("nodes");
        XmlSupport.writeIdAttribute(xsw, ID);
        xsw.writeAttribute("label", this.label);
        xsw.writeAttribute("number", this.number.toString());
        if (nonNull(this.incoming)) {
            xsw.writeAttribute("incoming", this.incoming.getID());
        }
        if (!this.outgoing.isEmpty()) {
            xsw.writeAttribute("outgoing", this.outgoing.stream().map(Edge::getID).collect(Collectors.joining(" ")));
        }
    }
}
