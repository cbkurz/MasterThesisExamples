package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.MessageTrace;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UseCase;

import java.util.Objects;

import static java.util.Objects.isNull;
import static org.kurz.ma.examples.kieker2uml.uml.UmlInteractions.addLifelines;

public class UmlUseCases {

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
        linkInteractionWithComponent(model, myInteraction, "myComponent"); // TODO: for now there are only dummy components.
        addLifelines(myInteraction, messageTrace.getSequenceAsVector());
    }

    private static void linkInteractionWithComponent(final Model model, final Interaction myInteraction, final String myComponent) {
        final Package staticView = (Package) model.getPackagedElement("staticView");
        if (isNull(staticView)) {
            throw new RuntimeException("StaticView cannot be null when the interaction and a component shall be linked.");
        }
        final Component component = (Component) staticView.getPackagedElements().stream().filter(pe -> myComponent.equals(pe.getName())).findFirst().orElseThrow(() -> new RuntimeException("Component must be present."));
        final String s = myComponent + "-property";
        final Property property = myInteraction.createOwnedAttribute(s, component);
        myInteraction.getOwnedAttributes().stream().filter(a -> s.equals(a.getName())).findFirst().orElseThrow(RuntimeException::new);
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
