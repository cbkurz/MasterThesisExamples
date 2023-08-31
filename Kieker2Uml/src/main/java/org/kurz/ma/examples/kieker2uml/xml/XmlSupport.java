package org.kurz.ma.examples.kieker2uml.xml;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;

public class XmlSupport {

    final public static String XMI_NS = "http://www.omg.org/XMI";
    final public static String XMI_VERSION = "2.0";

    public static void writeXml(Path pathToFile, final ElementWriter elementWriter) {
        try (final FileOutputStream fos = new FileOutputStream(pathToFile.toFile())) {
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            final XMLStreamWriter writer = output.createXMLStreamWriter(fos, "ASCII");
            try {
                writer.writeStartDocument("ASCII", "1.0");
                elementWriter.writeToXml(writer);
                writer.writeEndDocument();
            } finally {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXMLStreamException(e);
        }
    }

    public interface ElementWriter {
        void writeToXml(XMLStreamWriter xsw) throws XMLStreamException;
    }

    public static class UncheckedXMLStreamException extends RuntimeException {
        public UncheckedXMLStreamException(final XMLStreamException e) {
            super(Objects.requireNonNull(e));
        }
    }


    public static void writeIdAttribute(XMLStreamWriter xsw, final String id) throws XMLStreamException {
        xsw.writeAttribute("xmi:id", id);
    }

    public static void writeXmiAttributes(XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute("xmlns:xmi", XMI_NS);
        xsw.writeAttribute("xmi:version", XMI_VERSION);
    }

}
