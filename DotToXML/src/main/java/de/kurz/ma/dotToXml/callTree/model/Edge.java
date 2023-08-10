package de.kurz.ma.dotToXml.callTree.model;

import de.kurz.ma.dotToXml.xml.XmlSupport;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.UUID;

public class Edge implements XmlSupport.ElementWriter {

    final private String label;
    final private Node from;
    final private Node to;

    final private String ID = UUID.randomUUID().toString();

    public Edge(final String label, final Node from, final Node to) {
        this.label = label;
        this.from = from;
        from.addOutgoing(this);
        this.to = to;
        to.setIncoming(this);
    }

    public String getID() {
        return ID;
    }

    @Override
    public void writeToXml(final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeEmptyElement("edges");
        XmlSupport.writeIdAttribute(xsw, ID);
        xsw.writeAttribute("label", this.label);
        xsw.writeAttribute("from", this.from.getID());
        xsw.writeAttribute("to", this.to.getID());
    }
}
