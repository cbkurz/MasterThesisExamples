package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.MessageTrace;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.getTraceRepresentation;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.isTraceApplied;
import static org.kurz.ma.examples.kieker2uml.uml.UmlInteractions.createInteraction;
import static org.kurz.ma.examples.kieker2uml.uml.UmlInteractions.getInteraction;
import static org.kurz.ma.examples.kieker2uml.uml.UmlInteractions.getInteractionName;

public class Kieker2UmlModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(Kieker2UmlModel.class);

    public static void main(String[] args) {
        Model m = Kieker2UmlUtil.loadModel(Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams.uml"));
        System.out.println(m.getName());
        Kieker2UmlUtil.saveModel(m, Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams2.uml"));
    }

    public static void addInteractionToUseCase(final Model model, final MessageTrace messageTrace, final String useCaseName) {
        final UseCase useCase = UmlUseCases.getUseCase(model, useCaseName);
        MarteSupport.applyGaScenario(useCase);
        final String traceRepresentation = getTraceRepresentation(messageTrace);

        final Optional<Interaction> interaction = getInteraction(useCase, traceRepresentation);
        if (interaction.isEmpty()) { // create Interaction
            LOGGER.info("Creating interaction for Trace: " + messageTrace.getTraceId());
            final Interaction newInteraction = createInteraction(getInteractionName(useCase), messageTrace);
            MarteSupport.applyPerformanceStereotypesToInteraction(newInteraction, messageTrace);
            useCase.getOwnedBehaviors().add(newInteraction);
            UmlInteractions.connectEntryLifelineToActor(useCase);
        } else if (isTraceApplied(interaction.get(), messageTrace.getTraceId())) { // update Interaction
            LOGGER.info("Interaction was created before, performance information will now be added to Trace with id: " + messageTrace.getTraceId());
            MarteSupport.applyPerformanceStereotypesToInteraction(interaction.get(), messageTrace);
        } else {
            LOGGER.info(String.format("Trace with id '%s' was applied before and is therefore skipped.", messageTrace.getTraceId()));
        }
    }

    public static void addStaticViewToModel(final Model model, final MessageTrace trace) {
        UmlClasses.addClasses(model, trace);
        UmlComponents.addComponents(model, trace);
        final List<Node> nodeList = model.allOwnedElements().stream().filter(pe -> pe instanceof Node).map(pe -> (Node) pe).toList();
        MarteSupport.applyPerformanceStereotypesToNodes(nodeList);
    }

}
