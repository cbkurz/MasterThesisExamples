package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.MessageTrace;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UseCase;

import java.util.Objects;

import static org.kurz.ma.examples.kieker2uml.uml.Uml2Interactions.addLifelines;

public class Uml2UseCases {

    public static final EClass USE_CASE_E_CLASS = UMLFactory.eINSTANCE.createUseCase().eClass();
    public static final EClass INTERACTION_E_CLASS = UMLFactory.eINSTANCE.createInteraction().eClass();

    static void addUseCase(final Model model, final MessageTrace messageTrace, final String useCaseName) {
        final UseCase useCase = getUseCase(model, useCaseName);
        final String traceName = "trace-" + messageTrace.getTraceId();
        Behavior trace = useCase.getOwnedBehavior(traceName);
        if (Objects.nonNull(trace)) {
            return; // trace has already been added to the use-case and needs no further execution
        }
        final Interaction myInteraction = (Interaction) useCase.createOwnedBehavior(traceName, INTERACTION_E_CLASS);
        addLifelines(myInteraction, messageTrace.getSequenceAsVector());
    }

    private static UseCase getUseCase(final Model model, final String useCaseName) {
        return model.getPackagedElements().stream()
                .filter(pe -> pe instanceof UseCase)
                .map(uc -> (UseCase) uc)
                .filter(uc -> useCaseName.equals(uc.getName()))
                .findFirst()
                .orElseGet(() -> (UseCase) model.createPackagedElement(useCaseName, USE_CASE_E_CLASS));
    }

}
