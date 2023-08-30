package org.kurz.ma.examples.kieker2uml.uml;

public class Message {

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

    public enum MessageType {
        SYNCHRONOUS, ASYNCHRONOUS, SYNCHRONOUS_REPLY, ASYNCHRONOUS_REPLY
    }
}
