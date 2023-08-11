package de.kurz.ma.dotToXml.dot.model;

import de.kurz.ma.dotToXml.xml.XmlSupport;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Graph implements XmlSupport.ElementWriter {
    final private Set<Node> nodes;
    final private Set<Edge> edges;

    final static public String NAMESPACE = "de.kurz.ma.model.CallTree";

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

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    @Override
    public void writeToXml(final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.setDefaultNamespace(NAMESPACE);
        xsw.writeStartElement(NAMESPACE, "Graph");
        xsw.writeAttribute("xmlns", NAMESPACE);
        XmlSupport.writeXmiAttributes(xsw);
        XmlSupport.writeIdAttribute(xsw, ID);
        edges.forEach(e -> {
            try {
                e.writeToXml(xsw);
            } catch (XMLStreamException ex) {
                throw new XmlSupport.UncheckedXMLStreamException(ex);
            }
        });
        nodes.forEach(n -> {
            try {
                n.writeToXml(xsw);
            } catch (XMLStreamException e) {
                throw new XmlSupport.UncheckedXMLStreamException(e);
            }
        });
        xsw.writeEndElement();
    }

    public String getID() {
        return ID;
    }
}
