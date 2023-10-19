package org.kurz.ma.examples.kieker2uml.filter;

import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.SynchronousCallMessage;
import kieker.model.system.model.SynchronousReplyMessage;
import org.eclipse.uml2.uml.MessageSort;

public class Util {
    /**
     * {@link MessageSort} is an enumeration of different kinds of messages.
     * This enumeration determines if it is a call or a reply.
     * This method only expects there to be 2 types {MessageSort.SYNCH_CALL_LITERAL} or {MessageSort.REPLY_LITERAL}
     * {@link MessageSort}
     * @param message the kieker trace message, two types are considered {@link SynchronousCallMessage} and {@link SynchronousReplyMessage} if neither are matched an exception is thrown.
     * @return MessageSort Literal
     */
    public static MessageSort getMessageSort(final AbstractMessage message) {
        if (message instanceof SynchronousCallMessage) {
            return MessageSort.SYNCH_CALL_LITERAL;
        }
        if (message instanceof SynchronousReplyMessage) {
            return MessageSort.REPLY_LITERAL;
        }
        throw new RuntimeException("Unexpected message type of: " + message.getClass());

    }
}
