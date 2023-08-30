package org.kurz.ma.examples.kieker2uml.uml;

import java.util.ArrayList;
import java.util.List;

import static org.kurz.ma.examples.kieker2uml.uml.Message.MessageType;

public class Lifeline implements Comparable<Lifeline> {

    private final Long id;

    private final List<Message> messagesOutgoing = new ArrayList<>();
    private final List<Message> messagesIncoming = new ArrayList<>();
    private final LifelineType type;
    private final String label;

    public Lifeline(final Long id, final LifelineType type, final String label) {
        this.id = id;
        this.type = type;
        this.label = label;
    }

    public void messageOutgoing(final Lifeline to, final MessageType type, final String label) {
        messagesOutgoing.add(new Message(this, to, type, label));
    }
    protected void messageIncoming(final Message message) {
        messagesIncoming.add(message);
    }

    @Override
    public int compareTo(final Lifeline o) {
        if (LifelineType.ACTOR.equals(this.type) && LifelineType.ACTOR.equals((o.type))) {
            return (int) (this.id - o.id);
        }
        if (LifelineType.ACTOR.equals(this.type)) {
            return -1;
        }
        if (LifelineType.ACTOR.equals((o.type))) {
            return 1;
        }
        return (int) (this.id - o.id);
    }

    public Long getId() {
        return id;
    }

    public List<Message> getMessagesOutgoing() {
        return messagesOutgoing;
    }

    public List<Message> getMessagesIncoming() {
        return messagesIncoming;
    }

    public LifelineType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }


    public enum LifelineType {
        ACTOR, OBJECT
    }
}
