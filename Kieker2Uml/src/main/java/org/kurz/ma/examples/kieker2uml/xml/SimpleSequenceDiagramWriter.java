package org.kurz.ma.examples.kieker2uml.xml;

import org.kurz.ma.examples.kieker2uml.model.Lifeline;
import org.kurz.ma.examples.kieker2uml.model.Message;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Set;

import static java.util.stream.Collectors.joining;

public class SimpleSequenceDiagramWriter implements XmlSupport.ElementWriter {

    public static final String MY_SEQUENCE_DIAGRAM_NAMESPACE = "mySequenceDiagramNamespace";
    private final Set<Lifeline> lifelines;

    public SimpleSequenceDiagramWriter(final Set<Lifeline> lifelineSet) {
        this.lifelines = lifelineSet;
    }

    @Override
    public void writeToXml(final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.setDefaultNamespace(MY_SEQUENCE_DIAGRAM_NAMESPACE);
        xsw.writeStartElement(MY_SEQUENCE_DIAGRAM_NAMESPACE, "Model");
        xsw.writeAttribute("xmlns", MY_SEQUENCE_DIAGRAM_NAMESPACE);
        XmlSupport.writeXmiAttributes(xsw);
        lifelines.forEach(l -> writeLifeline(xsw, l));
        lifelines.stream().flatMap(l -> l.getMessagesOutgoing().stream()).toList().forEach(m -> writeMessage(xsw, m));
        lifelines.stream().flatMap(l -> l.getMessagesIncoming().stream()).toList().forEach(m -> writeMessage(xsw, m));
        xsw.writeEndElement();
    }

    private void writeLifeline(final XMLStreamWriter xsw, final Lifeline l) {
        try {
            xsw.writeEmptyElement(MY_SEQUENCE_DIAGRAM_NAMESPACE, "Lifeline");
            xsw.writeAttribute("id", l.getId().toString());
            xsw.writeAttribute("label", l.getLabel());
            xsw.writeAttribute("type", l.getType().toString());
            xsw.writeAttribute("messagesOutgoing", l.getMessagesOutgoing().stream().map(Message::getId).collect(joining(" ")));
            xsw.writeAttribute("messagesIncoming", l.getMessagesIncoming().stream().map(Message::getId).collect(joining(" ")));
        } catch (XMLStreamException e) {
            throw new XmlSupport.UncheckedXMLStreamException(e);
        }
    }

    private void writeMessage(final XMLStreamWriter xsw, final Message m) {
        try {
            xsw.writeEmptyElement(MY_SEQUENCE_DIAGRAM_NAMESPACE, "Message");
            xsw.writeAttribute("id", m.getId());
            xsw.writeAttribute("label", m.getLabel());
            xsw.writeAttribute("from", m.getFrom().getId().toString());
            xsw.writeAttribute("to", m.getTo().getId().toString());
        } catch (XMLStreamException e) {
            throw new XmlSupport.UncheckedXMLStreamException(e);
        }
    }
}
