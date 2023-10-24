package org.kurz.ma.examples.kieker2uml.uml;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UseCase;

public class UmlUseCases {

    public static final EClass USE_CASE_E_CLASS = UMLFactory.eINSTANCE.createUseCase().eClass();

    static UseCase getUseCase(final Model model, final String useCaseName) {
        return model.getPackagedElements().stream()
                .filter(pe -> pe instanceof UseCase)
                .map(uc -> (UseCase) uc)
                .filter(uc -> useCaseName.equals(uc.getName()))
                .findFirst()
                .orElseGet(() -> (UseCase) model.createPackagedElement(useCaseName, USE_CASE_E_CLASS));
    }

}
