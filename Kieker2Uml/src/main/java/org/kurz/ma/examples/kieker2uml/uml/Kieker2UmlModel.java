package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.MessageTrace;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UseCase;

import java.nio.file.Paths;
import java.util.Optional;

import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.getTraceRepresentation;
import static org.kurz.ma.examples.kieker2uml.uml.UmlInteractions.addTraceId;
import static org.kurz.ma.examples.kieker2uml.uml.UmlInteractions.createInteraction;
import static org.kurz.ma.examples.kieker2uml.uml.UmlInteractions.getInteraction;
import static org.kurz.ma.examples.kieker2uml.uml.UmlInteractions.getInteractionName;

public class Kieker2UmlModel {

    public static void main(String[] args) {
        Model m = Kieker2UmlUtil.loadModel(Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams.uml"));
        System.out.println(m.getName());
        Kieker2UmlUtil.saveModel(m, Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams2.uml"));
    }

    public static void addInteractionToUseCase(final Model model, final MessageTrace messageTrace, final String useCaseName) {
        final UseCase useCase = UmlUseCases.getUseCase(model, useCaseName);
        final String traceRepresentation = getTraceRepresentation(messageTrace);

        final Optional<Interaction> interaction = getInteraction(useCase, traceRepresentation);
        if (interaction.isEmpty()) { // create Interaction
            final Interaction newInteraction = createInteraction(getInteractionName(useCase), messageTrace);
            MarteSupport.applyPerformanceStereotypesToInteraction(newInteraction, messageTrace);
            useCase.getOwnedBehaviors().add(newInteraction);
        } else { // update Interaction
            MarteSupport.applyPerformanceStereotypesToInteraction(interaction.get(), messageTrace);
            addTraceId(interaction.get(), messageTrace);
        }
    }

    public static void addStaticViewToModel(final Model model, final MessageTrace trace) {
        UmlClasses.addClasses(model, trace);
        UmlComponents.addComponents(model, trace);
    }

}
