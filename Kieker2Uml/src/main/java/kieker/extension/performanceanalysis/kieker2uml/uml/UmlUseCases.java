package kieker.extension.performanceanalysis.kieker2uml.uml;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.UseCase;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class UmlUseCases {

    public static final EClass USE_CASE_E_CLASS = UMLFactory.eINSTANCE.createUseCase().eClass();
    public static final String KIEKER_ENTRY_NAME = "'Entry'";

    static UseCase getUseCase(final Model model, final String useCaseName, final String traceRepresentation) {
        final Package dynamicView = getDynamicView(model);
        final UseCase useCase = getUseCase(useCaseName, dynamicView, traceRepresentation);
        final Actor actor = getActor(dynamicView, useCase);
        Kieker2UmlUtil.createAssociation(useCase, actor);
        return useCase;
    }

    private static UseCase getUseCase(final String useCaseName, final Package dynamicView, final String traceRepresentation) {
        final List<UseCase> ucList = dynamicView.getPackagedElements().stream()
                .filter(pe -> pe instanceof UseCase)
                .map(uc -> (UseCase) uc)
                .filter(uc -> uc.getName().contains(useCaseName))
                .toList();

        return ucList.stream()
                .filter(uc -> Kieker2UmlUtil.getRepresentation(uc).map(r -> r.equals(traceRepresentation)).orElse(false))
                .findFirst()
                .orElseGet(() -> {
                    final UseCase useCase = (UseCase) dynamicView.createPackagedElement(useCaseName + "-" + ucList.size(), USE_CASE_E_CLASS);
                    Kieker2UmlUtil.setRepresentation(useCase, traceRepresentation);
                    return useCase;
                });
    }

    static Actor getActor(final Package dynamicView, final UseCase useCase) {
        requireNonNull(dynamicView, "dynamicView");
        final String actorName = KIEKER_ENTRY_NAME + "-" + useCase.getName();
        return dynamicView.getPackagedElements().stream()
                .filter(e -> UMLPackage.Literals.ACTOR.equals(e.eClass()))
                .filter(e -> actorName.equals(e.getName()))
                .filter(a -> Kieker2UmlUtil.isAssociateWith(a, useCase))
                .map(e -> (Actor) e)
                .findFirst()
                .orElseGet(() -> (Actor) dynamicView.createPackagedElement(actorName, UMLPackage.Literals.ACTOR));
    }

    static Package getDynamicView(final Model model) {
        requireNonNull(model, "model");
        return model.getPackagedElements().stream().filter(pe -> pe.getName().equals("dynamicView")).findFirst().map(pe -> (Package) pe).orElseGet(() -> (Package) model.createPackagedElement("dynamicView", UMLPackage.Literals.PACKAGE));
    }

}
