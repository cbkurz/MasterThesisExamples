package org.kurz.ma.examples.kieker2uml.uml;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.OccurrenceSpecification;
import org.eclipse.uml2.uml.UMLFactory;
import org.kurz.ma.examples.kieker2uml.model.Lifeline;
import org.kurz.ma.examples.kieker2uml.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

class Uml2Interactions {

    public static final EClass E_CLASS_MESSAGE_OCCURRENCE = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification().eClass();
    public static final EClass E_CLASS_BEHAVIOUR_EXECUTION = UMLFactory.eINSTANCE.createBehaviorExecutionSpecification().eClass();

    static void addInteractionToModel(final Model model, final String interactionName, final Set<Lifeline> lifelines) {
        final EClass eClass = UMLFactory.eINSTANCE.createInteraction().eClass();
        final Interaction interaction = (Interaction) model.createPackagedElement(interactionName, eClass);

        final List<org.eclipse.uml2.uml.Lifeline> umlLifelines = addLifelines(lifelines, interaction);

        lifelines.stream().flatMap(l -> l.getMessagesOutgoing().stream()).toList().forEach(m -> createMessage(interaction, umlLifelines, m));
//        lifelines.forEach(l -> createBehaviourOccurrence(interaction, l));
    }

    private static void createBehaviourOccurrence(final Interaction interaction, final Lifeline lifeline) {
        final BehaviorExecutionSpecification behaviour = (BehaviorExecutionSpecification) interaction.createFragment("BehaviorExecutionSpecification" + lifeline.getLabel(), E_CLASS_BEHAVIOUR_EXECUTION);
        final EList<org.eclipse.uml2.uml.Lifeline> lifelines = interaction.getLifelines();
        final org.eclipse.uml2.uml.Lifeline umlLifeline = getLifeline(lifelines, lifeline);
        behaviour.getCovereds().add(umlLifeline);

        behaviour.setStart(getOccurrenceByMessage(umlLifeline.getCoveredBys(), lifeline.getFirstMessage()));
        behaviour.setFinish(getOccurrenceByMessage(umlLifeline.getCoveredBys(), lifeline.getLastMessage()));
    }

    private static OccurrenceSpecification getOccurrenceByMessage(final EList<InteractionFragment> fragments, final Message message) {
        final String messageLabel = message.getLabel();
        requireNonNull(messageLabel, "The lable of the message requires to be non null. Message: " + message);

        final List<MessageOccurrenceSpecification> list = fragments.stream()
                .filter(f -> f instanceof MessageOccurrenceSpecification)
                .map(f -> (MessageOccurrenceSpecification) f)
                .filter(b -> messageLabel.equals(b.getMessage().getLabel()))
                .toList();

        return list.get(list.size() - 1); // this is just an assumption but the last found message should be the right one.
    }

    private static List<org.eclipse.uml2.uml.Lifeline> addLifelines(final Set<Lifeline> lifelines, final Interaction interaction) {
        final List<org.eclipse.uml2.uml.Lifeline> umlLifelines = new ArrayList<>();
        lifelines.forEach(l -> {
            final org.eclipse.uml2.uml.Lifeline lifeline = interaction.createLifeline(l.getLabel());
            lifeline.setName(l.getLabel()); // TODO: redundant?
            umlLifelines.add(lifeline);
        });
        return umlLifelines;
    }

    private static void createMessage(final Interaction interaction, final List<org.eclipse.uml2.uml.Lifeline> umlLifelines, final Message m) {
        final org.eclipse.uml2.uml.Message umlMessage = interaction.createMessage(m.getLabel());
        final org.eclipse.uml2.uml.Lifeline from = getLifeline(umlLifelines, m.getFrom());
        final org.eclipse.uml2.uml.Lifeline to = getLifeline(umlLifelines, m.getTo());


        umlMessage.setSendEvent(createMessageOccurance(interaction, umlMessage, from, m.getLabel() + "SendEvent"));
        umlMessage.setReceiveEvent(createMessageOccurance(interaction, umlMessage, to, m.getLabel() + "ReceiveEvent"));
    }

    private static MessageOccurrenceSpecification createMessageOccurance(final Interaction interaction, final org.eclipse.uml2.uml.Message umlMessage, final org.eclipse.uml2.uml.Lifeline lifeline, final String label) {
        final MessageOccurrenceSpecification fragment = (MessageOccurrenceSpecification) interaction.createFragment(label, E_CLASS_MESSAGE_OCCURRENCE);

        fragment.getCovereds().add(lifeline);
        fragment.setMessage(umlMessage);
        return fragment;
    }

    private static org.eclipse.uml2.uml.Lifeline getLifeline(final List<org.eclipse.uml2.uml.Lifeline> umlLifelines, final Lifeline m) {
        return umlLifelines.stream().filter(l -> l.getLabel().equals(m.getLabel())).findFirst().orElseThrow();
    }
}
