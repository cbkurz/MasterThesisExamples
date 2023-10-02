package org.kurz.ma.examples.kieker2uml.uml;

import kieker.common.util.signature.Signature;
import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.AssemblyComponent;
import kieker.model.system.model.MessageTrace;
import kieker.model.system.model.SynchronousCallMessage;
import kieker.model.system.model.SynchronousReplyMessage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

class Uml2Interactions {

    public static final EClass E_CLASS_MESSAGE_OCCURRENCE = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification().eClass();
    public static final EClass E_CLASS_BEHAVIOUR_EXECUTION = UMLFactory.eINSTANCE.createBehaviorExecutionSpecification().eClass();

    static void addInteractionToModel(final Model model, final MessageTrace messageTrace) {
        final EClass eClass = UMLFactory.eINSTANCE.createInteraction().eClass();
        final Interaction interaction = (Interaction) model.createPackagedElement("Interaction-Trace-" + messageTrace.getTraceId() + "-StartAt-" + messageTrace.getStartTimestamp(), eClass);

        addLifelines(interaction, messageTrace.getSequenceAsVector()); // adds lifelines and messages
    }

    /**
     * Creates the following Types in the UML2 Model:
     * * {@link org.eclipse.uml2.uml.Lifeline} - The Lifelines represent the different objects that are interaction within the Trace of the application
     * * {@link org.eclipse.uml2.uml.Message} - The Messages represent the calls that the objects in the application are making to each other
     * * {@link org.eclipse.uml2.uml.MessageOccurrenceSpecification} - This is required by UML2 and connects the Message with the lifeline
     * * {@link org.eclipse.uml2.uml.BehaviorExecutionSpecification} - This is required by UML2 and represents when an object of the application is active
     *
     * @param interaction - Representing the whole interaction all other Types are enclosed by this Type.
     * @param messages    - The Kieker Messages of the {@link MessageTrace}
     */
    private static void addLifelines(final Interaction interaction, final List<AbstractMessage> messages) {
        // assumption: the messages are ordered
        for (final AbstractMessage message : messages) {
            final AssemblyComponent senderComponent = message.getSendingExecution().getAllocationComponent().getAssemblyComponent();
            final AssemblyComponent receiverComponent = message.getReceivingExecution().getAllocationComponent().getAssemblyComponent();

            // getLifeline(name, ignoreCase, createOnDemand) <-- naming of the parameters
            final org.eclipse.uml2.uml.Lifeline senderLifeline = interaction.getLifeline(senderComponent.getIdentifier(), false, true);
            final org.eclipse.uml2.uml.Lifeline receiverLifeline = interaction.getLifeline(receiverComponent.getIdentifier(), false, true);

            createMessage(interaction, message, senderLifeline, receiverLifeline);

        }
    }

    private static void createMessage(final Interaction interaction, final AbstractMessage message, final org.eclipse.uml2.uml.Lifeline senderLifeline, final org.eclipse.uml2.uml.Lifeline receiverLifeline) {
        requireNonNull(interaction, "interaction");
        requireNonNull(message, "message");
        requireNonNull(senderLifeline, "senderLifeline");
        requireNonNull(receiverLifeline, "receiverLifeline");

        final String messageLabel = getMessageLabel(message);
        final org.eclipse.uml2.uml.Message umlMessage = interaction.createMessage(messageLabel);
        final MessageSort messageSort = getMessageSort(message);
        umlMessage.setMessageSort(messageSort);

        final MessageOccurrenceSpecification messageOccurrenceSend = createMessageOccurrence(interaction, umlMessage, senderLifeline, messageLabel + "SendEvent");
        final MessageOccurrenceSpecification messageOccurrenceReceive = createMessageOccurrence(interaction, umlMessage, receiverLifeline, messageLabel + "ReceiveEvent");

        umlMessage.setSendEvent(messageOccurrenceSend);
        umlMessage.setReceiveEvent(messageOccurrenceReceive);

        final boolean senderAndReceiverAreEqual = senderLifeline.equals(receiverLifeline); // this might happen if an object/class calls itself.

        if (messageSort.equals(MessageSort.SYNCH_CALL_LITERAL) && !senderAndReceiverAreEqual) {
            openBehaviourSpecification(interaction, receiverLifeline, messageOccurrenceReceive);
        }
        if (messageSort.equals(MessageSort.REPLY_LITERAL) && !senderAndReceiverAreEqual) {
            closeBehaviourSpecification(senderLifeline, messageOccurrenceSend);
        }

    }

    private static void closeBehaviourSpecification(final org.eclipse.uml2.uml.Lifeline senderLifeline, final MessageOccurrenceSpecification messageOccurrenceSend) {
        final List<BehaviorExecutionSpecification> list = senderLifeline.getCoveredBys().stream()
                .filter(cb -> cb instanceof BehaviorExecutionSpecification)
                .map(cb -> (BehaviorExecutionSpecification) cb)
                .filter(bes -> isNull(bes.getFinish()))
                .toList();
        if (list.isEmpty()) {
            throw new RuntimeException("There is no open BehaviorExecutionSpecification. Exactly one was expected.");
        }
        if (list.size() > 1) {
            throw new RuntimeException(String.format("There is more than one open BehaviourExecutionSpecification, this is unexpected pleas check the running code.\nsize: %s\nlist: %s", list.size(), list.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]"))));
        }

        list.get(0).setFinish(messageOccurrenceSend);
    }

    /**
     * MessageSort is an enumeration of different kinds of messages.
     * This enumeration determines if it is a call or a reply.
     * This method only expects there to be 2 types SYNC_CALL or REPLY
     * {@link MessageSort}
     * @param message the kieker trace message, two types are considered {@link SynchronousCallMessage} and {@link SynchronousReplyMessage} if neither are matched an exception is thrown.
     * @return MessageSort Literal
     */
    private static MessageSort getMessageSort(final AbstractMessage message) {
        if (message instanceof SynchronousCallMessage) {
            return MessageSort.SYNCH_CALL_LITERAL;
        }
        if (message instanceof SynchronousReplyMessage) {
            return MessageSort.REPLY_LITERAL;
        }
        throw new RuntimeException("Unexpected message type of: " + message.getClass());

    }


    private static void openBehaviourSpecification(final Interaction interaction, final org.eclipse.uml2.uml.Lifeline umlLifeline, final MessageOccurrenceSpecification messageOccurrenceReceive) {
        final BehaviorExecutionSpecification behaviour = (BehaviorExecutionSpecification) interaction.createFragment("BehaviorExecutionSpecification" + umlLifeline.getLabel(), E_CLASS_BEHAVIOUR_EXECUTION);
        behaviour.getCovereds().add(umlLifeline);

        behaviour.setStart(messageOccurrenceReceive);
    }

    private static MessageOccurrenceSpecification createMessageOccurrence(final Interaction interaction, final org.eclipse.uml2.uml.Message umlMessage, final org.eclipse.uml2.uml.Lifeline lifeline, final String label) {
        final MessageOccurrenceSpecification fragment = (MessageOccurrenceSpecification) interaction.createFragment(label, E_CLASS_MESSAGE_OCCURRENCE);

        fragment.getCovereds().add(lifeline);
        fragment.setMessage(umlMessage);
        return fragment;
    }

    private static String getMessageLabel(final AbstractMessage me) {
        final Signature sig = me.getReceivingExecution().getOperation().getSignature();
        final String params = String.join(", ", sig.getParamTypeList());
        return sig.getName() + '(' + params + ')';
    }

}
