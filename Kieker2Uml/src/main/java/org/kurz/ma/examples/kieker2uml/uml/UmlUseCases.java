package org.kurz.ma.examples.kieker2uml.uml;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.UseCase;

import static java.util.Objects.requireNonNull;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.createAssociation;

public class UmlUseCases {

    public static final EClass USE_CASE_E_CLASS = UMLFactory.eINSTANCE.createUseCase().eClass();
    public static final String KIEKER_ENTRY_NAME = "'Entry'";

    static UseCase getUseCase(final Model model, final String useCaseName) {
        final Package dynamicView = getDynamicView(model);
        final UseCase useCase = dynamicView.getPackagedElements().stream()
                .filter(pe -> pe instanceof UseCase)
                .map(uc -> (UseCase) uc)
                .filter(uc -> useCaseName.equals(uc.getName()))
                .findFirst()
                .orElseGet(() -> (UseCase) dynamicView.createPackagedElement(useCaseName, USE_CASE_E_CLASS));
        final Actor actor = getActor(dynamicView);
        createAssociation(useCase, actor);
        return useCase;
    }

    static Actor getActor(final Package dynamicView) {
        requireNonNull(dynamicView, "dynamicView");
        return dynamicView.getPackagedElements().stream()
                .filter(e -> UMLPackage.Literals.ACTOR.equals(e.eClass()))
                .filter(e -> KIEKER_ENTRY_NAME.equals(((Actor) e).getName()))
                .map(e -> (Actor) e)
                .findFirst()
                .orElseGet(() -> (Actor) dynamicView.createPackagedElement(KIEKER_ENTRY_NAME, UMLPackage.Literals.ACTOR));
    }

    static Package getDynamicView(final Model model) {
        requireNonNull(model, "model");
        return model.getPackagedElements().stream().filter(pe -> pe.getName().equals("dynamicView")).findFirst().map(pe -> (Package) pe).orElseGet(() -> (Package) model.createPackagedElement("dynamicView", UMLPackage.Literals.PACKAGE));
    }

}
