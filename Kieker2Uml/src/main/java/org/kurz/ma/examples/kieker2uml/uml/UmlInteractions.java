package org.kurz.ma.examples.kieker2uml.uml;

import kieker.common.util.signature.Signature;
import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.AssemblyComponent;
import kieker.model.system.model.MessageTrace;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.kurz.ma.examples.kieker2uml.filter.Util;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

class UmlInteractions {

    public static final EClass MESSAGE_OCCURRENCE_E_CLASS = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification().eClass();
    public static final EClass BEHAVIOUR_EXECUTION_E_CLASS = UMLFactory.eINSTANCE.createBehaviorExecutionSpecification().eClass();

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
    static void addLifelines(final Interaction interaction, final List<AbstractMessage> messages) {
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
        final MessageSort messageSort = Util.getMessageSort(message);
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

        applyPerformanceAnnotations(message, senderLifeline, umlMessage);

    }

    private static void applyPerformanceAnnotations(final AbstractMessage message, final Lifeline senderLifeline, final Message umlMessage) {
        final double execTime = (message.getReceivingExecution().getTout() - message.getReceivingExecution().getTin()) / 1_000_000_000.0; // in seconds
        MarteSupport.setGaStepAsAnnotation(umlMessage, execTime + "", "1");
        MarteSupport.setGaWorkflow(senderLifeline, "closed:2");
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


    private static void openBehaviourSpecification(final Interaction interaction, final org.eclipse.uml2.uml.Lifeline umlLifeline, final MessageOccurrenceSpecification messageOccurrenceReceive) {
        final BehaviorExecutionSpecification behaviour = (BehaviorExecutionSpecification) interaction.createFragment("BehaviorExecutionSpecification" + umlLifeline.getLabel(), BEHAVIOUR_EXECUTION_E_CLASS);
        behaviour.getCovereds().add(umlLifeline);

        behaviour.setStart(messageOccurrenceReceive);
    }

    private static MessageOccurrenceSpecification createMessageOccurrence(final Interaction interaction, final org.eclipse.uml2.uml.Message umlMessage, final org.eclipse.uml2.uml.Lifeline lifeline, final String label) {
        final MessageOccurrenceSpecification fragment = (MessageOccurrenceSpecification) interaction.createFragment(label, MESSAGE_OCCURRENCE_E_CLASS);

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
