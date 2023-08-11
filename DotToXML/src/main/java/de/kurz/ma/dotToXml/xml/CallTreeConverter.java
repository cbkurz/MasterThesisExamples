package de.kurz.ma.dotToXml.xml;

import de.kurz.ma.dotToXml.dot.GraphFactory;
import de.kurz.ma.dotToXml.dot.model.Edge;
import de.kurz.ma.dotToXml.dot.model.Graph;
import de.kurz.ma.dotToXml.dot.model.Node;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class CallTreeConverter implements XmlSupport.ElementWriter {

    final private Graph graph;

    public CallTreeConverter(final Graph graph) {
        this.graph = graph;
    }

    @Override
    public void writeToXml(final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.setDefaultNamespace(GraphFactory.NAMESPACE_CALL_TREE);
        xsw.writeStartElement(GraphFactory.NAMESPACE_CALL_TREE, "Graph");
        xsw.writeAttribute("xmlns", GraphFactory.NAMESPACE_CALL_TREE);
        XmlSupport.writeXmiAttributes(xsw);
        XmlSupport.writeIdAttribute(xsw, graph.getID());
        graph.getNodes().forEach(n -> {
            try {
                writeNode(xsw, n);
            } catch (XMLStreamException e) {
                throw new XmlSupport.UncheckedXMLStreamException(e);
            }
        });
        graph.getEdges().forEach(e -> {
            try {
                writeEdge(xsw, e);
            } catch (XMLStreamException ex) {
                throw new XmlSupport.UncheckedXMLStreamException(ex);
            }
        });
        xsw.writeEndElement();
    }

    private void writeEdge(final XMLStreamWriter xsw, Edge e) throws XMLStreamException {
        xsw.writeEmptyElement("edges");
        XmlSupport.writeIdAttribute(xsw, e.getID());
        xsw.writeAttribute("label", e.getAttributes().get("label"));
        xsw.writeAttribute("from", e.getFrom().getID());
        xsw.writeAttribute("to", e.getTo().getID());
    }

    private void writeNode(final XMLStreamWriter xsw, Node n) throws XMLStreamException {
        xsw.writeEmptyElement("nodes");
        XmlSupport.writeIdAttribute(xsw, n.getID());
        xsw.writeAttribute("label", n.getAttributes().get("label"));
        xsw.writeAttribute("number", n.getNumber().toString());
        if (nonNull(n.getIncoming())) {
            xsw.writeAttribute("incoming", n.getIncoming().getID());
        }
        if (!n.getOutgoing().isEmpty()) {
            xsw.writeAttribute("outgoing", n.getOutgoing().stream().map(Edge::getID).collect(Collectors.joining(" ")));
        }
    }
}
