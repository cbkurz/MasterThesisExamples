package org.kurz.ma.examples.kieker2uml.uml;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.kurz.ma.examples.kieker2uml.model.Lifeline;
import org.kurz.ma.examples.kieker2uml.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Uml2Interactions {
    static void addInteractionToModel(final Model model, final String interactionName, final Set<Lifeline> lifelines) {
        final EClass eClass = UMLFactory.eINSTANCE.createInteraction().eClass();
        final Interaction interaction = (Interaction) model.createPackagedElement(interactionName, eClass);

        final List<org.eclipse.uml2.uml.Lifeline> umlLifelines = addLifelines(lifelines, interaction);

        lifelines.stream().flatMap(l -> l.getMessagesOutgoing().stream()).toList().forEach(m -> createMessage(interaction, umlLifelines, m));
        lifelines.stream().flatMap(l -> l.getMessagesIncoming().stream()).toList().forEach(m -> createMessage(interaction, umlLifelines, m));
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

        createMessageOccurance(interaction, umlMessage, from, m.getLabel() + "SendEvent");
        createMessageOccurance(interaction, umlMessage, to, m.getLabel() + "ReceiveEvent");
    }

    private static void createMessageOccurance(final Interaction interaction, final org.eclipse.uml2.uml.Message umlMessage, final org.eclipse.uml2.uml.Lifeline lifeline, final String label) {
        final MessageOccurrenceSpecification fragment = (MessageOccurrenceSpecification) interaction.createFragment(label, UMLFactory.eINSTANCE.createMessageOccurrenceSpecification().eClass());

        fragment.getCovereds().add(lifeline);
        fragment.setMessage(umlMessage);
    }

    private static org.eclipse.uml2.uml.Lifeline getLifeline(final List<org.eclipse.uml2.uml.Lifeline> umlLifelines, final Lifeline m) {
        return umlLifelines.stream().filter(l -> l.getLabel().equals(m.getLabel())).findFirst().orElseThrow();
    }
}
