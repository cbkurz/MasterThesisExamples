package org.kurz.ma.examples.kieker2uml.uml;

import java.util.UUID;

public class Message {

    private final String id = UUID.randomUUID().toString();
    private final String label;
    private final MessageType type;
    private final Lifeline from;
    private final Lifeline to;

    public Message(final Lifeline from, final Lifeline to, final MessageType type, final String label) {
        this.label = label;
        this.type = type;
        this.from = from;
        this.to = to;
        to.messageIncoming(this);
    }

    public String getLabel() {
        return label;
    }

    public MessageType getType() {
        return type;
    }

    public Lifeline getFrom() {
        return from;
    }

    public Lifeline getTo() {
        return to;
    }

    public String getId() {
        return id;
    }

    public enum MessageType {
        SYNCHRONOUS, ASYNCHRONOUS, SYNCHRONOUS_REPLY, ASYNCHRONOUS_REPLY
    }
}
