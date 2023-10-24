package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.AssemblyComponent;
import kieker.model.system.model.MessageTrace;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.getRepresentation;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.setRepresentation;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.setReferenceAnnotation;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.setReferenceAnnotations;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.setRepresentationCount;

class UmlInteractions {
    public static final EClass INTERACTION_E_CLASS = UMLFactory.eINSTANCE.createInteraction().eClass();
    private static final Logger LOGGER = LoggerFactory.getLogger(UmlInteractions.class);
    public static final EClass MESSAGE_OCCURRENCE_E_CLASS = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification().eClass();
    public static final EClass BEHAVIOUR_EXECUTION_E_CLASS = UMLFactory.eINSTANCE.createBehaviorExecutionSpecification().eClass();
    public static final String TRACE_IDS_SET_NAME = "AppliedTraceIds";

    static Interaction createInteraction(final String interactionName, final MessageTrace messageTrace) {
        final Interaction interaction = UMLFactory.eINSTANCE.createInteraction();

        interaction.setName(interactionName);
        addLifelines(interaction, messageTrace.getSequenceAsVector()); // adds lifelines and messages
        Kieker2UmlUtil.addTraceId(interaction, messageTrace);
        setRepresentation(interaction, Kieker2UmlUtil.getTraceRepresentation(messageTrace));

        return interaction;
    }

    /**
     * @param useCase             The UseCase to which this Interaction will be added.
     * @param traceRepresentation The traceRepresentation for the Interaction so it can be correctly identified.
     * @return The Interaction, either an existing one or the newly created one. The Interaction will have the traceRepresentation set in the id-Annotation.
     */
    static Optional<Interaction> getInteraction(final UseCase useCase, final String traceRepresentation) {
        requireNonNull(useCase, "useCase");
        requireNonNull(traceRepresentation, "traceRepresentation");

        return useCase.getOwnedBehaviors().stream()
                .filter(i -> i instanceof Interaction)
                .map(i -> (Interaction) i)
                .filter(i -> getRepresentation(i).map(s -> s.equals(traceRepresentation)).orElseGet(() -> false))
                .findFirst();
    }

    /**
     * The Name is used as File-Name later in the process and simply is "Interaction-" + the count of the interaction.
     * count starts with zero.
     *
     * @param useCase The MessageTrace that is used to create the name.
     * @return The Name of the Trace beginning with "Interaction-" + COUNT
     */
    static String getInteractionName(final UseCase useCase) {
        return "Interaction-" + useCase.getOwnedBehaviors().size();
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
        int count = 0; // The count was introduced to have an additional separation option for Messages that have the same representation
        for (final AbstractMessage message : messages) {
            final AssemblyComponent senderComponent = message.getSendingExecution().getAllocationComponent().getAssemblyComponent();
            final AssemblyComponent receiverComponent = message.getReceivingExecution().getAllocationComponent().getAssemblyComponent();

            // getLifeline(name, ignoreCase, createOnDemand) <-- naming of the parameters
            final org.eclipse.uml2.uml.Lifeline senderLifeline = interaction.getLifeline(senderComponent.getIdentifier(), false, true);
            final org.eclipse.uml2.uml.Lifeline receiverLifeline = interaction.getLifeline(receiverComponent.getIdentifier(), false, true);

            setReferenceAnnotations(senderLifeline, message.getSendingExecution());
            setReferenceAnnotations(receiverLifeline, message.getReceivingExecution());

            final String messageId = Kieker2UmlUtil.getMessageRepresentation(message);
            createMessage(interaction, message, senderLifeline, receiverLifeline, messageId, count);
            count++;
        }
    }

    private static void createMessage(final Interaction interaction, final AbstractMessage message, final Lifeline senderLifeline, final Lifeline receiverLifeline, final String messageId, final int count) {
        requireNonNull(interaction, "interaction");
        requireNonNull(message, "message");
        requireNonNull(senderLifeline, "senderLifeline");
        requireNonNull(receiverLifeline, "receiverLifeline");

        final String messageLabel = Kieker2UmlUtil.getMessageLabel(message.getReceivingExecution());
        final org.eclipse.uml2.uml.Message umlMessage = interaction.createMessage(messageLabel);
        final MessageSort messageSort = Kieker2UmlUtil.getMessageSort(message);
        umlMessage.setMessageSort(messageSort);

        final MessageOccurrenceSpecification messageOccurrenceSend = createMessageOccurrence(interaction, umlMessage, senderLifeline, messageLabel + "SendEvent");
        final MessageOccurrenceSpecification messageOccurrenceReceive = createMessageOccurrence(interaction, umlMessage, receiverLifeline, messageLabel + "ReceiveEvent");

        umlMessage.setSendEvent(messageOccurrenceSend);
        umlMessage.setReceiveEvent(messageOccurrenceReceive);

        final boolean senderAndReceiverAreEqual = senderLifeline.equals(receiverLifeline); // this might happen if an object/class calls itself.

        if (messageSort.equals(MessageSort.SYNCH_CALL_LITERAL) && !senderAndReceiverAreEqual) {
            final BehaviorExecutionSpecification bes = openBehaviourSpecification(interaction, receiverLifeline, messageOccurrenceReceive);
            setRepresentation(bes, Kieker2UmlUtil.getBESRepresentation(messageId));
            setRepresentationCount(bes, count);
            setReferenceAnnotation(bes, "OpenMessage", messageId);
        }
        if (messageSort.equals(MessageSort.REPLY_LITERAL) && !senderAndReceiverAreEqual) {
            final BehaviorExecutionSpecification bes = closeBehaviourSpecification(senderLifeline, messageOccurrenceSend);
            setReferenceAnnotation(bes, "CloseMessage", messageId);
            setReferenceAnnotation(bes, "CloseMessageCount", count + "");
        }

        // set Metadata
        // uml message
        setRepresentation(umlMessage, messageId);
        setRepresentationCount(umlMessage, count);
        setReferenceAnnotations(umlMessage, message.getReceivingExecution());
        // message occurrence send
        setRepresentation(messageOccurrenceSend, Kieker2UmlUtil.getSendMOSRepresentation(messageId));
        setRepresentationCount(messageOccurrenceSend, count);
        setReferenceAnnotations(messageOccurrenceSend, message.getSendingExecution());
        // message occurrence receive
        setRepresentation(messageOccurrenceReceive, Kieker2UmlUtil.getReceiveMOSRepresentation(messageId));
        setRepresentationCount(messageOccurrenceReceive, count);
        setReferenceAnnotations(messageOccurrenceReceive, message.getReceivingExecution());
    }

    private static BehaviorExecutionSpecification closeBehaviourSpecification(final Lifeline senderLifeline, final MessageOccurrenceSpecification messageOccurrenceSend) {
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
        return list.get(0);
    }


    private static BehaviorExecutionSpecification openBehaviourSpecification(final Interaction interaction, final Lifeline umlLifeline, final MessageOccurrenceSpecification messageOccurrenceReceive) {
        final BehaviorExecutionSpecification behaviour = (BehaviorExecutionSpecification) interaction.createFragment("BehaviorExecutionSpecification" + umlLifeline.getLabel(), BEHAVIOUR_EXECUTION_E_CLASS);
        behaviour.getCovereds().add(umlLifeline);

        behaviour.setStart(messageOccurrenceReceive);
        return behaviour;
    }

    private static MessageOccurrenceSpecification createMessageOccurrence(final Interaction interaction, final org.eclipse.uml2.uml.Message umlMessage, final org.eclipse.uml2.uml.Lifeline lifeline, final String label) {
        final MessageOccurrenceSpecification fragment = (MessageOccurrenceSpecification) interaction.createFragment(label, MESSAGE_OCCURRENCE_E_CLASS);

        fragment.getCovereds().add(lifeline);
        fragment.setMessage(umlMessage);
        return fragment;
    }

}
